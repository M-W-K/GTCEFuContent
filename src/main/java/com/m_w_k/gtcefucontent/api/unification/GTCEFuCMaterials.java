package com.m_w_k.gtcefucontent.api.unification;

import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.*;
import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.fluids.void_starlight.VoidStarlightBlockFluid;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCHeatCapacityProperty;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCPropertyKey;

import crafttweaker.annotations.ZenRegister;
import gregtech.api.GTValues;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.attribute.FluidAttributes;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.BlastProperty;
import gregtech.api.unification.material.properties.ToolProperty;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("mods.gtcefucontent.material.Material")
@ZenRegister
public final class GTCEFuCMaterials {

    public static Material EutecticCaesiumSodiumPotassium;
    public static Material EutecticCaesiumPotassiumGalliumNaquadahEnriched;

    public static Material VoidEssence;
    public static Material LightEssence;
    public static Material ExperienceEssence;
    public static Material ExistenceEssence;
    public static Material DisruptionEssence;
    public static Material StabilityEssence;
    public static Material ChaosEssence;
    public static Material OrderEssence;
    public static Material RealityEssence;

    public static Material PreheatedWater;
    public static Material CaesiumChlorineMix;
    public static Material ChargedEnder;
    public static Material TriniumReduced;
    public static Material FireEnhancer;
    public static Material VaporSeedRaw;
    public static Material VaporSeed;

    public static Material NaquadricAlloy;
    public static Material UnstableNaquadahAlloy;

    public static Material TitaniumBoride;
    public static Material SiliconCarbide;
    public static Material UncuredThermostableCeramic;
    public static Material ThermostableCeramic;

    public static Material ConcentratedEffort;
    public static Material FrozenStarlight;
    public static Material Infinitesimality;
    public static Material VoidStarlight;

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

    // IDs 22500 - 22519 reserved for essences
    // IDs 22520 - 22589 reserved for normal materials
    // IDs 22590 - 22599 reserved for eutectics

    public static void register() {
        if (INIT.getAndSet(true)) {
            return;
        }

        GTCEFuCMaterialFlagAddition.init();

        essences();

        PreheatedWater = new Material.Builder(22520, gtcefucId("preheated_water"))
                .liquid(new FluidBuilder().temperature(373))
                .gas(new FluidBuilder().temperature(973).customStill().name("hps")
                        .translation("gtcefucontent.fluid.hps"))
                .color(0x4A94EE)
                .flags(DISABLE_DECOMPOSITION)
                .components(Hydrogen, 2, Oxygen, 1)
                .build();

        CaesiumChlorineMix = new Material.Builder(22521, gtcefucId("caesium_chlorine_mix"))
                .dust()
                .colorAverage().iconSet(FINE)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .components(Caesium, 16, Chlorine, 1)
                .build();

        ChargedEnder = new Material.Builder(22522, gtcefucId("charged_ender"))
                .fluid()
                .color(0xa0cefa).iconSet(FLUID)
                .build();

        TriniumReduced = new Material.Builder(22523, gtcefucId("reduced_trinium"))
                .gem(7).fluid()
                .color(0x5E4C6E).iconSet(RUBY)
                .element(GTCEFuCElements.Ke1)
                .build();

        FireEnhancer = new Material.Builder(22524, gtcefucId("fire_enhancer"))
                .gas(fluidAtTemp(2000))
                .color(0xD69A00).iconSet(FLUID)
                .build();

        VaporSeedRaw = new Material.Builder(22525, gtcefucId("raw_vapor_seed"))
                .gas(fluidAtTemp(300))
                .color(0x20FFF0).iconSet(FLUID)
                .build();

        VaporSeed = new Material.Builder(22526, gtcefucId("vapor_seed"))
                .liquid(fluidAtTemp(100))
                .color(0x003B78).iconSet(FLUID)
                .build();

        NaquadricAlloy = new Material.Builder(22527, gtcefucId("naquadric_alloy"))
                .ingot().fluid()
                .colorAverage().iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_FOIL)
                .fluidPipeProperties(9530, 230, false, false, false, true)
                .components(Naquadria, 3, Thorium, 5, Cadmium, 1, Lead, 1)
                .blast(b -> b.temp(7340, BlastProperty.GasTier.HIGHER)
                        .blastStats(GTValues.VA[GTValues.ZPM], 900))
                .build();

        UnstableNaquadahAlloy = new Material.Builder(22528, gtcefucId("unstable_naquadah_alloy"))
                .ingot().fluid()
                .color(0x182832).iconSet(DULL)
                .flags(EXT2_METAL, GENERATE_SPRING, GENERATE_RING, GENERATE_ROTOR, GENERATE_SMALL_GEAR,
                        GENERATE_FRAME, GENERATE_DENSE, GENERATE_FOIL, GENERATE_GEAR, GENERATE_DOUBLE_PLATE)
                .components(NaquadahEnriched, 2, Osmiridium, 1, TriniumReduced, 1)
                .toolStats(ToolProperty.Builder.of(35.0F, 10.0F, 2764, 5)
                        .attackSpeed(0.25F).enchantability(100).magnetic().build())
                .rotorStats(7.5f, 4.5f, 4096)
                .cableProperties(V[UV], 2, 12)
                .fluidPipeProperties(8320, 130, true, true, false, true)
                .blast(b -> b
                        .temp(6900, BlastProperty.GasTier.HIGH)
                        .blastStats(VA[LuV], 900)
                        .vacuumStats(VA[IV], 250))
                .build();

        TitaniumBoride = new Material.Builder(22529, gtcefucId("titanium_boride"))
                .colorAverage().dust()
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .components(Titanium, 1, Boron, 2)
                .build();

        SiliconCarbide = new Material.Builder(22530, gtcefucId("silicon_carbide"))
                .colorAverage().ingot()
                .flags(STD_METAL)
                .components(Silicon, 1, Carbon, 1)
                .blast(b -> b.temp(3440, BlastProperty.GasTier.MID)
                        .blastStats(GTValues.VA[GTValues.EV], 700))
                .build();

        UncuredThermostableCeramic = new Material.Builder(22531, gtcefucId("uncured_thermostable_ceramic"))
                .color(0xFAF1C6).dust()
                .flags(DECOMPOSITION_BY_CENTRIFUGING, GENERATE_PLATE)
                .components(TitaniumBoride, 4, SiliconCarbide, 1)
                .build();

        ThermostableCeramic = new Material.Builder(22532, gtcefucId("thermostable_ceramic"))
                .color(0xFFF8E6).dust()
                .flags(DECOMPOSITION_BY_ELECTROLYZING, GENERATE_PLATE, EXCLUDE_PLATE_COMPRESSOR_RECIPE,
                        EXCLUDE_BLOCK_CRAFTING_RECIPES, NO_SMASHING, NO_SMELTING, NO_WORKING)
                .components(TitaniumBoride, 4, SiliconCarbide, 1)
                .build();

        ConcentratedEffort = new Material.Builder(22533, gtcefucId("concentrated_effort"))
                .color(0x10AA04).liquid()
                .build();

        FrozenStarlight = new Material.Builder(22534, gtcefucId("frozen_starlight"))
                .color(0x6fffb).liquid(new FluidBuilder().luminosity(15).temperature(100))
                .build();

        Infinitesimality = new Material.Builder(22535, gtcefucId("infinitesimality"))
                .liquid(new FluidBuilder().customStill().density(1000d).disableBucket()
                        .attribute(FluidAttributes.ACID).temperature(4999))
                .build();

        VoidStarlight = new Material.Builder(22536, gtcefucId("void_starlight"))
                .liquid(new VoidStarlightBlockFluid.VoidStarlightFluidBuilder()
                        .temperature(Integer.MAX_VALUE)
                        .luminosity(15)
                        .customStill())
                .build();

        eutectics();
    }

    private static void essences() {
        VoidEssence = essence(22500, gtcefucId("void_essence")).color(0x33523F).build();

        LightEssence = essence(22501, gtcefucId("light_essence")).color(0xFFF997).build();

        ExperienceEssence = essence(22502, gtcefucId("experience_essence")).color(0x04BB00).build();

        ExistenceEssence = essence(22503, gtcefucId("existence_essence")).color(0xAA00AA).build();

        DisruptionEssence = essence(22504, gtcefucId("disruption_essence")).color(0xAA0000).build();

        StabilityEssence = essence(22505, gtcefucId("stability_essence")).color(0x0000AA).build();

        ChaosEssence = essence(22506, gtcefucId("chaos_essence")).color(0x440000).build();

        OrderEssence = essence(22507, gtcefucId("order_essence")).color(0x000044).build();

        RealityEssence = essence(22508, gtcefucId("reality_essence")).color(0x440044).build();
    }

    private static void eutectics() {
        // sea snake
        EutecticCaesiumSodiumPotassium = eutectic(
                22590, gtcefucId("eutectic_csnak_alloy"),
                EutecticAlloysString.get("eutectic_csnak_alloy"))
                        .colorAverage()
                        .flags(DECOMPOSITION_BY_CENTRIFUGING)
                        .components(Caesium, 4, Sodium, 1, Potassium, 5)
                        .build();

        EutecticCaesiumPotassiumGalliumNaquadahEnriched = eutectic(
                22591, gtcefucId("eutectic_enriched_naquadah_gallium_csk_alloy"),
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
                .gas(new FluidBuilder().attributes(FluidAttributes.ACID).temperature(1).disableBucket()).iconSet(FLUID);
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
