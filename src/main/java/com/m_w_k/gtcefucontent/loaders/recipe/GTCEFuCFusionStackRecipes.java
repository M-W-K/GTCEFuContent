package com.m_w_k.gtcefucontent.loaders.recipe;

import java.util.Collection;
import java.util.stream.Collectors;

import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.builders.FusionRecipeBuilder;
import gregtech.api.recipes.recipeproperties.FusionEUToStartProperty;

public class GTCEFuCFusionStackRecipes {

    private GTCEFuCFusionStackRecipes() {}

    public static void init() {
        GTCEFuContent.log("Starting the construction of Fusion Stack custom recipes...");
        Collection<Recipe> fusionRecipes = RecipeMaps.FUSION_RECIPES.getRecipeList();

        // replicate fusion map as fusion stack maps with modified values
        for (int i = 0; i < 3; i++) {
            RecipeMap<FusionRecipeBuilder> recipeMap = GTCEFuCRecipeMaps.FUSION_STACK_RECIPE_MAPS.get(i);
            GTCEFuContent.log(String.format("Constructing %s recipe map", recipeMap.getUnlocalizedName()));
            int mod2 = (int) Math.pow(2, i + 1);
            int mod4 = mod2 * mod2;
            for (Recipe defaultRecipe : fusionRecipes) {
                FusionRecipeBuilder recipe = recipeMap.recipeBuilder();
                recipe.fluidInputs(defaultRecipe.getFluidInputs().stream()
                        .map(input -> input.copyWithAmount(input.getInputFluidStack().amount * mod2 * mod4))
                        .collect(Collectors.toList()));
                // only one fluid output
                FluidStack fluidOut = defaultRecipe.getFluidOutputs().get(0);
                recipe.fluidOutputs(new FluidStack(fluidOut, fluidOut.amount * mod2 * mod4));
                recipeMap.addRecipe(recipe
                        .EUt(defaultRecipe.getEUt() * mod2)
                        .duration(defaultRecipe.getDuration() * mod4)
                        .EUToStart(defaultRecipe.getRecipePropertyStorage()
                                .getRecipePropertyValue(FusionEUToStartProperty.getInstance(), 0L) * mod2)
                        .build());
            }

        }
        GTCEFuContent.log("Finished Fusion Stack recipe construction.");
    }
}
