package com.tm.playingcards.init;

import com.tm.playingcards.entity.*;
import com.tm.playingcards.PlayingCards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PlayingCards.MOD_ID);

    public static final RegistryObject<EntityType<EntityCard>> CARD = ENTITY_TYPES.register("card", () -> EntityType.Builder.<EntityCard>of(EntityCard::new, MobCategory.MISC).sized(0.5F, 0.25F).build(new ResourceLocation(PlayingCards.MOD_ID, "card").toString()));
    public static final RegistryObject<EntityType<EntityCardDeck>> CARD_DECK = ENTITY_TYPES.register("card_deck", () -> EntityType.Builder.<EntityCardDeck>of(EntityCardDeck::new, MobCategory.MISC).sized(0.5F, 0.25F).build(new ResourceLocation(PlayingCards.MOD_ID, "card_deck").toString()));
    public static final RegistryObject<EntityType<EntityPokerChip>> POKER_CHIP = ENTITY_TYPES.register("poker_chip", () -> EntityType.Builder.<EntityPokerChip>of(EntityPokerChip::new, MobCategory.MISC).sized(0.3F, 0.3F).build(new ResourceLocation(PlayingCards.MOD_ID, "poker_chip").toString()));
    public static final RegistryObject<EntityType<EntityDice>> DICE = ENTITY_TYPES.register("dice", () -> EntityType.Builder.<EntityDice>of(EntityDice::new, MobCategory.MISC).sized(0.3F, 0.3F).build(new ResourceLocation(PlayingCards.MOD_ID, "dice").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
