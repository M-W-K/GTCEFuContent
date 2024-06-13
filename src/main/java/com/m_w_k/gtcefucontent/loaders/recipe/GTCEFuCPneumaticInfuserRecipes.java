package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;

import gregtech.api.GTValues;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;

public final class GTCEFuCPneumaticInfuserRecipes {

    private GTCEFuCPneumaticInfuserRecipes() {}

    public static void init() {
        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, Materials.Steel)
                .fluidInputs(Materials.Lava.getFluid(1000),
                        Materials.Oxygen.getFluid(100))
                .output(OrePrefix.ingot, Materials.DamascusSteel)
                .duration(500).EUt(GTValues.VA[GTValues.EV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(GTCEFuCMaterials.LightEssence.getFluid(10000),
                        Materials.Blaze.getFluid(1440))
                .fluidOutputs(GTCEFuCMaterials.FireEnhancer.getFluid(10000))
                .duration(250).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(GTCEFuCMaterials.VaporSeed.getFluid(80),
                        Materials.Hydrogen.getFluid(30000))
                .fluidOutputs(GTCEFuCMaterials.TriniumReduced.getFluid(5000))
                .duration(400).EUt(GTValues.VA[GTValues.IV]).buildAndRegister();

        essences();
    }

    private static void essences() {
        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(GTCEFuCMaterials.VaporSeed.getFluid(100),
                        Materials.Water.getFluid(128000))
                .fluidOutputs(GTCEFuCMaterials.ExistenceEssence.getFluid(1000))
                .duration(400).EUt(GTValues.VA[GTValues.EV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(GTCEFuCMaterials.VaporSeed.getFluid(100),
                        Materials.Radon.getFluid(1500))
                .fluidOutputs(GTCEFuCMaterials.VoidEssence.getFluid(5000))
                .duration(400).EUt(GTValues.VA[GTValues.IV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(GTCEFuCMaterials.VaporSeed.getFluid(100),
                        GTCEFuCMaterials.FrozenStarlight.getFluid(1000))
                .fluidOutputs(GTCEFuCMaterials.LightEssence.getFluid(10000))
                .duration(400).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        // ZPM

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(GTCEFuCMaterials.VaporSeed.getFluid(100),
                        GTCEFuCMaterials.ChaosEssence.getFluid(1500),
                        GTCEFuCMaterials.OrderEssence.getFluid(1500))
                .fluidOutputs(GTCEFuCMaterials.RealityEssence.getFluid(3000))
                .duration(400).EUt(GTValues.VA[GTValues.UV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(GTCEFuCMaterials.VaporSeed.getFluid(100),
                        GTCEFuCMaterials.ConcentratedEffort.getFluid(75),
                        Materials.Argon.getPlasma(64000))
                .fluidOutputs(GTCEFuCMaterials.ExperienceEssence.getFluid(100))
                .duration(400).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();
    }
}
