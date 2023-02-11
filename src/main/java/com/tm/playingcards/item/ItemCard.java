package com.tm.playingcards.item;

import com.tm.playingcards.util.CardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCard extends ItemCardCovered {
    public ItemCard() {
        isCovered = false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(CardHelper.getCardName(stack.getDamageValue()).withStyle(ChatFormatting.GOLD));
        super.appendHoverText(stack, world, tooltip, flag);
    }
}
