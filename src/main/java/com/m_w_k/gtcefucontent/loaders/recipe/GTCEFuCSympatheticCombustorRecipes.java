package com.m_w_k.gtcefucontent.loaders.recipe;

import static com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps.SYMPATHETIC_COMBUSTOR_RECIPES;

import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;

import crazypants.enderio.base.fluid.Fluids;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;

public final class GTCEFuCSympatheticCombustorRecipes {

    private GTCEFuCSympatheticCombustorRecipes() {}

    private static int i = 1;

    public static void init() {
        RecipeBuilder<FuelRecipeBuilder> builder = SYMPATHETIC_COMBUSTOR_RECIPES.recipeBuilder()
                // 1000 LV tick production of coke (much more but idc)
                .input(OrePrefix.dust, Materials.Coke)
                .fluidInputs(Materials.Oxygen.getFluid(250));

        // 1.3x efficiency
        buildAndRegister(builder, 1300, 1);

        // 2996 LV tick production of methane; round up to 3000
        builder = builder
                .fluidInputs(Materials.Methane.getFluid(535));

        // 1.69x efficiency, truncated
        buildAndRegister(builder, 6760, 2);

        // 12000 LV tick production of rocket fuel
        builder = builder
                .fluidInputs(Materials.RocketFuel.getFluid(800));

        // 2.197x efficiency, truncated
        buildAndRegister(builder, 35152, 3);

        // 48000 LV tick production of HOG
        builder = builder
                .fluidInputs(Materials.HighOctaneGasoline.getFluid(300));

        // 2.8561x efficiency, truncated
        buildAndRegister(builder, 182790, 4);

        // 192000 LV tick production of charged ender
        builder = builder
                .fluidInputs(GTCEFuCMaterials.ChargedEnder.getFluid(4));

        // 3.7129x efficiency, truncated
        buildAndRegister(builder, 950510, 5);
    }

    private static void buildAndRegister(RecipeBuilder<FuelRecipeBuilder> builder, int duration, int tier) {
        int mod4 = (int) Math.pow(4, tier - 1);
        builder.EUt(mod4 * 32);
        builder.copy()
                .duration(duration / mod4)
                .circuitMeta(i)
                .buildAndRegister();
        // 1mb fire water = 100 more tick of LV production
        builder.copy().duration(duration * 2 / mod4)
                .fluidInputs(GTCEFuCMaterials.FireEnhancer.getFluid(duration / 1000))
                .circuitMeta(i + 10)
                .buildAndRegister();
        i++;
    }
}
