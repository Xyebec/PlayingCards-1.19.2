package com.tm.playingcards.event;

import com.tm.playingcards.PlayingCards;
import com.tm.playingcards.packet.PacketInteractCard;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.tm.playingcards.item.ItemCard;

public class CardInteractEvent {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onLeftClick(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
            return;

        if (event.getAction() != 1 || event.getButton() != 0)
            return;

        Player player = mc.player;
        if (mc.level == null || player == null)
            return;

        ItemStack heldStack = player.getMainHandItem();
        if (!(heldStack.getItem() instanceof ItemCard))
            return;

        ItemCard card = (ItemCard)heldStack.getItem();
        card.flipCard(heldStack, player);

        PlayingCards.network.sendToServer(new PacketInteractCard("flipinv"));

        event.setCanceled(true);
    }
}
