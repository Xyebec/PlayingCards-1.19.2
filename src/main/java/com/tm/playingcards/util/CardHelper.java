package com.tm.playingcards.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.playingcards.entity.EntityCardDeck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public class CardHelper {
    public static final String[] CARD_SKIN_NAMES = { "card.skin.blue", "card.skin.red", "card.skin.black", "card.skin.pig" };
    public static final byte MAX_STACK_SIZE = 52;

    public static void renderItem(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, 0);
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

    public static boolean isNearDeck(Level world, BlockPos pos, UUID deckUUID) {
        AABB lookupAABB = new AABB(pos.getX() - 20, pos.getY() - 20, pos.getZ() - 20, pos.getX() + 20, pos.getY() + 20, pos.getZ() + 20);
        List<EntityCardDeck> closeDecks = world.getEntitiesOfClass(EntityCardDeck.class, lookupAABB);

        for (EntityCardDeck closeDeck : closeDecks) {
            if (deckUUID.equals(closeDeck.getUUID())) {
                return true;
            }
        }

        return false;
    }
}
