package com.tm.playingcards.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.playingcards.entity.EntityDice;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RenderEntityDice extends EntityRenderer<EntityDice> {
    public RenderEntityDice(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(EntityDice entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, combinedLight);

        poseStack.pushPose();
        poseStack.translate(0, 0.15D, 0);
        //poseStack.scale(0.6F, 0.6F, 0.6F);

        //CardHelper.renderItem(new ItemStack(InitItems.DICE_WHITE.get()), 0, 0,0, matrixStack, buffer, combinedLight);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityDice entity) {
        return null;
    }
}
