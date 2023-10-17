package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.GTCEFuContent;
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

    public static void init() {
        RecipeBuilder<FuelRecipeBuilder> builder = SYMPATHETIC_COMBUSTOR_RECIPES.recipeBuilder()
                //100 tick production of steam
                .fluidInputs(Materials.Steam.getFluid(7040))
                .input(OrePrefix.turbineBlade, Materials.Invar)
                .chancedOutput(OrePrefix.turbineBlade, Materials.Invar, 9900, -100)
                .EUt( (int) GTValues.V[GTValues.LV]);

        // 1.3x efficiency
        buildAndRegister(builder, 132);

        //980 tick production of methane
        builder = builder
                .fluidInputs(Materials.Methane.getFluid(175))
                .input(OrePrefix.dustTiny, Materials.NeodymiumMagnetic);

        // 1.69x efficiency, truncated
        buildAndRegister(builder, 1825);

        //960 tick production of HOG
        builder = builder
                .fluidInputs(Materials.HighOctaneGasoline.getFluid(6))
                .input(OrePrefix.dust, Materials.ArsenicTrioxide);

        // 2.197x efficiency, truncated
        buildAndRegister(builder, 4481);

        //960 tick production of rocket fuel
        builder = builder
                .fluidInputs(Materials.RocketFuel.getFluid(4))
                .input(OrePrefix.plate, Materials.IndiumGalliumPhosphide)
                .chancedOutput(OrePrefix.plate, Materials.IndiumGalliumPhosphide, 9900, -100);

        // 2.8561x efficiency, truncated
        buildAndRegister(builder, 8568);
    }

    private static void buildAndRegister(RecipeBuilder<FuelRecipeBuilder> builder, int duration) {
        builder.copy().duration(duration).buildAndRegister();
        builder.copy().duration(duration * 2)
                .fluidInputs(new FluidStack(Fluids.FIRE_WATER.getFluid(), duration))
                .input(OrePrefix.bolt, Materials.Naquadah)
                .chancedOutput(OrePrefix.bolt, Materials.Naquadah, 9990, -10)
                .buildAndRegister();
    }
}
