package com.tm.playingcards.entity;

import com.mojang.math.Vector3d;
import com.tm.playingcards.init.ModEntityTypes;
import com.tm.playingcards.init.ModItems;
import com.tm.playingcards.item.ItemCard;
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

import java.util.UUID;

public class EntityCardDeck extends EntityStacked {
    protected static final EntityDataAccessor<Byte> SKIN_ID = SynchedEntityData.defineId(EntityCardDeck.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> COVERED = SynchedEntityData.defineId(EntityCardDeck.class, EntityDataSerializers.BOOLEAN);

    public EntityCardDeck(EntityType<? extends EntityCardDeck> type, Level world) {
        super(type, world);
    }

    protected EntityCardDeck(EntityType<? extends EntityCardDeck> type, Level world, Vector3d position) {
        super(type, world, position);

    }

    public EntityCardDeck(Level world, Vector3d position, float rotation, byte skinId) {
        this(ModEntityTypes.CARD_DECK.get(), world, position);
        setYRot(rotation);

        createAndFillDeck();
        shuffleStack();

        entityData.set(SKIN_ID, skinId);
    }

    public byte getSkinId() {
        return entityData.get(SKIN_ID);
    }

    public boolean isCovered() {
        return entityData.get(COVERED);
    }

    private void createAndFillDeck() {
        Byte[] newStack = new Byte[CardHelper.MAX_STACK_SIZE];

        for (byte i = 0; i < CardHelper.MAX_STACK_SIZE; i++) {
            newStack[i] = i;
        }

        entityData.set(STACK, newStack);
    }

    protected void takeCard(Player player, UUID deckUUID) {
        if (!level.isClientSide) {
            ItemStack card = entityData.get(COVERED)
                    ? new ItemStack(ModItems.CARD_COVERED.get())
                    : new ItemStack(ModItems.CARD.get());

            int cardId = getTopStackId();
            card.setDamageValue(cardId);
            ItemHelper.getNBT(card).putUUID("UUID", deckUUID);
            ItemHelper.getNBT(card).putByte("SkinID", entityData.get(SKIN_ID));

            ItemHelper.spawnStackAtEntity(level, player, card);
        }

        removeFromTop();
    }

    protected InteractionResult onPutCard(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (getStackSize() >= CardHelper.MAX_STACK_SIZE) {
            player.displayClientMessage(Component.translatable("message.stack_full").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        addToTop((byte) heldItem.getDamageValue());
        heldItem.shrink(1);
        return InteractionResult.SUCCESS;
    }

    protected InteractionResult onTakeCard(Player player, InteractionHand hand) {
        if (getStackSize() <= 0) {
            player.displayClientMessage(Component.translatable("message.stack_empty").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        takeCard(player, getUUID());
        return InteractionResult.SUCCESS;
    }

    protected InteractionResult onShuffle(Player player, InteractionHand hand) {
        shuffleStack();
        player.displayClientMessage(Component.translatable("message.stack_shuffled").withStyle(ChatFormatting.GREEN), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isCrouching())
            return onShuffle(player, hand);

        ItemStack heldItem = player.getItemInHand(hand);
        return heldItem.getItem() instanceof ItemCard
                ? onPutCard(player, hand)
                : onTakeCard(player, hand);
    }

    protected boolean onFlip(Player player) {
        entityData.set(COVERED, !entityData.get(COVERED));
        return true;
    }

    protected boolean onPickUp(Player player) {
        if (!level.isClientSide) {
            ItemStack deck = new ItemStack(ModItems.CARD_DECK.get());
            ItemHelper.getNBT(deck).putByte("SkinID", entityData.get(SKIN_ID));
            ItemHelper.spawnStackAtEntity(level, player, deck);
        }

        remove(RemovalReason.DISCARDED);
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!(source.getEntity() instanceof Player))
            return false;

        Player player = (Player)source.getEntity();

        return player.isCrouching()
                ? onPickUp(player)
                : onFlip(player);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SKIN_ID, (byte) 0);
        entityData.define(COVERED, true);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        entityData.set(SKIN_ID, nbt.getByte("SkinID"));
        entityData.set(COVERED, nbt.getBoolean("Covered"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("SkinID", entityData.get(SKIN_ID));
        nbt.putBoolean("Covered", entityData.get(COVERED));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
