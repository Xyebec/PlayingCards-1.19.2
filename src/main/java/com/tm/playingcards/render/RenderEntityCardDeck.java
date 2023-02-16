package com.tm.playingcards.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.tm.playingcards.init.ModItems;
import com.tm.playingcards.util.CardHelper;
import com.tm.playingcards.util.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.tm.playingcards.entity.EntityCardDeck;
import net.minecraft.world.item.ItemStack;

public class RenderEntityCardDeck extends EntityRenderer<EntityCardDeck> {
    public RenderEntityCardDeck(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(EntityCardDeck entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, combinedLight);

        ItemStack card;
        if (entity.isCovered()) {
            card = new ItemStack(ModItems.CARD_COVERED.get());
            ItemHelper.getNBT(card).putByte("SkinID", entity.getSkinId());
        } else {
            card = new ItemStack(ModItems.CARD.get());
            card.setDamageValue(entity.getTopStackId());
        }

        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-entity.getYRot() + 180));
        poseStack.scale(1.5F, 1.5F + (entity.getStackSize() * 0.4F), 1.5F);

        CardHelper.renderItem(card, poseStack, buffer, combinedLight);

        poseStack.popPose();
    }

    @Override
    protected boolean shouldShowName(EntityCardDeck entity) {
        return Minecraft.getInstance().player.isCrouching() && entity.getStackSize() > 0;
    }

    @Override
    protected void renderNameTag(EntityCardDeck entity, Component component, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
        String text = String.format("%d/%d", entity.getStackSize(), CardHelper.MAX_STACK_SIZE);
        Component nameTag = Component.literal(text);
        super.renderNameTag(entity, nameTag, poseStack, buffer, combinedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCardDeck entity) {
        return null;
    }
}
