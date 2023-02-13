package com.tm.playingcards;

import com.tm.playingcards.init.ModDataSerializers;
import com.tm.playingcards.event.CardInteractEvent;
import com.tm.playingcards.init.*;
import com.tm.playingcards.packet.PacketInteractCard;
import com.tm.playingcards.render.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(PlayingCards.MOD_ID)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class PlayingCards {
    public static final String MOD_ID = "playingcards";
    public static final String MOD_NAME = "Playing Cards";
    public static SimpleChannel network;

    public PlayingCards() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModDataSerializers.register();
        //InitTileEntityTypes.register(MOD_EVENT_BUS);
        ModEntityTypes.register(modEventBus);
        ModItems.register(modEventBus);

        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        int id = 0;
        network = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, MOD_ID), () -> "1.0", s -> true, s -> true);
        network.registerMessage(++id, PacketInteractCard.class, PacketInteractCard::toBytes, PacketInteractCard::new, PacketInteractCard::handle);

        MinecraftForge.EVENT_BUS.register(new CardInteractEvent());
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        ModModelOverrides.register();
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.CARD.get(), RenderEntityCard::new);
        event.registerEntityRenderer(ModEntityTypes.CARD_DECK.get(), RenderEntityCardDeck::new);
        event.registerEntityRenderer(ModEntityTypes.POKER_CHIP.get(), RenderEntityPokerChip::new);
        event.registerEntityRenderer(ModEntityTypes.DICE.get(), RenderEntityDice::new);
        //event.registerEntityRenderer(InitEntityTypes.SEAT.get(), RenderEntitySeat::new);
    }
}