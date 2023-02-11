package com.tm.playingcards.item;

import com.mojang.math.Vector3d;
import com.tm.playingcards.entity.EntityDice;
import com.tm.playingcards.init.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ItemDice extends Item {
    public ItemDice() {
        super(new Item.Properties().tab(ModItems.TAB).stacksTo(5));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        Vec3 pos = player.position();
        EntityDice cardDeck = new EntityDice(world, new Vector3d(pos.x, pos.y, pos.z), player.getYHeadRot());
        world.addFreshEntity(cardDeck);
        stack.shrink(1);

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }
}
