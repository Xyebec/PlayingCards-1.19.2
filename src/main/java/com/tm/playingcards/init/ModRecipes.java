package com.tm.playingcards.init;

import com.tm.playingcards.PlayingCards;
import com.tm.playingcards.recipe.CardDeckRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PlayingCards.MOD_ID);

    public static final RegistryObject<RecipeSerializer<CardDeckRecipe>> CARD_DECK =
            RECIPES.register("crafting_special_deck", () ->  CardDeckRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        RECIPES.register(eventBus);
    }
}
