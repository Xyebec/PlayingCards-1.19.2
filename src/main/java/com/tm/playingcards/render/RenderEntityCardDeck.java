package com.tm.playingcards.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.tm.playingcards.init.ModItems;
import com.tm.playingcards.util.CardHelper;
import com.tm.playingcards.util.ItemHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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

        ItemStack cardStack = new ItemStack(ModItems.CARD_COVERED.get());
        ItemHelper.getNBT(cardStack).putByte("SkinID", entity.getSkinId());

        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-entity.getYRot() + 180));
        poseStack.scale(1.5F, 1.5F + (entity.getStackSize() * 0.4F), 1.5F);

        CardHelper.renderItem(cardStack, 0, 0, 0, poseStack, buffer, combinedLight);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCardDeck entity) {
        return null;
    }
}
