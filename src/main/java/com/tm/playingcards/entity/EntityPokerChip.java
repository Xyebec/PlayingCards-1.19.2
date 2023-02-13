package com.tm.playingcards.entity;

import com.mojang.math.Vector3d;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.tm.playingcards.init.ModEntityTypes;
import com.tm.playingcards.init.ModItems;
import com.tm.playingcards.item.ItemPokerChip;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;

public class EntityPokerChip extends EntityStacked {
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(EntityPokerChip.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<String> OWNER_NAME = SynchedEntityData.defineId(EntityPokerChip.class, EntityDataSerializers.STRING);

    public EntityPokerChip(EntityType<? extends EntityPokerChip> type, Level world) {
        super(type, world);
    }

    public EntityPokerChip(Level world, Vector3d position, UUID ownerId, String ownerName, byte firstChipId) {
        super(ModEntityTypes.POKER_CHIP.get(), world, position);

        createEmptyStack();
        addToTop(firstChipId);
        entityData.set(OWNER_UUID, Optional.of(ownerId));
        entityData.set(OWNER_NAME, ownerName);
    }

    public UUID getOwnerUUID() {
        return (entityData.get(OWNER_UUID).isPresent()) ? entityData.get(OWNER_UUID).get() : null;
    }

    private void takeChip(Player player) {
        byte chipId = getTopStackId();

        if (!level.isClientSide)
            spawnChip(player, ItemPokerChip.getPokerChip(chipId), 1);

        removeFromTop();

        if (getStackSize() <= 0) {
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof ItemPokerChip) {
            CompoundTag nbt = ItemHelper.getNBT(stack);

            if (nbt.hasUUID("OwnerID")) {
                UUID ownerId = nbt.getUUID("OwnerID");

                if (ownerId.equals(getOwnerUUID())) {
                    if (player.isCrouching()) {
                        while (getStackSize() < CardHelper.MAX_STACK_SIZE && stack.getCount() > 0) {
                            ItemPokerChip chip = (ItemPokerChip) stack.getItem();
                            addToTop(chip.getChipId());
                            stack.shrink(1);
                        }
                    } else {
                        if (getStackSize() < CardHelper.MAX_STACK_SIZE) {
                            ItemPokerChip chip = (ItemPokerChip) stack.getItem();
                            addToTop(chip.getChipId());
                            stack.shrink(1);
                        } else {
                            if (level.isClientSide) {
                                player.displayClientMessage(Component.translatable("message.stack_full").withStyle(ChatFormatting.RED), true);
                            }
                        }
                    }
                } else if (level.isClientSide) {
                    player.displayClientMessage(Component.translatable("message.stack_owner_error").withStyle(ChatFormatting.RED), true);
                }
            }
        } else {
            takeChip(player);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player) {
            Player player = (Player) source.getEntity();

            int whiteAmount = 0;
            int redAmount = 0;
            int blueAmount = 0;
            int greenAmount = 0;
            int blackAmount = 0;

            for (int i = 0; i < entityData.get(STACK).length; i++) {
                byte chipId = getIdAt(i);

                if (chipId == 0) whiteAmount++;
                if (chipId == 1) redAmount++;
                if (chipId == 2) blueAmount++;
                if (chipId == 3) greenAmount++;
                if (chipId == 4) blackAmount++;
            }

            if (whiteAmount > 0) spawnChip(player, ModItems.POKER_CHIP_WHITE.get(), whiteAmount);
            if (redAmount > 0)   spawnChip(player, ModItems.POKER_CHIP_RED.get(), redAmount);
            if (blueAmount > 0)  spawnChip(player, ModItems.POKER_CHIP_BLUE.get(), blueAmount);
            if (greenAmount > 0) spawnChip(player, ModItems.POKER_CHIP_GREEN.get(), greenAmount);
            if (blackAmount > 0) spawnChip(player, ModItems.POKER_CHIP_BLACK.get(), blackAmount);

            remove(RemovalReason.DISCARDED);

            return false;
        }

        return true;
    }

    private void spawnChip(Player player, Item item, int amount) {
        if (level.isClientSide)
            return;

        ItemStack chip = new ItemStack(item, amount);
        CompoundTag nbt = ItemHelper.getNBT(chip);
        nbt.putUUID("OwnerID", getOwnerUUID());
        nbt.putString("OwnerName", entityData.get(OWNER_NAME));
        ItemHelper.spawnStackAtEntity(level, player, chip);
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 pos = position();
        double size = 0.1D;
        double addAmount = 0.01575D;

        setBoundingBox(new AABB(pos.x - size, pos.y, pos.z - size, pos.x + size, pos.y + 0.02D + (addAmount * getStackSize()), pos.z + size));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_UUID, Optional.empty());
        entityData.define(OWNER_NAME, "");
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        entityData.set(OWNER_UUID, Optional.of(nbt.getUUID("OwnerID")));
        entityData.set(OWNER_NAME, nbt.getString("OwnerName"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putUUID("OwnerID", getOwnerUUID());
        nbt.putString("OwnerName", entityData.get(OWNER_NAME));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
