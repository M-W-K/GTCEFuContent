package com.m_w_k.gtcefucontent.loaders.recipe;

import static com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials.*;

import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;
import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
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
                        getEutectic(EutecticCaesiumSodiumPotassium, 1000, 0),
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
                        getEutectic(EutecticCaesiumSodiumPotassium, 1000, 0),
                        Materials.NaquadahEnriched.getFluid(432),
                        Materials.Gallium.getFluid(576))
                .output(OrePrefix.dust, Materials.Salt)
                .fluidOutputs(EutecticCaesiumPotassiumGalliumNaquadahEnriched.getFluid(1000))
                .duration(160).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();

        GTCEFuCHeatCapacityProperty property;
        for (Material eutectic : EutecticAlloys.keySet()) {
            property = eutectic.getProperty(GTCEFuCPropertyKey.HEAT_CAPACITY);
            EutecticFluid fluid = (EutecticFluid) eutectic.getFluid();

            int heatDiff = fluid.getTemperature() - fluid.getMinTemperature();
            RecipeMaps.FLUID_HEATER_RECIPES.recipeBuilder()
                    .fluidInputs(fluid.stackWithTemperature(10, 0))
                    .fluidOutputs(eutectic.getFluid(10))
                    .duration((int) (10L * property.getThermalCapacityFluid() * heatDiff /
                            (30 * GTCEFuCHeatExchangerLoader.SINGLE_EU_ENERGY)))
                    .EUt(30).buildAndRegister();
            RecipeMaps.VACUUM_RECIPES.recipeBuilder()
                    .fluidInputs(eutectic.getFluid(100))
                    .fluidOutputs(fluid.stackWithTemperature(100, 0))
                    .duration((int) (100L * property.getThermalCapacityFluid() * heatDiff /
                            (120 * GTCEFuCHeatExchangerLoader.SINGLE_EU_ENERGY)))
                    .EUt(120).buildAndRegister();

            heatDiff = fluid.getMaxTemperature() - fluid.getTemperature();
            RecipeMaps.FLUID_HEATER_RECIPES.recipeBuilder()
                    .fluidInputs(eutectic.getFluid(10))
                    .fluidOutputs(fluid.stackWithTemperature(10, Integer.MAX_VALUE))
                    .duration((int) (10L * property.getThermalCapacityFluid() * heatDiff /
                            (30 * GTCEFuCHeatExchangerLoader.SINGLE_EU_ENERGY)))
                    .EUt(30).buildAndRegister();
            RecipeMaps.VACUUM_RECIPES.recipeBuilder()
                    .fluidInputs(fluid.stackWithTemperature(100, Integer.MAX_VALUE))
                    .fluidOutputs(eutectic.getFluid(100))
                    .duration((int) (100L * property.getThermalCapacityFluid() * heatDiff /
                            (120 * GTCEFuCHeatExchangerLoader.SINGLE_EU_ENERGY)))
                    .EUt(120).buildAndRegister();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static FluidStack getEutectic(Material eutectic, int amount, int temperature) {
        EutecticFluid fluid = (EutecticFluid) eutectic.getFluid();
        return fluid.stackWithTemperature(amount, temperature);
    }
}
