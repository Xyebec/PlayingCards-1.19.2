package com.tm.playingcards.item;

import com.mojang.math.Vector3d;
import com.tm.playingcards.entity.EntityCard;
import com.tm.playingcards.init.ModItems;
import com.tm.playingcards.util.CardHelper;
import com.tm.playingcards.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemCard extends Item {
    private final boolean isCovered;

    public ItemCard(boolean isCovered) {
        super(new Properties().stacksTo(1));
        this.isCovered = isCovered;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        if (!isCovered)
            tooltip.add(CardHelper.getCardName(stack.getDamageValue()).withStyle(ChatFormatting.GOLD));

        CompoundTag nbt = ItemHelper.getNBT(stack);
        tooltip.add(Component.translatable("lore.cover").append(" ").withStyle(ChatFormatting.GRAY).append(Component.translatable(CardHelper.CARD_SKIN_NAMES[nbt.getByte("SkinID")]).withStyle(ChatFormatting.AQUA)));
    }

    public void flipCard(ItemStack heldItem, LivingEntity entity) {
        if (!(entity instanceof Player))
            return;

        Player player = (Player)entity;
        if (!(player.getMainHandItem().getItem() instanceof ItemCard))
            return;

        Item nextCard = isCovered ? ModItems.CARD.get() : ModItems.CARD_COVERED.get();

        ItemStack newCard = new ItemStack(nextCard);
        newCard.setDamageValue(heldItem.getDamageValue());

        CompoundTag heldNBT = ItemHelper.getNBT(heldItem);
        ItemHelper.getNBT(newCard).putUUID("UUID", heldNBT.getUUID("UUID"));
        ItemHelper.getNBT(newCard).putByte("SkinID", heldNBT.getByte("SkinID"));

        player.setItemInHand(InteractionHand.MAIN_HAND, newCard);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.getGameTime() % 60 != 0)
            return;

        if (!(entity instanceof Player))
            return;

        CompoundTag nbt = ItemHelper.getNBT(stack);
        if (!nbt.hasUUID("UUID"))
            return;

        UUID deckUUID = nbt.getUUID("UUID");
        if (deckUUID.getLeastSignificantBits() == 0)
            return;

        Player player = (Player)entity;
        BlockPos pos = player.blockPosition();

        if (CardHelper.isNearDeck(world, pos, deckUUID))
            return;

        player.getInventory().getItem(itemSlot).shrink(1);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.PASS;

        CompoundTag nbt = ItemHelper.getNBT(context.getItemInHand());
        UUID deckUUID = nbt.getUUID("UUID");

        Level world = context.getLevel();

        if (!CardHelper.isNearDeck(world, context.getClickedPos(), deckUUID)) {
            if (player.level.isClientSide)
                player.displayClientMessage(Component.translatable("message.deck_too_far").withStyle(ChatFormatting.RED), true);

            return InteractionResult.PASS;
        }

        Vec3 clickPos = context.getClickLocation();
        ItemStack heldItem = context.getItemInHand();
        EntityCard cardDeck = new EntityCard(world, new Vector3d(clickPos.x, clickPos.y, clickPos.z), context.getRotation(), nbt.getByte("SkinID"), deckUUID, isCovered, (byte)heldItem.getDamageValue());
        world.addFreshEntity(cardDeck);
        heldItem.shrink(1);

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
        return false;
    }
}
