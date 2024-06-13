package com.m_w_k.gtcefucontent.loaders.recipe;

import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.getTemp;

import java.util.*;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;
import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.api.recipes.HalfExchangeData;
import com.m_w_k.gtcefucontent.api.recipes.HeatExchangerRecipeHandler;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;

import gregicality.multiblocks.api.fluids.GCYMFluidStorageKeys;
import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.properties.PropertyKey;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public final class GTCEFuCHeatExchangerLoader {

    private GTCEFuCHeatExchangerLoader() {}

    public static final int WATER_HEAT_CAPACITY = 418600;
    public static final long WATER_HEAT_OF_VAPORIZATION = 2260000L;
    /**
     * 1L water -> 160L steam
     */
    // heat up by 80°K, then add the heat of vaporization
    public static final long WATER_TO_STEAM_ENERGY = WATER_HEAT_CAPACITY * 80 + WATER_HEAT_OF_VAPORIZATION;
    /**
     * Thermal energy for a single EU, assuming 2L steam is 1 EU.
     */
    public static final long SINGLE_EU_ENERGY = WATER_TO_STEAM_ENERGY / 80;

    private static Map<Fluid, Material> plasmas;
    private static Map<Fluid, Material> moltens;

    private static void collectMaterialMaps() {
        plasmas = new Object2ObjectOpenHashMap<>();
        moltens = new Object2ObjectOpenHashMap<>();
        GregTechAPI.materialManager.getRegisteredMaterials().stream()
                .filter((a) -> a.hasProperty(PropertyKey.FLUID))
                .forEach(a -> {
                    Fluid test = a.getFluid(FluidStorageKeys.PLASMA);
                    if (test != null) plasmas.put(test, a);
                    test = a.getFluid(GCYMFluidStorageKeys.MOLTEN);
                    if (test != null && a.getFluid(FluidStorageKeys.LIQUID) != null) moltens.put(test, a);
                });
    }

    private static void discardMaterialMaps() {
        plasmas = null;
        moltens = null;
    }

    public static void init() {
        collectMaterialMaps();

        // water <-> l. ice
        HeatExchangerRecipeHandler.registerHeatExchangeC(
                Materials.Ice.getFluid(1),
                Materials.Water.getFluid(1),
                WATER_HEAT_CAPACITY);

        // water <-> steam
        HeatExchangerRecipeHandler.registerHeatExchangeE(
                Materials.Water.getFluid(1),
                Materials.Steam.getFluid(160),
                WATER_TO_STEAM_ENERGY);

        // d. water <-> preheated water
        HeatExchangerRecipeHandler.registerHeatExchangeC(
                Materials.DistilledWater.getFluid(1),
                GTCEFuCMaterials.PreheatedWater.getFluid(1),
                WATER_HEAT_CAPACITY);

        // preheated water <-> hps
        HeatExchangerRecipeHandler.registerHeatExchangeE(
                GTCEFuCMaterials.PreheatedWater.getFluid(1),
                GTCEFuCMaterials.PreheatedWater.getFluid(FluidStorageKeys.GAS, 1),
                WATER_HEAT_OF_VAPORIZATION);

        // oxygen <-> l. oxygen
        HeatExchangerRecipeHandler.registerHeatExchangeE(
                Materials.Oxygen.getFluid(FluidStorageKeys.GAS, 1),
                Materials.Oxygen.getFluid(FluidStorageKeys.LIQUID, 1),
                430 * SINGLE_EU_ENERGY);

        // helium <-> l. helium
        HeatExchangerRecipeHandler.registerHeatExchangeE(
                Materials.Helium.getFluid(FluidStorageKeys.GAS, 1),
                Materials.Helium.getFluid(FluidStorageKeys.LIQUID, 1),
                340 * SINGLE_EU_ENERGY);

        // l. airs
        air(Materials.Air, Materials.LiquidAir, 6);
        air(Materials.NetherAir, Materials.LiquidNetherAir, 34);
        air(Materials.EnderAir, Materials.LiquidEnderAir, 125);

        // plasmas
        Collection<Recipe> recipes = RecipeMaps.PLASMA_GENERATOR_FUELS.getRecipeList();
        recipes.forEach((entry) -> {
            // only true plasmas that are processed via plasma generator are registered
            Material material = plasmas.get(entry.getFluidInputs().get(0).getInputFluidStack().getFluid());
            if (material != null) {
                long eu = (long) entry.getEUt() * entry.getDuration();
                HeatExchangerRecipeHandler.registerHeatExchangeE(
                        material.getPlasma(1),
                        material.getFluid(1),
                        // 4L of steam potential per EU, or double effective EU output before efficiency bonuses.
                        SINGLE_EU_ENERGY * eu * 2, false);
            }
        });

        // molten alloys
        recipes = RecipeMaps.VACUUM_RECIPES.getRecipeList();
        recipes.forEach((entry) -> {
            Material material = null;
            int quant = 1;
            for (int i = 0; i < entry.getFluidInputs().size(); i++) {
                Material molten = moltens.get(entry.getFluidInputs().get(i).getInputFluidStack().getFluid());
                if (molten != null) {
                    if (material == null) {
                        material = molten;
                        quant = entry.getFluidInputs().get(i).getAmount();
                    }
                    // multiple molten materials in input is incomprehensible behavior
                    else return;
                }
            }
            if (material != null) {
                // datafix so that moltens are at least 1K hotter than the liquids, so heat exchanges don't / 0
                Fluid molten = material.getFluid(GCYMFluidStorageKeys.MOLTEN);
                Fluid liquid = material.getFluid(FluidStorageKeys.LIQUID);
                if (molten.getTemperature() <= liquid.getTemperature())
                    molten.setTemperature(liquid.getTemperature() + 1);

                long eu = (long) entry.getEUt() * entry.getDuration();
                HeatExchangerRecipeHandler.registerHeatExchangeE(
                        material.getFluid(GCYMFluidStorageKeys.MOLTEN, 1),
                        material.getFluid(FluidStorageKeys.LIQUID, 1),
                        // ignore helium stack, but other than that all the cost turns into pure profit
                        SINGLE_EU_ENERGY * eu / quant, false);
            }
        });

        // custom conversions
        HeatExchangerRecipeHandler.registerHeatExchangeE(
                GTCEFuCMaterials.TriniumReduced.getFluid(55),
                Materials.Trinium.getFluid(9),
                WATER_TO_STEAM_ENERGY * GTValues.V[GTValues.UV],
                false);
    }

    private static void air(Material air, Material lair, int energy) {
        HeatExchangerRecipeHandler.registerHeatExchangeE(
                air.getFluid(1),
                lair.getFluid(1),
                energy * SINGLE_EU_ENERGY);
    }

    public static void postInit() {
        handleExchangeMap(HeatExchangerRecipeHandler.getHeatingMapCopy());
        handleExchangeMap(HeatExchangerRecipeHandler.getCoolingMapCopy());
        handleEutectics(HeatExchangerRecipeHandler.getEutecticsCopy());
    }

    private static void handleExchangeMap(Map<Fluid, HalfExchangeData> map) {
        for (HalfExchangeData data : map.values()) {
            handleExchangeData(data);
        }
    }

    private static void handleEutectics(Set<EutecticFluid> eutectics) {
        for (EutecticFluid eutectic : eutectics) {
            FluidStack base = new FluidStack(eutectic, 1);
            FluidStack cold = eutectic.stackWithTemperature(1, 0);
            FluidStack hot = eutectic.stackWithTemperature(1, Integer.MAX_VALUE);
            handleExchangeData(new HalfExchangeData(base, hot, eutectic.getThermalCapacity()));
            handleExchangeData(new HalfExchangeData(cold, base, eutectic.getThermalCapacity()));
            handleExchangeData(new HalfExchangeData(hot, base, eutectic.getThermalCapacity()));
            handleExchangeData(new HalfExchangeData(base, cold, eutectic.getThermalCapacity()));
        }
    }

    private static void handleExchangeData(HalfExchangeData data) {
        GTCEFuCRecipeMaps.EXCHANGER_PLACEHOLDER_MAP.recipeBuilder()
                .circuitMeta((getTemp(data.out) > getTemp(data.in)) ? 2 : 1)
                .fluidInputs(data.in)
                .fluidOutputs(data.out)
                .heatToConvert(data.thermalEnergy)
                // the recipemap doesn't actually do anything, it's just there for JEI visualization
                .EUt(1).duration(1).buildAndRegister();
    }
}
