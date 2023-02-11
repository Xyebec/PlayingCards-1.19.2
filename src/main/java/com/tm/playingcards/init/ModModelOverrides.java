package com.tm.playingcards.init;

import com.tm.playingcards.util.ItemHelper;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ModModelOverrides {
    public static void register() {
        ItemProperties.register(ModItems.CARD.get(), new ResourceLocation("value"), (stack, world, player, unknown) -> stack.getDamageValue());
        ItemProperties.register(ModItems.CARD_COVERED.get(), new ResourceLocation("skin"), (stack, world, player, unknown) -> ItemHelper.getNBT(stack).getByte("SkinID"));
        ItemProperties.register(ModItems.CARD_DECK.get(), new ResourceLocation("skin"), (stack, world, player, unknown) -> ItemHelper.getNBT(stack).getByte("SkinID"));
    }
}
