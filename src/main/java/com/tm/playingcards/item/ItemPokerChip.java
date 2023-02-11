package com.tm.playingcards.item;

import com.mojang.math.Vector3d;
import com.tm.playingcards.entity.EntityPokerChip;
import com.tm.playingcards.init.ModItems;
import com.tm.playingcards.util.ItemHelper;
import com.tm.playingcards.util.StringHelper;
import com.tm.playingcards.util.UnitChatMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ItemPokerChip extends Item {
    private final byte chipId;
    private final int value;

    public ItemPokerChip(byte chipId, int value) {
        super(new Properties().tab(ModItems.TAB));
        this.chipId = chipId;
        this.value = value;
    }

    private UnitChatMessage getUnitMessage(Player... players) {
        return new UnitChatMessage("poker_chip", players);
    }

    public byte getChipId() {
        return this.chipId;
    }

    public static Item getPokerChip(byte pokerChipId) {
        return switch (pokerChipId) {
            case 1  -> ModItems.POKER_CHIP_RED.get();
            case 2  -> ModItems.POKER_CHIP_BLUE.get();
            case 3  -> ModItems.POKER_CHIP_GREEN.get();
            case 4  -> ModItems.POKER_CHIP_BLACK.get();
            default -> ModItems.POKER_CHIP_WHITE.get();
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag nbt = ItemHelper.getNBT(stack);

        String owner = nbt.hasUUID("OwnerID") ? nbt.getString("OwnerName") : "Not set";
        tooltip.add(Component.translatable(ChatFormatting.GRAY + "Owner: " + ChatFormatting.GOLD + owner));

        tooltip.add(Component.translatable(ChatFormatting.GRAY + "Value (1): " + ChatFormatting.GOLD + value));

        if (stack.getCount() > 1) {
            tooltip.add(Component.translatable(ChatFormatting.GRAY + "Value (" + stack.getCount() + "): " + ChatFormatting.GOLD + StringHelper.printCommas(value * stack.getCount())));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!player.isCrouching())
            return new InteractionResultHolder<>(InteractionResult.FAIL, heldItem);

        UnitChatMessage unitMessage = getUnitMessage(player);
        CompoundTag nbt = ItemHelper.getNBT(heldItem);

        if (!nbt.hasUUID("OwnerID")) {
            nbt.putUUID("OwnerID", player.getUUID());
            nbt.putString("OwnerName", player.getDisplayName().getString());

            if (world.isClientSide)
                unitMessage.printMessage(ChatFormatting.GREEN, Component.translatable("message.poker_chip_owner_set"));
        } else if (world.isClientSide) {
            unitMessage.printMessage(ChatFormatting.RED, Component.translatable("message.poker_chip_owner_error"));
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isCrouching())
            return InteractionResult.PASS;

        Level world = context.getLevel();
        //Location location = new Location(world, context.getClickedPos());

        UnitChatMessage unitMessage = getUnitMessage(player);
        CompoundTag nbt = ItemHelper.getNBT(context.getItemInHand());

        if (nbt.hasUUID("OwnerID")) {
            UUID ownerId = nbt.getUUID("OwnerID");
            String ownerName = nbt.getString("OwnerName");

            //if (world.getBlockState(context.getClickedPos()).hasBlockEntity()) {
            //    BlockEntity tileEntity = world.getBlockEntity(context.getClickedPos());

            //    if (tileEntity instanceof TileEntityPokerTable) {
            //        TileEntityPokerTable pokerTable = (TileEntityPokerTable)tileEntity;

            //        if (!ownerId.equals(pokerTable.getOwnerId())) {
            //            if (world.isClientSide)
            //                unitMessage.printMessage(ChatFormatting.RED, Component.translatable("message.poker_chip_table_error"));

            //            return InteractionResult.PASS;
            //        }
            //    }
            //}

            Vec3 clickPos = context.getClickLocation();
            EntityPokerChip chip = new EntityPokerChip(world, new Vector3d(clickPos.x, clickPos.y, clickPos.z), ownerId, ownerName, chipId);
            world.addFreshEntity(chip);
            context.getItemInHand().shrink(1);
        } else if (world.isClientSide) {
            player.displayClientMessage(Component.translatable("message.poker_chip_owner_missing").withStyle(ChatFormatting.RED), true);
        }

        return InteractionResult.SUCCESS;
    }
}
