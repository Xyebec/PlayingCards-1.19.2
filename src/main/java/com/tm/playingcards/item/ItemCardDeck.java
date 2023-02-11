package com.tm.playingcards.item;

import com.mojang.math.Vector3d;
import com.tm.playingcards.entity.EntityCardDeck;
import com.tm.playingcards.init.ModItems;
import com.tm.playingcards.util.CardHelper;
import com.tm.playingcards.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemCardDeck extends Item {
    public ItemCardDeck() {
        super(new Item.Properties().tab(ModItems.TAB).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag nbt = ItemHelper.getNBT(stack);
        tooltip.add(Component.translatable("lore.cover").append(" ").withStyle(ChatFormatting.GRAY).append(Component.translatable(CardHelper.CARD_SKIN_NAMES[nbt.getByte("SkinID")]).withStyle(ChatFormatting.AQUA)));
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab != ModItems.TAB)
            return;

        for (byte colorId = 0; colorId < CardHelper.CARD_SKIN_NAMES.length; colorId++) {
            ItemStack stack = new ItemStack(this);
            CompoundTag nbt = ItemHelper.getNBT(stack);

            nbt.putByte("SkinID", colorId);
            items.add(stack);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        CompoundTag nbt = ItemHelper.getNBT(context.getItemInHand());
        Vec3 clickPos = context.getClickLocation();
        EntityCardDeck cardDeck = new EntityCardDeck(world, new Vector3d(clickPos.x, clickPos.y, clickPos.z), context.getRotation(), nbt.getByte("SkinID"));
        world.addFreshEntity(cardDeck);
        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }
}
