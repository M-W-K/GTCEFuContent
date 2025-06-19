package com.m_w_k.gtcefucontent.api.unification;

import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.*;
import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.fluids.void_starlight.VoidStarlightBlockFluid;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCEutecticMaterialProperty;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCPropertyKey;

import crafttweaker.annotations.ZenRegister;
import gregtech.api.GTValues;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.attribute.FluidAttributes;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconSet;
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

    public static Material TitaniumPressureAlloy;
    public static Material CobaltAlloy;

    public static void register() {
        GTCEFuCMaterialFlagAddition.init();

        essences();

        // element materials: 1-200
        TriniumReduced = new Material.Builder(1, gtcefucId("reduced_trinium"))
                .gem(7).fluid()
                .color(0x5E4C6E).iconSet(RUBY)
                .element(GTCEFuCElements.Ke1)
                .build();

        // misc materials: 201-1000
        PreheatedWater = new Material.Builder(201, gtcefucId("preheated_water"))
                .liquid(new FluidBuilder().temperature(373))
                .gas(new FluidBuilder().temperature(973).customStill().name("hps")
                        .translation("gtcefucontent.fluid.hps"))
                .color(0x4A94EE)
                .flags(DISABLE_DECOMPOSITION)
                .components(Hydrogen, 2, Oxygen, 1)
                .build();

        CaesiumChlorineMix = new Material.Builder(202, gtcefucId("caesium_chlorine_mix"))
                .dust()
                .colorAverage().iconSet(FINE)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .components(Caesium, 16, Chlorine, 1)
                .build();

        ChargedEnder = new Material.Builder(203, gtcefucId("charged_ender"))
                .fluid()
                .color(0xa0cefa).iconSet(FLUID)
                .build();

        FireEnhancer = new Material.Builder(204, gtcefucId("fire_enhancer"))
                .gas(fluidAtTemp(2000))
                .color(0xD69A00).iconSet(FLUID)
                .build();

        VaporSeedRaw = new Material.Builder(205, gtcefucId("raw_vapor_seed"))
                .gas(fluidAtTemp(300))
                .color(0x20FFF0).iconSet(FLUID)
                .build();

        VaporSeed = new Material.Builder(206, gtcefucId("vapor_seed"))
                .liquid(fluidAtTemp(100))
                .color(0x003B78).iconSet(FLUID)
                .build();

        NaquadricAlloy = new Material.Builder(207, gtcefucId("naquadric_alloy"))
                .ingot().fluid()
                .colorAverage().iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_FOIL)
                .fluidPipeProperties(9530, 230, false, false, false, true)
                .components(Naquadria, 3, Thorium, 5, Cadmium, 1, Lead, 1)
                .blast(b -> b.temp(7340, BlastProperty.GasTier.HIGHER)
                        .blastStats(GTValues.VA[GTValues.ZPM], 900))
                .build();

        UnstableNaquadahAlloy = new Material.Builder(208, gtcefucId("unstable_naquadah_alloy"))
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

        TitaniumBoride = new Material.Builder(209, gtcefucId("titanium_boride"))
                .colorAverage().dust()
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .components(Titanium, 1, Boron, 2)
                .build();

        SiliconCarbide = new Material.Builder(210, gtcefucId("silicon_carbide"))
                .colorAverage().ingot()
                .flags(STD_METAL)
                .components(Silicon, 1, Carbon, 1)
                .blast(b -> b.temp(3440, BlastProperty.GasTier.MID)
                        .blastStats(GTValues.VA[GTValues.EV], 700))
                .build();

        UncuredThermostableCeramic = new Material.Builder(211, gtcefucId("uncured_thermostable_ceramic"))
                .color(0xFAF1C6).dust()
                .flags(DECOMPOSITION_BY_CENTRIFUGING, GENERATE_PLATE)
                .components(TitaniumBoride, 4, SiliconCarbide, 1)
                .build();

        ThermostableCeramic = new Material.Builder(212, gtcefucId("thermostable_ceramic"))
                .color(0xFFF8E6).dust()
                .flags(DECOMPOSITION_BY_ELECTROLYZING, GENERATE_PLATE, EXCLUDE_PLATE_COMPRESSOR_RECIPE,
                        EXCLUDE_BLOCK_CRAFTING_RECIPES, NO_SMASHING, NO_SMELTING, NO_WORKING)
                .components(TitaniumBoride, 4, SiliconCarbide, 1)
                .build();

        ConcentratedEffort = new Material.Builder(213, gtcefucId("concentrated_effort"))
                .color(0x10AA04).liquid()
                .build();

        FrozenStarlight = new Material.Builder(214, gtcefucId("frozen_starlight"))
                .color(0x6fffb).liquid(new FluidBuilder().luminosity(15).temperature(100))
                .build();

        Infinitesimality = new Material.Builder(215, gtcefucId("infinitesimality"))
                .liquid(new FluidBuilder().customStill().density(1000d).disableBucket()
                        .attribute(FluidAttributes.ACID).temperature(4999))
                .build();

        VoidStarlight = new Material.Builder(216, gtcefucId("void_starlight"))
                .liquid(new VoidStarlightBlockFluid.VoidStarlightFluidBuilder()
                        .temperature(Short.MAX_VALUE)
                        .attribute(FluidAttributes.ACID)
                        .luminosity(15)
                        .customStill())
                .build();

        TitaniumPressureAlloy = new Material.Builder(217, gtcefucId("titanium_pressure_alloy"))
                .ingot().liquid()
                .color(0xCBB4D9).iconSet(METALLIC)
                .flags(STD_METAL)
                .components(Titanium, 1, Aluminium, 6, Vanadium, 4)
                .blast(b -> b.temp(3820, BlastProperty.GasTier.HIGH)
                        .blastStats(GTValues.VA[GTValues.EV], 700))
                .build();

        CobaltAlloy = new Material.Builder(3010, gtcefucId("cobalt_alloy"))
                .ingot().fluid()
                .color(0x6594B2).iconSet(MaterialIconSet.METALLIC)
                .flags(GENERATE_PLATE, GENERATE_GEAR)
                .components(Cobalt, 3, Iridium, 3, Aluminium, 1, Tungsten, 1)
                .blast(b -> b.temp(5388, BlastProperty.GasTier.HIGH)
                        .blastStats(GTValues.VA[GTValues.IV], 1000))
                .build();

        eutectics();
    }

    private static void essences() {
        // essences: 1001-1500
        VoidEssence = essence(1001, gtcefucId("void_essence")).color(0x33523F).build();

        LightEssence = essence(1002, gtcefucId("light_essence")).color(0xFFF997).build();

        ExperienceEssence = essence(1003, gtcefucId("experience_essence")).color(0x04BB00).build();

        ExistenceEssence = essence(1004, gtcefucId("existence_essence")).color(0xAA00AA).build();

        DisruptionEssence = essence(1005, gtcefucId("disruption_essence")).color(0xAA0000).build();

        StabilityEssence = essence(1006, gtcefucId("stability_essence")).color(0x0000AA).build();

        ChaosEssence = essence(1007, gtcefucId("chaos_essence")).color(0x440000).build();

        OrderEssence = essence(1008, gtcefucId("order_essence")).color(0x000044).build();

        RealityEssence = essence(1009, gtcefucId("reality_essence")).color(0x440044).build();
    }

    private static void eutectics() {
        // eutectics: 1501-2000
        EutecticCaesiumSodiumPotassium = eutectic(new Material.Builder(
                1501, gtcefucId("eutectic_csnak_alloy")) // sea snake
                        .colorAverage().iconSet(METALLIC)
                        .flags(DECOMPOSITION_BY_CENTRIFUGING)
                        .components(Caesium, 4, Sodium, 1, Potassium, 5),
                195, 1237, 800000);

        EutecticCaesiumPotassiumGalliumNaquadahEnriched = eutectic(new Material.Builder(
                1502, gtcefucId("eutectic_enriched_naquadah_gallium_csk_alloy"))
                        .colorAverage().iconSet(METALLIC)
                        .flags(DECOMPOSITION_BY_ELECTROLYZING)
                        .components(Potassium, 5, Caesium, 20, Gallium, 4, NaquadahEnriched, 3),
                270, 7419, 10000000);
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
     * Finalizes a eutectic material. Generates the eutectic property, which then generates the eutectic fluid.
     */
    public static Material eutectic(@NotNull Material.Builder builder, int minTemp, int maxTemp, int heatCapacity) {
        Material mat = builder.iconSet(METALLIC).build();
        mat.setProperty(GTCEFuCPropertyKey.EUTECTIC,
                new GTCEFuCEutecticMaterialProperty(minTemp, maxTemp, heatCapacity));
        return mat;
    }

    /**
     * Finalizes a eutectic material. Generates the eutectic property, which then generates the eutectic fluid.
     */
    public static Material eutectic(@NotNull Material.Builder builder, int minTemp, int defaultTemp, int maxTemp,
                                    int heatCapacity) {
        Material mat = builder.build();
        mat.setProperty(GTCEFuCPropertyKey.EUTECTIC,
                new GTCEFuCEutecticMaterialProperty(minTemp, defaultTemp, maxTemp, heatCapacity));
        return mat;
    }
}
