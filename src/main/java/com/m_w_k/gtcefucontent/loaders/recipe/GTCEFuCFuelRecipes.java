package com.m_w_k.gtcefucontent.loaders.recipe;

import static com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials.ChargedEnder;
import static com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials.PreheatedWater;
import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;

import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.RecipeMaps;

public class GTCEFuCFuelRecipes {

    private GTCEFuCFuelRecipes() {}

    public static void init() {
        RecipeMaps.STEAM_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(PreheatedWater.getFluid(FluidStorageKeys.GAS, 4))
                .fluidOutputs(DistilledWater.getFluid(4))
                .duration(10)
                .EUt((int) V[MV])
                .buildAndRegister();

        RecipeMaps.PLASMA_GENERATOR_FUELS.recipeBuilder()
                .fluidInputs(Thorium.getPlasma(1))
                .fluidOutputs(Thorium.getFluid(1))
                .duration(532)
                .EUt((int) V[EV])
                .buildAndRegister();

        RecipeMaps.PLASMA_GENERATOR_FUELS.recipeBuilder()
                .fluidInputs(ChargedEnder.getFluid(1))
                .fluidOutputs(Beryllium.getFluid(1))
                .duration(750)
                .EUt((int) V[EV])
                .buildAndRegister();

        RecipeMaps.GAS_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(ChargedEnder.getFluid(1))
                .duration(700)
                .EUt((int) V[EV])
                .buildAndRegister();

        GTCEFuCRecipeMaps.NAQ_FUEL_CELL_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.NAQ_FUEL_CELL)
                .output(GTCEFuCMetaItems.NAQ_FUEL_CELL_EMPTY)
                .duration(20000).EUt((int) V[ZPM]).buildAndRegister();
    }
}
