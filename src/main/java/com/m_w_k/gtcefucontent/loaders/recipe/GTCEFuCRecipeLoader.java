package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.GTCEFuContent;

public final class GTCEFuCRecipeLoader {

    private GTCEFuCRecipeLoader() {}

    public static void init() {
        GTCEFuContent.log("Starting recipe construction...");

        GTCEFuCInfinityExtractorRecipes.init();
        GTCEFuCPneumaticInfuserRecipes.init();
        if (GTCEFuContent.isProjectEXLoaded) {
            GTCEFuCStarSiphonRecipes.init();
            GTCEFuCAntimatterCompressorRecipes.init();
        }

        GTCEFuCPackingLoader.init();

        GTCEFuCCasingLoader.init();
        GTCEFuCHEUComponentLoader.init();
        GTCEFuCMiscRecipes.init();
        GTCEFuCNaqFuelCellPackerLoader.init();
        GTCEFuCFuelRecipes.init();
        GTCEFuCEutecticLoader.init();

        GTCEFuCCraftingRecipeLoader.init();

        GTCEFuCControllerLoader.init();
        GTCEFuCMTELoader.init();

        GTCEFuContent.log("Recipe construction complete.");
    }

    public static void initLate() {
        // Adjust/create recipeMaps that depend on iterating through other recipeMaps.

        // GCYM molten liquid registration is too slow
        GTCEFuCHeatExchangerLoader.init();

        // We can't change the behavior of the cutter recipeMap setup itself without being overly invasive.
        GTCEFuCMiscRecipes.cutterUpdate();

        // Linear forging recipes are dependent on the EBF, ABS, and vacuum freezer
        LinearForgingFurnaceLoader.registerLate();

        // The fusion stack recipeMaps are dependent on the normal fusion recipemap
        GTCEFuCFusionStackRecipes.init();

        // Building the faux recipemap should wait until after all heat exchanger recipes are done.
        GTCEFuCHeatExchangerLoader.postInit();
    }
}
