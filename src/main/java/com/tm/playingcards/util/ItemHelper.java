package com.tm.playingcards.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemHelper {
    public static CompoundTag getNBT(ItemStack stack) {
        if (stack.getTag() == null) {
            stack.setTag(new CompoundTag());
        }

        return stack.getTag();
    }

    public static void spawnStackAtEntity(Level world, Entity entity, ItemStack stack) {
        spawnStack(world, entity.getX(), entity.getY(), entity.getZ(), stack);
    }

    private static void spawnStack(Level world, double x, double y, double z, ItemStack stack) {
        ItemEntity item = new ItemEntity(world, x, y, z, stack);
        item.setNoPickUpDelay();
        item.setDeltaMovement(0, 0, 0);
        world.addFreshEntity(item);
    }
}
