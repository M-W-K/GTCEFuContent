package com.m_w_k.gtcefucontent.common;

import java.util.Objects;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.registries.IForgeRegistry;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.fluids.void_starlight.VoidStarlightBlockFluid;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCRecipeLoader;

import gregtech.api.GregTechAPI;
import gregtech.api.block.VariantItemBlock;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.unification.material.event.MaterialEvent;
import gregtech.api.unification.material.event.MaterialRegistryEvent;

@Mod.EventBusSubscriber(modid = GTCEFuContent.MODID)
public class CommonProxy {

    public void preLoad() {}

    @SubscribeEvent
    public static void createMaterialRegistry(MaterialRegistryEvent event) {
        GregTechAPI.materialManager.createRegistry(GTCEFuContent.MODID);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        GTCEFuContent.log("Registering blocks...");
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(GTCEFuCMetaBlocks.ADVANCED_CASING);
        registry.register(GTCEFuCMetaBlocks.HARDENED_CASING);
        registry.register(GTCEFuCMetaBlocks.STANDARD_CASING);
        registry.register(GTCEFuCMetaBlocks.STORAGE_BLOCK);
        for (var fluid : VoidStarlightBlockFluid.TO_REGISTER) {
            registry.register(fluid);
        }
        VoidStarlightBlockFluid.TO_REGISTER = null;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        GTCEFuContent.log("Registering Items...");
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(createItemBlock(GTCEFuCMetaBlocks.ADVANCED_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(GTCEFuCMetaBlocks.HARDENED_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(GTCEFuCMetaBlocks.STANDARD_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(GTCEFuCMetaBlocks.STORAGE_BLOCK, VariantItemBlock::new));
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

    @SubscribeEvent
    public static void registerMaterials(MaterialEvent event) {
        GTCEFuCMaterials.register();
    }

    @SubscribeEvent
    public static void dimCheck(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            // check every second
            if (FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter() % 20 == 0) {
                DimensionBreathabilityHandler.checkPlayer(event.player);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
        EntityEquipmentSlot slot = event.getSlot();
        if (event.getFrom().isEmpty() || slot == EntityEquipmentSlot.MAINHAND || slot == EntityEquipmentSlot.OFFHAND)
            return;

        ItemStack stack = event.getFrom();
        if (!(stack.getItem() instanceof ArmorMetaItem) || stack.getItem().equals(event.getTo().getItem()))
            return;

        ArmorMetaItem<?>.ArmorMetaValueItem valueItem = ((ArmorMetaItem<?>) stack.getItem()).getItem(stack);
        if (valueItem == null) return;
        if (valueItem.isItemEqual(GTCEFuCMetaItems.GAS_MASK.getStackForm())) {
            event.getEntityLiving().removePotionEffect(MobEffects.NIGHT_VISION);
        }
    }
}
