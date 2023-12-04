package com.m_w_k.gtcefucontent.loaders.recipe;

import static com.m_w_k.gtcefucontent.api.fluids.GTCEFuCFluidStorageKeys.*;
import static com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials.*;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.api.recipes.HeatExchangerRecipeHandler;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCHeatCapacityProperty;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCPropertyKey;

import gregtech.api.GTValues;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;

public final class GTCEFuCEutecticLoader {

    private GTCEFuCEutecticLoader() {}

    public static void init() {
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, Materials.Caesium, 4)
                .input(OrePrefix.dust, Materials.Potassium, 4)
                .fluidInputs(Materials.SodiumPotassium.getFluid(1000))
                .fluidInputs(Materials.Oxygen.getFluid(FluidStorageKeys.LIQUID, 500))
                .fluidOutputs(
                        EutecticCaesiumSodiumPotassium.getFluid(COLD, 1000),
                        Materials.Oxygen.getFluid(500))
                .duration(1500).EUt(GTValues.VA[GTValues.MV]).buildAndRegister();

        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, Materials.Caesium, 16)
                .fluidInputs(Materials.Chlorine.getFluid(1000))
                .output(OrePrefix.dust, CaesiumChlorineMix, 17)
                .duration(4000).EUt(GTValues.VA[GTValues.MV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, CaesiumChlorineMix, 17)
                .fluidInputs(
                        EutecticCaesiumSodiumPotassium.getFluid(COLD, 1000),
                        Materials.NaquadahEnriched.getFluid(432),
                        Materials.Gallium.getFluid(576))
                .output(OrePrefix.dust, Materials.Salt)
                .fluidOutputs(EutecticCaesiumPotassiumGalliumNaquadahEnriched.getFluid(1000))
                .duration(160).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();

        GTCEFuCHeatCapacityProperty property;
        for (Material eutectic : EutecticAlloys.keySet()) {
            property = eutectic.getProperty(GTCEFuCPropertyKey.HEAT_CAPACITY);

            int heatDiff = eutectic.getFluid().getTemperature() - eutectic.getFluid(COLD).getTemperature();
            RecipeMaps.FLUID_HEATER_RECIPES.recipeBuilder()
                    .fluidInputs(eutectic.getFluid(COLD, 10))
                    .fluidOutputs(eutectic.getFluid(10))
                    .duration((int) (10L * property.getThermalCapacityFluid() * heatDiff /
                            (30 * HeatExchangerRecipeHandler.HEU)))
                    .EUt(30).buildAndRegister();
            RecipeMaps.VACUUM_RECIPES.recipeBuilder()
                    .fluidInputs(eutectic.getFluid(100))
                    .fluidOutputs(eutectic.getFluid(COLD, 100))
                    .duration((int) (100L * property.getThermalCapacityFluid() * heatDiff /
                            (60 * HeatExchangerRecipeHandler.HEU)))
                    .EUt(120).buildAndRegister();

            heatDiff = eutectic.getFluid(HOT).getTemperature() - eutectic.getFluid().getTemperature();
            RecipeMaps.FLUID_HEATER_RECIPES.recipeBuilder()
                    .fluidInputs(eutectic.getFluid(10))
                    .fluidOutputs(eutectic.getFluid(HOT, 10))
                    .duration((int) (10L * property.getThermalCapacityFluid() * heatDiff /
                            (30 * HeatExchangerRecipeHandler.HEU)))
                    .EUt(30).buildAndRegister();
            RecipeMaps.VACUUM_RECIPES.recipeBuilder()
                    .fluidInputs(eutectic.getFluid(HOT, 100))
                    .fluidOutputs(eutectic.getFluid(100))
                    .duration((int) (100L * property.getThermalCapacityFluid() * heatDiff /
                            (60 * HeatExchangerRecipeHandler.HEU)))
                    .EUt(120).buildAndRegister();
        }
    }
}
