package com.tm.playingcards.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class UnitChatMessage {
    private final String unitName;
    private final Player[] players;

    public UnitChatMessage(String unitName, Player... players) {
        this.unitName = unitName;
        this.players = players;
    }

    public void printMessage(ChatFormatting format, MutableComponent message) {
        for (Player player : players) {
            player.displayClientMessage(Component.literal("[").withStyle(ChatFormatting.WHITE).append(getUnitName().append("] ")).append(message.withStyle(format)), true);
        }
    }

    private MutableComponent getUnitName() {
        return Component.translatable("unitname." + unitName);
    }

}
