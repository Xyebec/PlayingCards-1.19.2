package com.tm.playingcards.entity;

import com.mojang.math.Vector3d;
import com.tm.playingcards.init.ModEntityTypes;
import com.tm.playingcards.init.ModItems;
import com.tm.playingcards.item.ItemCardCovered;
import com.tm.playingcards.util.CardHelper;
import com.tm.playingcards.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityCard extends EntityStacked {
    private static final EntityDataAccessor<Byte> SKIN_ID = SynchedEntityData.defineId(EntityCard.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> DECK_UUID = SynchedEntityData.defineId(EntityCard.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> COVERED = SynchedEntityData.defineId(EntityCard.class, EntityDataSerializers.BOOLEAN);

    public EntityCard(EntityType<? extends EntityCard> type, Level world) {
        super(type, world);
    }

    public EntityCard(Level world, Vector3d position, float rotation, byte skinId, UUID deckUUID, boolean isCovered, byte firstCardId) {
        super(ModEntityTypes.CARD.get(), world, position);
        setYRot(rotation);

        createEmptyStack();
        addToTop(firstCardId);
        entityData.set(SKIN_ID, skinId);
        entityData.set(DECK_UUID, Optional.of(deckUUID));
        entityData.set(COVERED, isCovered);
    }

    public byte getSkinId() {
        return entityData.get(SKIN_ID);
    }

    public UUID getDeckUUID() {
        return entityData.get(DECK_UUID).isPresent() ? entityData.get(DECK_UUID).get() : null;
    }

    public boolean isCovered() {
        return entityData.get(COVERED);
    }

    private void takeCard(Player player) {
        ItemStack card = entityData.get(COVERED)
                ? new ItemStack(ModItems.CARD_COVERED.get())
                : new ItemStack(ModItems.CARD.get());

        card.setDamageValue(getTopStackId());
        ItemHelper.getNBT(card).putUUID("UUID", getDeckUUID());
        ItemHelper.getNBT(card).putByte("SkinID", entityData.get(SKIN_ID));

        if (!level.isClientSide) {
            ItemHelper.spawnStackAtEntity(level, player, card);
        }

        removeFromTop();

        if (getStackSize() <= 0) {
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level.getGameTime() % 20 != 0)
            return;

        BlockPos pos = blockPosition();
        List<EntityCardDeck> closeDecks = level.getEntitiesOfClass(EntityCardDeck.class, new AABB(pos.getX() - 20, pos.getY() - 20, pos.getZ() - 20, pos.getX() + 20, pos.getY() + 20, pos.getZ() + 20));

        boolean foundParentDeck = false;

        for (EntityCardDeck closeDeck : closeDecks) {
            if (getDeckUUID().equals(closeDeck.getUUID())) {
                foundParentDeck = true;
                break;
            }
        }

        if (!foundParentDeck)
            remove(RemovalReason.DISCARDED);

        super.onRemovedFromWorld();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!(stack.getItem() instanceof ItemCardCovered)) {
            takeCard(player);
            return InteractionResult.SUCCESS;
        }

        if (getStackSize() >= CardHelper.MAX_STACK_SIZE) {
            if (level.isClientSide)
                player.displayClientMessage(Component.translatable("message.stack_full").withStyle(ChatFormatting.RED), true);

            return InteractionResult.SUCCESS;
        }

        addToTop((byte) stack.getDamageValue());
        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!(source.getEntity() instanceof Player))
            return false;

        entityData.set(COVERED, !entityData.get(COVERED));
        return true;
    }

    @Override
    public void moreData() {
        entityData.define(SKIN_ID, (byte) 0);
        entityData.define(DECK_UUID, Optional.empty());
        entityData.define(COVERED, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        entityData.set(SKIN_ID, nbt.getByte("SkinID"));
        entityData.set(DECK_UUID, Optional.of(nbt.getUUID("DeckID")));
        entityData.set(COVERED, nbt.getBoolean("Covered"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("SkinID", entityData.get(SKIN_ID));
        nbt.putUUID("DeckID", getDeckUUID());
        nbt.putBoolean("Covered", entityData.get(COVERED));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
