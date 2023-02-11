package com.tm.playingcards.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class CardHelper {
    public static final String[] CARD_SKIN_NAMES = { "card.skin.blue", "card.skin.red", "card.skin.black", "card.skin.pig" };
    public static final byte MAX_STACK_SIZE = 52;

    public static void renderItem(ItemStack stack, double offsetX, double offsetY, double offsetZ, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
        poseStack.pushPose();
        poseStack.translate(offsetX, offsetY, offsetZ);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, 0);
        poseStack.popPose();
    }

    public static MutableComponent getCardName(int id) {
        int typeId = id / 4 + 1;

        String type = switch (typeId) {
            case 0  -> "card.ace";
            case 11 -> "card.jack";
            case 12 -> "card.queen";
            case 13 -> "card.king";
            default -> "" + typeId;
        };

        int suiteId = id % 4;

        String suite = switch (suiteId) {
            case 1  -> "card.clubs";
            case 2  -> "card.diamonds";
            case 3  -> "card.hearts";
            default -> "card.spades";
        };

        return Component.translatable(type).append(" ").append(Component.translatable("card.of").append(" ").append(Component.translatable(suite)));
    }
}
