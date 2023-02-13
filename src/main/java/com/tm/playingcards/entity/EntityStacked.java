package com.tm.playingcards.entity;

import com.mojang.math.Vector3d;
import com.tm.playingcards.init.ModDataSerializers;
import com.tm.playingcards.util.ArrayHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
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

        Vec3 velocity = this.getDeltaMovement();

        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }

        if (this.level.isClientSide) {
            this.noPhysics = false;
        } else {
            this.noPhysics = !this.level.noCollision(this);
            if (this.noPhysics) {
                this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
            }
        }

        if (!this.onGround || this.getDeltaMovement().horizontalDistanceSqr() > (double)1.0E-5F || (this.tickCount + this.getId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float f1 = 0.98F;
            if (this.onGround) {
                f1 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getFriction(level, new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ()), this) * 0.98F;
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.98D, f1));
            if (this.onGround) {
                Vec3 vec31 = this.getDeltaMovement();
                if (vec31.y < 0.0D) {
                    this.setDeltaMovement(vec31.multiply(1.0D, -0.5D, 1.0D));
                }
            }
        }

        this.hasImpulse |= this.updateInWaterStateAndDoFluidPushing();
        if (!this.level.isClientSide) {
            double d0 = this.getDeltaMovement().subtract(velocity).lengthSqr();
            if (d0 > 0.01D) {
                this.hasImpulse = true;
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(STACK, new Byte[0]);
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
