package com.m_w_k.gtcefucontent.common;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCRecipeLoader;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = GTCEFuContent.MODID)
public class CommonProxy {
    public void preLoad() {}

//    @SubscribeEvent
//    public static void registerBlocks(RegistryEvent.Register<Block> event) {
//        GCYMLog.logger.info("Registering blocks...");
//        IForgeRegistry<Block> registry = event.getRegistry();
//
//        registry.register(GCYMMetaBlocks.UNIQUE_CASING);
//        registry.register(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING);
//    }

//    @SubscribeEvent
//    public static void registerItems(RegistryEvent.Register<Item> event) {
//        GCYMLog.logger.info("Registering Items...");
//        IForgeRegistry<Item> registry = event.getRegistry();
//
//        registry.register(createItemBlock(GCYMMetaBlocks.UNIQUE_CASING, VariantItemBlock::new));
//        registry.register(createItemBlock(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING, VariantItemBlock::new));
//    }
//
//    private static <T extends Block> ItemBlock createItemBlock(T block, Function<T, ItemBlock> producer) {
//        ItemBlock itemBlock = producer.apply(block);
//        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
//        return itemBlock;
//    }

    @SubscribeEvent()
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        GTCEFuContent.log("Registering recipes...");
        GTCEFuCRecipeLoader.init();
    }
}
