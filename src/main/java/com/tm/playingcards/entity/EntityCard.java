package com.tm.playingcards.entity;

import com.mojang.math.Vector3d;
import com.tm.playingcards.init.ModEntityTypes;
import com.tm.playingcards.util.CardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;

public class EntityCard extends EntityCardDeck {
    private static final EntityDataAccessor<Optional<UUID>> DECK_UUID = SynchedEntityData.defineId(EntityCard.class, EntityDataSerializers.OPTIONAL_UUID);

    public EntityCard(EntityType<? extends EntityCard> type, Level world) {
        super(type, world);
    }

    public EntityCard(Level world, Vector3d position, float rotation, byte skinId, UUID deckUUID, boolean isCovered, byte cardId) {
        super(ModEntityTypes.CARD.get(), world, position);
        setYRot(rotation);

        createEmptyStack();
        addToTop(cardId);

        entityData.set(SKIN_ID, skinId);
        entityData.set(COVERED, isCovered);
        entityData.set(DECK_UUID, Optional.of(deckUUID));
    }

    public UUID getDeckUUID() {
        return entityData.get(DECK_UUID).isPresent() ? entityData.get(DECK_UUID).get() : null;
    }

    @Override
    protected InteractionResult onTakeCard(Player player, InteractionHand hand) {
        if (getStackSize() <= 0) {
            player.displayClientMessage(Component.translatable("message.stack_empty").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        takeCard(player, getDeckUUID());

        if (getStackSize() <= 0)
            remove(RemovalReason.DISCARDED);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult onShuffle(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    protected boolean onPickUp(Player player) {
        while (getStackSize() > 0) {
            takeCard(player, getDeckUUID());
        }

        remove(RemovalReason.DISCARDED);
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (level.getGameTime() % 20 != 0)
            return;

        if (CardHelper.isNearDeck(level, blockPosition(), getDeckUUID()))
            return;

        remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DECK_UUID, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        entityData.set(DECK_UUID, Optional.of(nbt.getUUID("DeckID")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putUUID("DeckID", getDeckUUID());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
