package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import crazypants.enderio.base.fluid.Fluids;
import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import net.minecraftforge.fluids.FluidStack;

import static com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps.SYMPATHETIC_COMBUSTOR_RECIPES;

public class GTCEFuCSympatheticCombustorRecipes {
    private GTCEFuCSympatheticCombustorRecipes() {}

    private static int i = 1;
    public static void init() {
        RecipeBuilder<FuelRecipeBuilder> builder = SYMPATHETIC_COMBUSTOR_RECIPES.recipeBuilder()
                //1000 LV tick production of coke (much more but idc)
                .input(OrePrefix.dust, Materials.Coke)
                .fluidInputs(Materials.Oxygen.getFluid(250))
                .EUt( (int) GTValues.V[GTValues.LV]);

        // 1.3x efficiency
        buildAndRegister(builder, 1300);

        //2996 LV tick production of methane; round up to 3000
        builder = builder
                .EUt( (int) GTValues.V[GTValues.MV]).fluidInputs(Materials.Methane.getFluid(535));

        // 1.69x efficiency, truncated
        buildAndRegister(builder, 6760 / 4);

        //12000 LV tick production of rocket fuel
        builder = builder
                .EUt( (int) GTValues.V[GTValues.HV]).fluidInputs(Materials.RocketFuel.getFluid(800));

        // 2.197x efficiency, truncated
        buildAndRegister(builder, 35152 / 16);

        //48000 LV tick production of HOG
        builder = builder
                .EUt( (int) GTValues.V[GTValues.EV]).fluidInputs(Materials.HighOctaneGasoline.getFluid(300));

        // 2.8561x efficiency, truncated
        buildAndRegister(builder, 182790 / 64);

        //192000 LV tick production of charged ender
        builder = builder
                .EUt( (int) GTValues.V[GTValues.IV]).fluidInputs(GTCEFuCMaterials.ChargedEnder.getFluid(4));

        // 3.7129x efficiency, truncated
        buildAndRegister(builder, 950510 / 256);

    }

    private static void buildAndRegister(RecipeBuilder<FuelRecipeBuilder> builder, int duration) {
        builder.copy()
                .duration(duration)
                .circuitMeta(i)
                .buildAndRegister();
        // 1mb fire water = 100 more tick of LV production
        builder.copy().duration(duration * 2)
                .fluidInputs(new FluidStack(Fluids.FIRE_WATER.getFluid(), duration * builder.getEUt() / 3200))
                .circuitMeta(i + 10)
                .buildAndRegister();
        i++;
    }
}
