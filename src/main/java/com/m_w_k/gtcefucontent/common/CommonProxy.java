package com.m_w_k.gtcefucontent.common;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCRecipeLoader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import gregtech.api.block.VariantItemBlock;

import java.util.Objects;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = GTCEFuContent.MODID)
public class CommonProxy {
    public void preLoad() {}

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        GTCEFuContent.log("Registering blocks...");
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(GTCEFuCMetaBlocks.ADVANCED_CASING);
        registry.register(GTCEFuCMetaBlocks.HARDENED_CASING);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        GTCEFuContent.log("Registering Items...");
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(createItemBlock(GTCEFuCMetaBlocks.ADVANCED_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(GTCEFuCMetaBlocks.HARDENED_CASING, VariantItemBlock::new));
    }

    private static <T extends Block> ItemBlock createItemBlock(T block, Function<T, ItemBlock> producer) {
        ItemBlock itemBlock = producer.apply(block);
        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        return itemBlock;
    }

    @SubscribeEvent()
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        GTCEFuContent.log("Beginning recipe registration stage 1");
        // Main recipe registration
        // This is called AFTER GregTech registers recipes, so anything here is safe to call removals in
        GTCEFuCRecipeLoader.init();
        GTCEFuContent.log("Finished recipe registration stage 1");
    }

    // Register recipes that depend on other recipemaps to register
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerRecipesLow(RegistryEvent.Register<IRecipe> event) {
        GTCEFuContent.log("Beginning recipe registration stage 2");
        GTCEFuCRecipeLoader.initLate();
        GTCEFuContent.log("Finished recipe registration stage 2");
    }
}
