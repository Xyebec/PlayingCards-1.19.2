package com.tm.playingcards.packet;

import com.tm.playingcards.item.ItemCardCovered;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketInteractCard {
    private final String command;

    public PacketInteractCard(String command) {
        this.command = command;
    }

    public PacketInteractCard(FriendlyByteBuf buf) {
        command = buf.readUtf(11).trim();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(command, 11);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;

            if (command.equalsIgnoreCase("flipinv")) {
                Item item = player.getMainHandItem().getItem();

                if (item instanceof ItemCardCovered) {
                    ItemCardCovered card = (ItemCardCovered)item;
                    card.flipCard(player.getMainHandItem(), player);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
