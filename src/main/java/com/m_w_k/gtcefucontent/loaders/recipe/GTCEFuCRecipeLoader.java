package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.GTCEFuContent;

public final class GTCEFuCRecipeLoader {

    private GTCEFuCRecipeLoader() {}

    public static void init() {
        GTCEFuContent.log("Starting recipe construction...");

        GTCEFuCInfinityExtractorRecipes.init();
        GTCEFuCPneumaticInfuserRecipes.init();
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
