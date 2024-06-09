package com.m_w_k.gtcefucontent.api.unification;

import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.*;
import static gregtech.api.GTValues.LuV;
import static gregtech.api.GTValues.VA;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import gregtech.api.GTValues;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCHeatCapacityProperty;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCPropertyKey;

import crafttweaker.annotations.ZenRegister;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.attribute.FluidAttributes;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.BlastProperty;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("mods.gtcefucontent.material.Material")
@ZenRegister
public final class GTCEFuCMaterials {

    public static Material EutecticCaesiumSodiumPotassium;
    public static Material EutecticCaesiumPotassiumGalliumNaquadahEnriched;

    public static Material PreheatedWater;
    public static Material CaesiumChlorineMix;
    public static Material ChargedEnder;
    public static Material TriniumReduced;
    public static Material VoidEssence;
    public static Material LightEssence;
    public static Material ExperienceEssence;
    public static Material FireEnhancer;
    public static Material VaporSeedRaw;
    public static Material VaporSeed;

    public static Material NaquadricAlloy;


    /**
     * Contains a map of eutectic alloys and their temperatures before initialization
     * <br>
     * String - The name of this eutectic alloy
     * <br>
     * int[0] - The minimum temperature of the alloy
     * <br>
     * int[1] - The default temperature of the alloy
     * <br>
     * int[2] - The maximum temperature of the alloy
     * <br>
     * int[3] - The heat capacity, in units of J/L.
     * This means that 418600 units would increase the temperature of 1L water by 1°K,
     * as the thermal capacity of water is 418.6 kJ/kg, or kJ/L.
     * A negative specific heat capacity violates the laws of thermodynamics.
     */
    public final static Map<String, int[]> EutecticAlloysString = new HashMap<>() {

        {
            this.put("eutectic_csnak_alloy", new int[] { 195, 293, 1237, 800000 });
            this.put("eutectic_enriched_naquadah_gallium_csk_alloy", new int[] { 270, 293, 7419, 10000000 });
        }
    };

    /**
     * Contains a map of eutectic alloys and their temperatures after initialization
     * <br>
     * Material - The Material of this eutectic alloy
     * <br>
     * int[0] - The minimum temperature of the alloy
     * <br>
     * int[1] - The default temperature of the alloy
     * <br>
     * int[2] - The maximum temperature of the alloy
     * <br>
     * int[3] - The specific heat capacity, in units of J/L.
     * This means that 418600 units would increase the temperature of 1L water by 1°K,
     * as the thermal capacity of water is 418.6 kJ/kg, or kJ/L.
     * A negative specific heat capacity violates the laws of thermodynamics.
     */
    public final static Map<Material, int[]> EutecticAlloys = new HashMap<>();

    private static final AtomicBoolean INIT = new AtomicBoolean(false);

    // IDs 22500 - 22529 reserved for multiblocks
    // IDs 22530 - 22549 reserved for normal metaTileEntities
    // IDs 22570 - 22589 reserved for materials
    // IDs 22590 - 22599 reserved for eutectics
    private static int id = 22569;
    private static int eu = 22589;

    public static void register() {
        if (INIT.getAndSet(true)) {
            return;
        }

        GTCEFuCMaterialFlagAddition.init();

        PreheatedWater = new Material.Builder(id++, gtcefucId("preheated_water"))
                .liquid(new FluidBuilder().temperature(373))
                .gas(new FluidBuilder().temperature(973).customStill().name("hps")
                        .translation("gtcefucontent.fluid.hps"))
                .color(0x4A94EE)
                .flags(DISABLE_DECOMPOSITION)
                .components(Hydrogen, 2, Oxygen, 1)
                .build();

        CaesiumChlorineMix = new Material.Builder(id++, gtcefucId("caesium_chlorine_mix"))
                .dust()
                .colorAverage().iconSet(FINE)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .components(Caesium, 16, Chlorine, 1)
                .build();

        ChargedEnder = new Material.Builder(id++, gtcefucId("charged_ender"))
                .fluid()
                .color(0xa0cefa).iconSet(FLUID)
                .build();

        TriniumReduced = new Material.Builder(id++, gtcefucId("reduced_trinium"))
                .ingot().fluid()
                .color(0x5E4C6E).iconSet(DULL)
                .element(GTCEFuCElements.Ke1)
                .blast((b) -> b.temp(5200, BlastProperty.GasTier.HIGH).blastStats(VA[LuV], 900))
                .build();

        VoidEssence = essence(id++, gtcefucId("void_essence"))
                .color(0x33523F)
                // .components(Helium, 4, Neon, 4, Argon, 4, Krypton, 1, Xenon, 1, Radon, 2)
                .build();

        LightEssence = essence(id++, gtcefucId("light_essence"))
                .color(0xFFF997)
                .build();

        ExperienceEssence = essence(id++, gtcefucId("experience_essence"))
                .color(0x04BB00)
                .build();

        FireEnhancer = new Material.Builder(id++, gtcefucId("fire_enhancer"))
                .gas(fluidAtTemp(2000))
                .color(0xD69A00).iconSet(FLUID)
                .build();

        VaporSeedRaw = new Material.Builder(id++, gtcefucId("raw_vapor_seed"))
                .gas(fluidAtTemp(300))
                .color(0x20FFF0).iconSet(FLUID)
                .build();

        VaporSeed = new Material.Builder(id++, gtcefucId("vapor_seed"))
                .liquid(fluidAtTemp(100))
                .color(0x003B78).iconSet(FLUID)
                .build();

        NaquadricAlloy = new Material.Builder(id++, gtcefucId("naquadric_alloy"))
                .ingot().fluid()
                .colorAverage().iconSet(SHINY)
                .flags(GENERATE_PLATE)
                .fluidPipeProperties(9530, 230, false, false, false, true)
                .components(Naquadria, 3, Thorium, 5, Cadmium, 1, Lead, 1)
                .blast(b -> b.temp(7340, BlastProperty.GasTier.HIGHER)
                        .blastStats(GTValues.VA[GTValues.ZPM], 900))
                .build();

        // sea snake
        EutecticCaesiumSodiumPotassium = eutectic(
                eu++, gtcefucId("eutectic_csnak_alloy"),
                EutecticAlloysString.get("eutectic_csnak_alloy"))
                        .colorAverage()
                        .flags(DECOMPOSITION_BY_CENTRIFUGING)
                        .components(Caesium, 4, Sodium, 1, Potassium, 5)
                        .build();

        EutecticCaesiumPotassiumGalliumNaquadahEnriched = eutectic(
                eu++, gtcefucId("eutectic_enriched_naquadah_gallium_csk_alloy"),
                EutecticAlloysString.get("eutectic_enriched_naquadah_gallium_csk_alloy"))
                        .colorAverage()
                        .flags(DECOMPOSITION_BY_ELECTROLYZING)
                        .components(Potassium, 5, Caesium, 20, Gallium, 4, NaquadahEnriched, 3)
                        .build();

        populateEutecticMap();

        generateEutecticProperties();
    }

    private static void populateEutecticMap() {
        // 194K to 1238K (boiling point is made up, but this is a real eutectic mixture)
        EutecticAlloys.put(EutecticCaesiumSodiumPotassium, EutecticAlloysString.get("eutectic_csnak_alloy"));
        // 269K to 7420K
        EutecticAlloys.put(EutecticCaesiumPotassiumGalliumNaquadahEnriched,
                EutecticAlloysString.get("eutectic_enriched_naquadah_gallium_csk_alloy"));
    }

    /**
     * Call this function after adding more eutectic alloys to the eutectic maps to autogenerate their properties.
     */
    public static void generateEutecticProperties() {
        for (Map.Entry<Material, int[]> eutectic : EutecticAlloys.entrySet()) {
            // generate heat capacity properties for eutectic materials
            if (!eutectic.getKey().hasProperty(GTCEFuCPropertyKey.HEAT_CAPACITY)) {
                eutectic.getKey().setProperty(GTCEFuCPropertyKey.HEAT_CAPACITY,
                        new GTCEFuCHeatCapacityProperty(eutectic.getValue()[3], false));
            }
        }
    }

    /**
     * Generate an essence. Performs functions shared across all essences.
     * 
     * @param id               The MetaItemSubID for this Material. Must be unique.
     * @param resourceLocation The ModId and Name of this Material. Will be formatted as "<modid>.material.<name>"
     *                         for the Translation Key.
     * @return The essence material builder.
     */
    public static Material.Builder essence(int id, @NotNull ResourceLocation resourceLocation) {
        // gaseous, acidic, and cryogenic all at the same time. Stainless steel go brrr.
        // as an aside, I am very annoyed that I can't put a fluid at 0°K
        return new Material.Builder(id, resourceLocation)
                .gas(new FluidBuilder().attributes(FluidAttributes.ACID).temperature(1)).iconSet(FLUID);
    }

    /**
     * Generate a eutectic material. Performs functions shared across all eutectic materials.
     * 
     * @param id               The MetaItemSubID for this Material. Must be unique.
     * @param resourceLocation The ModId and Name of this Material. Will be formatted as "<modid>.material.<name>"
     *                         for the Translation Key.
     * @param eutecticStats    The parameters of the eutectic material.
     * @return The eutectic material builder.
     */
    public static Material.Builder eutectic(int id, @NotNull ResourceLocation resourceLocation, int[] eutecticStats) {
        return new Material.Builder(id, resourceLocation).iconSet(METALLIC)
                .liquid(eutecticWithStats(eutecticStats[0], eutecticStats[1], eutecticStats[2]));
    }
}
