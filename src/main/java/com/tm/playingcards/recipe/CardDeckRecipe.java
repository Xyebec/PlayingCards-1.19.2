package com.tm.playingcards.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tm.playingcards.PlayingCards;
import com.tm.playingcards.init.ModItems;
import com.tm.playingcards.util.ItemHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nullable;

public class CardDeckRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;

    public CardDeckRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(SimpleContainer container, Level world) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stackInSlot = container.getItem(i);

            if (i != 4) {
                if (stackInSlot.getItem() != Items.PAPER) {
                    return false;
                }
            }
        }

        ItemStack middleSlot = container.getItem(4);

        return middleSlot.getItem() == Items.BLUE_DYE
                || middleSlot.getItem() == Items.RED_DYE
                || middleSlot.getItem() == Items.BLACK_DYE
                || middleSlot.getItem() == Items.PINK_DYE;
    }

    @Override
    public ItemStack assemble(SimpleContainer container) {
        ItemStack result = new ItemStack(ModItems.CARD_DECK.get());
        CompoundTag nbt = ItemHelper.getNBT(result);

        ItemStack middleSlot = container.getItem(4);

        if (middleSlot.getItem() == Items.RED_DYE) {
            nbt.putByte("SkinID", (byte)1);
        } else if (middleSlot.getItem() == Items.BLACK_DYE) {
            nbt.putByte("SkinID", (byte)2);
        } else if (middleSlot.getItem() == Items.PINK_DYE) {
            nbt.putByte("SkinID", (byte)3);
        }

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    public static class Serializer implements RecipeSerializer<CardDeckRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(PlayingCards.MOD_ID,"crafting_special_deck");

        @Override
        public CardDeckRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new CardDeckRecipe(id, output, inputs);
        }

        @Override
        public CardDeckRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            return new CardDeckRecipe(id, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CardDeckRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
        }
    }
}
