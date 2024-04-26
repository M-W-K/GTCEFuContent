package com.m_w_k.gtcefucontent.loaders.recipe;

import net.minecraft.item.ItemStack;

import com.latmod.mods.projectex.item.ProjectEXItems;
import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockAdvancedCasing;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;

import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregicality.multiblocks.common.metatileentities.GCYMMetaTileEntities;
import gregtech.api.GTValues;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;

public final class GTCEFuCRecipeLoader {

    private GTCEFuCRecipeLoader() {}

    public static void init() {
        GTCEFuContent.log("Starting recipe construction...");

        GTCEFuCInfinityExtractorRecipes.init();
        GTCEFuCPneumaticInfuserRecipes.init();
        GTCEFuCSympatheticCombustorRecipes.init();
        GTCEFuCStarSiphonRecipes.init();
        GTCEFuCAntimatterCompressorRecipes.init();

        GTCEFuCPackingLoader.init();

        GTCEFuCCasingLoader.init();
        GTCEFuCHEUComponentLoader.init();
        GTCEFuCMiscRecipes.init();
        GTCEFuCEutecticLoader.init();
        GTCEFuCHeatExchangerLoader.init();

        GTCEFuCCraftingRecipeLoader.init();

        GTCEFuCControllerLoader.init();

        GTCEFuContent.log("Recipe construction complete.");
    }

    public static void initLate() {
        // Adjust/create recipeMaps that depend on iterating through other recipeMaps.

        // We can't change the behavior of the cutter recipeMap setup itself without being overly invasive.
        GTCEFuCMiscRecipes.cutterUpdate();

        // The forging furnace recipeMap is completely new, but dependent on the blast furnace recipeMap.
        GTCEFuCForgingFurnaceRecipes.init();

        // The fusion stack recipeMaps are dependent on the normal fusion recipemap
        GTCEFuCFusionStackRecipes.init();

        // Building the faux recipemap should wait until after all heat exchanger recipes are done.
        GTCEFuCHeatExchangerLoader.postInit();
    }
}
