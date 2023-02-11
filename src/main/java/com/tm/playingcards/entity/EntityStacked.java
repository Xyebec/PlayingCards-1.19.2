package com.tm.playingcards.entity;

import com.mojang.math.Vector3d;
import com.tm.playingcards.init.ModDataSerializers;
import com.tm.playingcards.util.ArrayHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class EntityStacked extends Entity {
    protected static final EntityDataAccessor<Byte[]> STACK = SynchedEntityData.defineId(EntityStacked.class, ModDataSerializers.STACK);

    public EntityStacked(EntityType<? extends EntityStacked> type, Level world) {
        super(type, world);
    }

    public EntityStacked(EntityType<? extends EntityStacked> type, Level world, Vector3d position) {
        this(type, world);
        setPos(position.x, position.y, position.z);
    }

    public int getStackSize() {
        return entityData.get(STACK).length;
    }

    public byte getTopStackId() {
        return getIdAt(getStackSize() - 1);
    }

    public byte getIdAt(int index) {
        if (index >= 0 && index < getStackSize()) {
            return entityData.get(STACK)[index];
        }

        return 0;
    }

    public void removeFromTop() {
        Byte[] newStack = new Byte[getStackSize() - 1];

        for (int index = 0; index < newStack.length; index++) {
            newStack[index] = entityData.get(STACK)[index];
        }

        entityData.set(STACK, newStack);
    }

    public void addToTop(byte id) {
        Byte[] newStack = new Byte[getStackSize() + 1];

        for (int index = 0; index < getStackSize(); index++) {
            newStack[index] = entityData.get(STACK)[index];
        }

        newStack[newStack.length - 1] = id;

        entityData.set(STACK, newStack);
    }

    public void createEmptyStack() {
        Byte[] newStack = new Byte[0];
        entityData.set(STACK, newStack);
    }

    public void shuffleStack() {
        Byte[] newStack = new Byte[getStackSize()];

        for (int index = 0; index < getStackSize(); index++) {
            newStack[index] = entityData.get(STACK)[index];
        }

        ArrayHelper.shuffle(newStack);

        entityData.set(STACK, newStack);
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            noPhysics = false;
        } else {
            noPhysics = !level.noCollision(this);

            if (noPhysics) {
                setDeltaMovement(getDeltaMovement().add(0.0D, 0.02D, 0.0D));
            } else {
                setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }
        }

        move(MoverType.SELF, getDeltaMovement());

        Vec3 pos = position();
        double size = 0.2D;
        double addAmount = 0.0045D;

        setBoundingBox(new AABB(pos.x - size, pos.y, pos.z - size, pos.x + size, pos.y + 0.03D + (addAmount * getStackSize()), pos.z + size));
    }

    public abstract void moreData();

    @Override
    protected void defineSynchedData() {
        entityData.define(STACK, new Byte[0]);
        moreData();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        entityData.set(STACK, ArrayHelper.toObject(nbt.getByteArray("Stack")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putByteArray("Stack", ArrayHelper.toPrimitive(entityData.get(STACK)));
    }

    @Override
    public boolean isPickable() {
        return true;
    }
}
