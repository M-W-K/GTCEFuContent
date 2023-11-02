package com.m_w_k.gtcefucontent.api.unification;

import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.gtcefucId;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.DECOMPOSITION_BY_CENTRIFUGING;
import static gregtech.api.unification.material.info.MaterialFlags.DECOMPOSITION_BY_ELECTROLYZING;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.m_w_k.gtcefucontent.api.recipes.HeatExchangerRecipeHandler;
import net.minecraft.util.ResourceLocation;

import com.m_w_k.gtcefucontent.api.fluids.fluidType.GTCEFuCFluidTypes;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCPropertyKey;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCThreeTempFluidProperty;

import gregicality.multiblocks.api.fluids.GCYMMetaFluids;
import gregtech.api.fluids.MetaFluids;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconSet;
import net.minecraftforge.fluids.Fluid;

public class GTCEFuCMaterials {

    public static Material EutecticCaesiumSodiumPotassium;
    public static Material EutecticCaesiumPotassiumGalliumNaquadahEnriched;

    public static Material CaesiumChlorineMix;
    public static Material ChargedEnder;

    /**
     * Contains a map of eutectic alloys and their temperatures before initialization
     * <br>
     * String - The name of this eutectic alloy
     * <br>
     * int[0] - The temperature of the cold alloy
     * <br>
     * int[1] - The temperature of the normal alloy
     * <br>
     * int[1] - The temperature of the hot alloy
     * <br>
     * int[2] - The heat capacity, in units of J/L.
     * This means that 418600 units would increase the temperature of 1L water by 1°K,
     * as the thermal capacity of water is 418.6 kJ/kg, or kJ/L.
     * A negative specific heat capacity violates the laws of thermodynamics.
     */
    public final static Map<String, int[]> EutecticAlloysString = new HashMap<>() {

        {
            this.put("eutectic_csnak_alloy", new int[] { 195, 293, 1237, 200000 });
            this.put("eutectic_enriched_naquadah_gallium_csk_alloy", new int[] { 270, 293, 7419, 500000 });
        }
    };

    /**
     * Contains a map of eutectic alloys and their temperatures after initialization
     * <br>
     * Material - The Material of this eutectic alloy
     * <br>
     * int[0] - The temperature of the cold alloy
     * <br>
     * int[1] - The temperature of the normal alloy
     * <br>
     * int[2] - The temperature of the hot alloy
     * <br>
     * int[2] - The specific heat capacity, in units of J/L.
     * This means that 418600 units would increase the temperature of 1L water by 1°K,
     * as the thermal capacity of water is 418.6 kJ/kg, or kJ/L.
     * A negative specific heat capacity violates the laws of thermodynamics.
     */
    public final static Map<Material, int[]> EutecticAlloys = new HashMap<>();

    private static final AtomicBoolean INIT = new AtomicBoolean(false);

    // IDs 22500 - 22529 reserved for multiblocks
    // IDs 22530 - 22559 reserved for normal metaTileEntities
    // IDs 22560 - 22599 reserved for materials
    private static final AtomicInteger ID = new AtomicInteger(22560);

    public static void register() {
        if (INIT.getAndSet(true)) {
            return;
        }

        GTCEFuCMaterialFlagAddition.init();

        CaesiumChlorineMix = new Material.Builder(ID.getAndIncrement(), gtcefucId("caesium_chlorine_mix"))
                .dust()
                .colorAverage().iconSet(MaterialIconSet.FINE)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .components(Caesium, 16, Chlorine, 1)
                .build();

        ChargedEnder = new Material.Builder(ID.getAndIncrement(), gtcefucId("charged_ender"))
                .fluid()
                .color(0xa0cefa).iconSet(MaterialIconSet.FLUID)
                .build();

        // the csnak is a c kind of snake
        EutecticCaesiumSodiumPotassium = new Material.Builder(ID.getAndIncrement(), gtcefucId("eutectic_csnak_alloy"))
                .fluid().fluidTemp(EutecticAlloysString.get("eutectic_csnak_alloy")[1])
                .colorAverage().iconSet(MaterialIconSet.METALLIC)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .components(Caesium, 4, Sodium, 1, Potassium, 5)
                .build();

        EutecticCaesiumPotassiumGalliumNaquadahEnriched = new Material.Builder(ID.getAndIncrement(),
                gtcefucId("eutectic_enriched_naquadah_gallium_csk_alloy"))
                        .fluid().fluidTemp(EutecticAlloysString.get("eutectic_enriched_naquadah_gallium_csk_alloy")[1])
                        .colorAverage().iconSet(MaterialIconSet.METALLIC)
                        .flags(DECOMPOSITION_BY_ELECTROLYZING)
                        .components(Potassium, 5, Caesium, 20, Gallium, 4, NaquadahEnriched, 3)
                        .build();

        populateEutecticMap();

        // generate the hot and cold versions of the eutectic alloys
        for (Material eutecticAlloy : EutecticAlloys.keySet()) {
            eutecticAlloy.setProperty(GTCEFuCPropertyKey.THREE_TEMP_FLUID, new GTCEFuCThreeTempFluidProperty(
                    EutecticAlloys.get(eutecticAlloy)[0],
                    EutecticAlloys.get(eutecticAlloy)[2],
                    EutecticAlloys.get(eutecticAlloy)[3]));

            MetaFluids.setMaterialFluidTexture(eutecticAlloy, GTCEFuCFluidTypes.HOT,
                    GCYMMetaFluids.AUTO_GENERATED_MOLTEN_TEXTURE);
            MetaFluids.setMaterialFluidTexture(eutecticAlloy, GTCEFuCFluidTypes.COLD,
                    new ResourceLocation("gregtech", "blocks/material_sets/fluid/fluid"));

            Fluid cold = MetaFluids.registerFluid(eutecticAlloy, GTCEFuCFluidTypes.COLD,
                    EutecticAlloys.get(eutecticAlloy)[0],
                    false);
            Fluid hot = MetaFluids.registerFluid(eutecticAlloy, GTCEFuCFluidTypes.HOT,
                    EutecticAlloys.get(eutecticAlloy)[2],
                    false);
            eutecticAlloy.getProperty(GTCEFuCPropertyKey.THREE_TEMP_FLUID).setFluidCold(
                    cold);
            eutecticAlloy.getProperty(GTCEFuCPropertyKey.THREE_TEMP_FLUID).setFluidHot(
                    hot);

            HeatExchangerRecipeHandler.addEutectic(cold, eutecticAlloy.getFluid(), hot);
        }
    }

    private static void populateEutecticMap() {
        // 194K to 1238K (boiling point is made up, but this is a real eutectic mixture)
        EutecticAlloys.put(EutecticCaesiumSodiumPotassium, EutecticAlloysString.get("eutectic_csnak_alloy"));
        // 269K to 7420K
        EutecticAlloys.put(EutecticCaesiumPotassiumGalliumNaquadahEnriched,
                EutecticAlloysString.get("eutectic_enriched_naquadah_gallium_csk_alloy"));
    }
}
