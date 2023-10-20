package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.material.material.Material;
import gregtech.api.GTValues;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

public class GTCEFuMiscRecipes {
    private GTCEFuMiscRecipes() {}

    public static void init() {
        RecipeMaps.LARGE_CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(Materials.LiquidAir.getFluid(60000), Materials.Water.getFluid(60000))
                .input(OrePrefix.dust, Materials.Ash)
                .input(OrePrefix.dust, Materials.DarkAsh)
                .fluidOutputs(new FluidStack(Fluids.CLOUD_SEED.getFluid(), 20000))
                .duration(200)
                .EUt(GTValues.VA[GTValues.EV])
                .buildAndRegister();

        RecipeMaps.VACUUM_RECIPES.recipeBuilder()
                .fluidInputs(new FluidStack(Fluids.CLOUD_SEED.getFluid(), 6000))
                .fluidOutputs(new FluidStack(Fluids.CLOUD_SEED_CONCENTRATED.getFluid(), 25))
                .duration(120)
                .EUt(GTValues.VA[GTValues.EV])
                .buildAndRegister();

        RecipeMaps.DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(new FluidStack(Fluids.ENDER_DISTILLATION.getFluid(), 8000))
                .fluidOutputs(
                        Materials.Helium.getFluid(2000),
                        Materials.Neon.getFluid(2000),
                        Materials.Argon.getFluid(2000),
                        Materials.Krypton.getFluid(500),
                        Materials.Xenon.getFluid(500),
                        Materials.Radon.getFluid(1000))
                .chancedOutput(Material.POWDER_PRECIENT.getStack(), 100, 10)
                .duration(800)
                .EUt(GTValues.VA[GTValues.EV])
                .buildAndRegister();


    }

    public static void cutterUpdate() {
        GTCEFuContent.log("Updating cutter recipes with Cloud Seed...");
        Collection<Recipe> oldRecipes = RecipeMaps.CUTTER_RECIPES.getRecipeList();
        oldRecipes.forEach(recipe -> {
            // replicate lubricant recipes, but halve the duration and switch to cloud seed
            if (recipe.hasInputFluid(Materials.Lubricant.getFluid(1))) {
                // Extrapolation of the greg formula for water, distilled water, and lubricant.
                int fluidAmount = (int) Math.max(50, Math.min(12500, recipe.getDuration() * recipe.getEUt() / 25.6));
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
        GTCEFuContent.log("Finished updating cutter recipes.");
    }
}
