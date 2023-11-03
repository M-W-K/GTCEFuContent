package com.m_w_k.gtcefucontent.loaders.recipe;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.api.recipes.HeatExchangerRecipeHandler;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCPropertyKey;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCThreeTempFluidProperty;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;

public final class GTCEFuCHeatExchangerLoader {

    private GTCEFuCHeatExchangerLoader() {}

    // heat up by 80°K, then add the heat of vaporization
    public static final long waterVaporizationEnergy = 418600 * 80 + 2260000L;

    public static void init() {
        // water <-> steam
        HeatExchangerRecipeHandler.registerHeatExchange(
                Materials.DistilledWater.getFluid(1),
                Materials.Steam.getFluid(160),
                waterVaporizationEnergy);

        // plasmas
        Map<Material, Integer> plasmaMap = new HashMap<>();
        plasmaMap.put(Materials.Helium, 40 * 2048);
        plasmaMap.put(Materials.Oxygen, 48 * 2048);
        plasmaMap.put(Materials.Nitrogen, 64 * 2048);
        plasmaMap.put(Materials.Iron, 96 * 2048);
        plasmaMap.put(Materials.Nickel, 192 * 2048);
        for (Map.Entry<Material, Integer> entry : plasmaMap.entrySet()) {
            Material material = entry.getKey();
            HeatExchangerRecipeHandler.registerHeatExchange(material.getPlasma(1),
                    material.getFluid(1),
                    // 8L of steam potential per EU, or quadruple effective EU output before efficiency bonuses.
                    waterVaporizationEnergy * entry.getValue() / 20, false);
        }

        // eutectic alloys
        GTCEFuCThreeTempFluidProperty property;
        for (Material material : GTCEFuCMaterials.EutecticAlloys.keySet()) {
            property = material.getProperty(GTCEFuCPropertyKey.THREE_TEMP_FLUID);
            HeatExchangerRecipeHandler.registerHeatExchange(
                    material.getFluid(1),
                    new FluidStack(property.getFluidHot(), 1),
                    property.getThermalCapacity());
            HeatExchangerRecipeHandler.registerHeatExchange(
                    material.getFluid(1),
                    new FluidStack(property.getFluidCold(), 1),
                    property.getThermalCapacity());
        }
    }
}
