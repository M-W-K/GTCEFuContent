package com.m_w_k.gtcefucontent.loaders.recipe;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.api.fluids.GTCEFuCFluidStorageKeys;
import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.api.recipes.HeatExchangerRecipeHandler;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCHeatCapacityProperty;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCPropertyKey;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.properties.PropertyKey;

public final class GTCEFuCHeatExchangerLoader {

    private GTCEFuCHeatExchangerLoader() {}

    public static final long waterHeatCapacity = 418600;
    public static final long waterHeatOfVaporization = 2260000L;
    // heat up by 80°K, then add the heat of vaporization
    public static final long waterToSteamEnergy = waterHeatCapacity * 80 + waterHeatOfVaporization;

    public static void init() {
        // water <-> steam
        HeatExchangerRecipeHandler.registerHeatExchange(
                Materials.Water.getFluid(1),
                Materials.Steam.getFluid(160),
                waterToSteamEnergy);

        // d. water <-> preheated water
        HeatExchangerRecipeHandler.registerHeatExchange(
                Materials.DistilledWater.getFluid(1),
                GTCEFuCMaterials.PreheatedWater.getFluid(1),
                waterHeatCapacity * 80);

        // preheated water <-> hps
        HeatExchangerRecipeHandler.registerHeatExchange(
                GTCEFuCMaterials.PreheatedWater.getFluid(1),
                GTCEFuCMaterials.PreheatedWater.getFluid(FluidStorageKeys.GAS, 1),
                waterHeatOfVaporization);

        // plasmas
        Collection<Recipe> recipes = RecipeMaps.PLASMA_GENERATOR_FUELS.getRecipeList();
        Map<Fluid, Material> plasmas = GregTechAPI.materialManager.getRegisteredMaterials().stream()
                .filter((a) -> a.hasProperty(PropertyKey.FLUID) && a.getFluid(FluidStorageKeys.PLASMA) != null)
                .collect(Collectors.toMap((a) -> a.getFluid(FluidStorageKeys.PLASMA), Function.identity()));

        recipes.forEach((entry) -> {
            // only true plasmas that are processed via plasma generator are registered
            Material material = plasmas.get(entry.getFluidInputs().get(0).getInputFluidStack().getFluid());
            if (material != null) {
                int eu = entry.getEUt() * entry.getDuration();
                HeatExchangerRecipeHandler.registerHeatExchange(material.getPlasma(1),
                        material.getFluid(1),
                        // 4L of steam potential per EU, or double effective EU output before efficiency bonuses.
                        waterToSteamEnergy * eu / 40, false);
            }
        });

        // eutectic alloys
        GTCEFuCHeatCapacityProperty property;
        for (Material material : GTCEFuCMaterials.EutecticAlloys.keySet()) {
            property = material.getProperty(GTCEFuCPropertyKey.HEAT_CAPACITY);
            HeatExchangerRecipeHandler.registerHeatExchange(
                    material.getFluid(1),
                    new FluidStack(material.getFluid(GTCEFuCFluidStorageKeys.HOT), 1),
                    property.getThermalCapacityFluid());
            HeatExchangerRecipeHandler.registerHeatExchange(
                    material.getFluid(1),
                    new FluidStack(material.getFluid(GTCEFuCFluidStorageKeys.COLD), 1),
                    property.getThermalCapacityFluid());
            // register as a eutectic
            HeatExchangerRecipeHandler.addEutectic(material.getFluid(),
                    material.getFluid(GTCEFuCFluidStorageKeys.COLD),
                    material.getFluid(GTCEFuCFluidStorageKeys.HOT));
        }

        // custom conversions
        HeatExchangerRecipeHandler.registerHeatExchange(
                GTCEFuCMaterials.TriniumReduced.getFluid(55),
                Materials.Trinium.getFluid(9),
                waterToSteamEnergy * GTValues.V[GTValues.UV],
                false);
    }

    public static void postInit() {
        handleExchangeMap(HeatExchangerRecipeHandler.getHeatingMapCopy(), 1);
        handleExchangeMap(HeatExchangerRecipeHandler.getCoolingMapCopy(), -1);
    }

    private static void handleExchangeMap(Map<Fluid, Tuple<FluidStack, long[]>> map, int heating) {
        for (Map.Entry<Fluid, Tuple<FluidStack, long[]>> entry : map.entrySet()) {
            Tuple<FluidStack, long[]> tuple = entry.getValue();
            GTCEFuCRecipeMaps.EXCHANGER_PLACEHOLDER_MAP.recipeBuilder()
                    .circuitMeta(heating + 2)
                    .fluidInputs(new FluidStack(entry.getKey(), (int) tuple.getSecond()[0]))
                    .fluidOutputs(tuple.getFirst())
                    .heatToConvert(tuple.getSecond()[1] * heating)
                    // the recipemap doesn't actually do anything, it's just there for JEI visualization
                    .EUt(1).duration(1).buildAndRegister();
        }
    }
}
