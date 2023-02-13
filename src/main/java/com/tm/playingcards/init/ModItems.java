package com.tm.playingcards.init;

import com.tm.playingcards.item.ItemCard;
import com.tm.playingcards.item.ItemCardDeck;
import com.tm.playingcards.item.ItemPokerChip;
import com.tm.playingcards.PlayingCards;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PlayingCards.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PlayingCards.MOD_ID);

    //----- BLOCKS ------\\

    //public static final RegistryObject<Block> POKER_TABLE = BLOCKS.register("poker_table", BlockPokerTable::new);
    //public static final RegistryObject<Item> POKER_TABLE_ITEM = ITEMS.register("poker_table", () -> new BlockItemBase(POKER_TABLE.get()));

    //public static final RegistryObject<Block> BAR_STOOL = BLOCKS.register("bar_stool", BlockBarStool::new);
    //public static final RegistryObject<Item> BAR_STOOL_ITEM = ITEMS.register("bar_stool", () -> new BlockItemBase(BAR_STOOL.get()));

    //public static final RegistryObject<Block> CASINO_CARPET_SPACE = BLOCKS.register("casino_carpet_space", BlockCasinoCarpet::new);
    //public static final RegistryObject<Item> CASINO_CARPET_SPACE_ITEM = ITEMS.register("casino_carpet_space", () -> new BlockItemBase(CASINO_CARPET_SPACE.get()));

    //----- ITEMS ------\\

    public static final RegistryObject<Item> CARD_DECK = ITEMS.register("card_deck", ItemCardDeck::new);
    public static final RegistryObject<Item> CARD_COVERED = ITEMS.register("card_covered", () -> new ItemCard(true));
    public static final RegistryObject<Item> CARD = ITEMS.register("card", () -> new ItemCard(false));

    public static final RegistryObject<Item> POKER_CHIP_WHITE = ITEMS.register("poker_chip_white", () -> new ItemPokerChip((byte)0, 1));
    public static final RegistryObject<Item> POKER_CHIP_RED = ITEMS.register("poker_chip_red", () -> new ItemPokerChip((byte)1,5));
    public static final RegistryObject<Item> POKER_CHIP_BLUE = ITEMS.register("poker_chip_blue", () -> new ItemPokerChip((byte)2,10));
    public static final RegistryObject<Item> POKER_CHIP_GREEN = ITEMS.register("poker_chip_green", () -> new ItemPokerChip((byte)3,25));
    public static final RegistryObject<Item> POKER_CHIP_BLACK = ITEMS.register("poker_chip_black", () -> new ItemPokerChip((byte)4,100));

    //public static final RegistryObject<Item> DICE_WHITE = ITEMS.register("dice_white", ItemDice::new);

    public static final CreativeModeTab TAB = new CreativeModeTab(PlayingCards.MOD_ID + ".tabMain") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(CARD.get());
        }
    };

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
