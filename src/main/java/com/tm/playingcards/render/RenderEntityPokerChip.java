package com.tm.playingcards.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.tm.playingcards.entity.EntityPokerChip;
import com.tm.playingcards.item.ItemPokerChip;
import com.tm.playingcards.util.CardHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public class RenderEntityPokerChip extends EntityRenderer<EntityPokerChip> {
    public RenderEntityPokerChip(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(EntityPokerChip entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, combinedLight);

        poseStack.pushPose();
        poseStack.translate(0, 0.01D, 0.07D);
        poseStack.scale(0.5F, 0.5F, 0.5F);

        for (byte i = 0; i < entity.getStackSize(); i++) {
            poseStack.pushPose();

            Random randomX = new Random(i * 200000);
            Random randomY = new Random(i * 100000);

            poseStack.translate(randomX.nextDouble() * 0.05D - 0.025D, 0, randomY.nextDouble() * 0.05D - 0.025D);
            poseStack.mulPose(Vector3f.XN.rotationDegrees(90));

            CardHelper.renderItem(new ItemStack(ItemPokerChip.getPokerChip(entity.getIdAt(i))), 0, 0,i * 0.032D, poseStack, buffer, combinedLight);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityPokerChip entity) {
        return null;
    }
}
