package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import crazypants.enderio.base.fluid.Fluids;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.unification.material.Materials;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;
import java.util.List;

public class GTCEFuMiscRecipes {
    private GTCEFuMiscRecipes() {}

    public static void init() {

    }

    public static void cutterUpdate() {
        GTCEFuContent.log("Updating cutter recipes with Cloud Seed...");
        Collection<Recipe> oldRecipes = RecipeMaps.CUTTER_RECIPES.getRecipeList();
        oldRecipes.forEach(recipe -> {
            if (recipe.hasInputFluid(Materials.Lubricant.getFluid(1))) {
                // replicate the recipe, but halve the duration and switch to cloud seed
                int fluidAmount = recipe.getFluidInputs().get(0).getAmount();
                RecipeMaps.CUTTER_RECIPES.recipeBuilder()
                        .fluidInputs(new FluidStack(Fluids.CLOUD_SEED.getFluid(), fluidAmount))
                        // we know the cutter only has one input
                        .input(recipe.getInputs().get(0))
                        .outputs(recipe.getOutputs().toArray(new ItemStack[0]))
                        .chancedOutputs(recipe.getChancedOutputs())
                        .duration(recipe.getDuration() / 2)
                        .EUt(recipe.getEUt())
                        .buildAndRegister();
            }
        });
    }
}
