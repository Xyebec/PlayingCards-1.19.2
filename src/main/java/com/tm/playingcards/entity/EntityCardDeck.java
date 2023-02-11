package com.tm.playingcards.entity;

import com.mojang.math.Vector3d;
import com.tm.playingcards.init.ModEntityTypes;
import com.tm.playingcards.init.ModItems;
import com.tm.playingcards.util.CardHelper;
import com.tm.playingcards.util.ItemHelper;
import net.minecraft.ChatFormatting;
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
import net.minecraftforge.network.NetworkHooks;

public class EntityCardDeck extends EntityStacked {
    private static final EntityDataAccessor<Byte> SKIN_ID = SynchedEntityData.defineId(EntityCardDeck.class, EntityDataSerializers.BYTE);

    public EntityCardDeck(EntityType<? extends EntityCardDeck> type, Level world) {
        super(type, world);
    }

    public EntityCardDeck(Level world, Vector3d position, float rotation, byte skinId) {
        super(ModEntityTypes.CARD_DECK.get(), world, position);
        setYRot(rotation);

        createAndFillDeck();
        shuffleStack();

        entityData.set(SKIN_ID, skinId);
    }

    public byte getSkinId() {
        return entityData.get(SKIN_ID);
    }

    private void createAndFillDeck() {
        Byte[] newStack = new Byte[CardHelper.MAX_STACK_SIZE];

        for (byte i = 0; i < CardHelper.MAX_STACK_SIZE; i++) {
            newStack[i] = i;
        }

        entityData.set(STACK, newStack);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResult.FAIL;

        if (getStackSize() > 0) {
            int cardId = getTopStackId();

            ItemStack card = new ItemStack(ModItems.CARD_COVERED.get());

            card.setDamageValue(cardId);
            ItemHelper.getNBT(card).putUUID("UUID", getUUID());
            ItemHelper.getNBT(card).putByte("SkinID", entityData.get(SKIN_ID));

            if (!level.isClientSide) {
                ItemHelper.spawnStackAtEntity(level, player, card);
            }

            removeFromTop();

            return player.getMainHandItem().isEmpty() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        } else if (level.isClientSide) {
            player.displayClientMessage(Component.translatable("message.stack_empty").withStyle(ChatFormatting.RED), true);
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!(source.getEntity() instanceof Player))
            return false;

        Player player = (Player)source.getEntity();

        if (!player.isCrouching()) {
            shuffleStack();
            player.displayClientMessage(Component.translatable("message.stack_shuffled").withStyle(ChatFormatting.GREEN), true);
        } else {
            ItemStack deck = new ItemStack(ModItems.CARD_DECK.get());
            ItemHelper.getNBT(deck).putByte("SkinID", entityData.get(SKIN_ID));

            if (!level.isClientSide) {
                ItemHelper.spawnStackAtEntity(level, player, deck);
            }

            remove(RemovalReason.DISCARDED);
        }

        return true;
    }

    @Override
    public void moreData() {
        entityData.define(SKIN_ID, (byte) 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        entityData.set(SKIN_ID, nbt.getByte("SkinID"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("SkinID", entityData.get(SKIN_ID));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
