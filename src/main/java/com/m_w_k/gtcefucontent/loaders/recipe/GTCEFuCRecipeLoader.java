package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.GTCEFuContent;

public class GTCEFuCRecipeLoader {
    private GTCEFuCRecipeLoader() {}

    public static void init() {
        GTCEFuContent.log("Starting recipe construction...");
        GTCEFuCInfinityExtractorRecipes.init();
        GTCEFuCSympatheticCombustorRecipes.init();
        GTCEFuMiscRecipes.init();
        GTCEFuContent.log("Recipe construction complete.");
    }
}
