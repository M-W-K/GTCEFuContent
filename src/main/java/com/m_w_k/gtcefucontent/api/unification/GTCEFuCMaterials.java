package com.m_w_k.gtcefucontent.api.unification;

import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.gtcefucId;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;

import com.m_w_k.gtcefucontent.api.fluids.fluidType.GTCEFuCFluidTypes;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCHotFluidProperty;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCPropertyKey;
import gregicality.multiblocks.api.fluids.GCYMMetaFluids;
import gregtech.api.fluids.MetaFluids;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconSet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GTCEFuCMaterials {

    public static Material EutecticCaesiumSodiumPotassium;
    public static Material EutecticCaesiumPotassiumGalliumNaquadahEnriched;

    public static Material CaesiumChlorineMix;

    private static final AtomicBoolean INIT = new AtomicBoolean(false);

    /**
     * Contains a map of eutectic alloys and their temperatures before initialization
     * <p>String - The name of this eutectic alloy</p>
     * <p>int[0] - The temperature of the cold alloy</p>
     * <p>int[1] - The temperature of the hot alloy</p>
     * <p>int[2] - The heat capacity, in units of 1/100 waters. E.g. 1/4 the heat capacity of water would be 25.
     * These units are used because, if we ever get the energy back out of the eutectic alloy, it'll be heating up water.</p>
     */
    public final static Map<String, int[]> EutecticAlloysString = new HashMap<>() {{
        this.put("eutectic_csnak_alloy", new int[]{195, 1237, 23});
        this.put("eutectic_enriched_naquadah_gallium_csk_alloy", new int[]{270, 9419, 80});
    }};

    /**
     * Contains a map of eutectic alloys and their temperatures after initialization
     * <p>Material - The Material of this eutectic alloy</p>
     * <p>int[0] - The temperature of the cold alloy</p>
     * <p>int[1] - The temperature of the hot alloy</p>
     * <p>int[2] - The heat capacity, in units of 1/100 waters. E.g. 1/4 the heat capacity of water would be 25.
     * These units are used because, if we ever get the energy back out of the eutectic alloy, it'll be heating up water.</p>
     */
    public final static Map<Material, int[]> EutecticAlloys = new HashMap<>();

    public static void register() {
        if (INIT.getAndSet(true)) {
            return;
        }

        GTCEFuCMaterialFlagAddition.init();

        CaesiumChlorineMix = new Material.Builder(22560, gtcefucId("caesium_chlorine_mix"))
                .dust()
                .colorAverage().iconSet(MaterialIconSet.FINE)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .components(Caesium, 16, Chlorine, 1)
                .build();

        EutecticCaesiumSodiumPotassium = new Material.Builder(22561, gtcefucId("eutectic_csnak_alloy")) // the csnak is a c kind of snake
                .fluid().fluidTemp(EutecticAlloysString.get("eutectic_csnak_alloy")[0])
                .colorAverage().iconSet(MaterialIconSet.METALLIC)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .components(Caesium, 4, Sodium, 1, Potassium, 5)
                .build();

        EutecticCaesiumPotassiumGalliumNaquadahEnriched = new Material.Builder(22562, gtcefucId("eutectic_enriched_naquadah_gallium_csk_alloy"))
                .fluid().fluidTemp(EutecticAlloysString.get("eutectic_enriched_naquadah_gallium_csk_alloy")[0])
                .colorAverage().iconSet(MaterialIconSet.METALLIC)
                .flags(DECOMPOSITION_BY_ELECTROLYZING)
                .components(Potassium, 5, Caesium, 20, Gallium, 4, NaquadahEnriched, 3)
                .build();

        populateEutecticMap();

        // generate the hot versions of the eutectic alloys
        for (Material eutecticAlloy : EutecticAlloys.keySet()) {
            eutecticAlloy.setProperty(GTCEFuCPropertyKey.HOT_FLUID, new GTCEFuCHotFluidProperty(EutecticAlloys.get(eutecticAlloy)[1]));
            MetaFluids.setMaterialFluidTexture(eutecticAlloy, GTCEFuCFluidTypes.HOT, GCYMMetaFluids.AUTO_GENERATED_MOLTEN_TEXTURE);
            eutecticAlloy.getProperty(GTCEFuCPropertyKey.HOT_FLUID).setFluid(
                    MetaFluids.registerFluid(eutecticAlloy, GTCEFuCFluidTypes.HOT, EutecticAlloys.get(eutecticAlloy)[1], false));
        }
    }

    private static void populateEutecticMap() {
        // 194K to 1238K (boiling point is made up, but this is a real eutectic mixture)
        EutecticAlloys.put(EutecticCaesiumSodiumPotassium, EutecticAlloysString.get("eutectic_csnak_alloy"));
        // 269K to 9420K
        EutecticAlloys.put(EutecticCaesiumPotassiumGalliumNaquadahEnriched, EutecticAlloysString.get("eutectic_enriched_naquadah_gallium_csk_alloy"));
    }
}
