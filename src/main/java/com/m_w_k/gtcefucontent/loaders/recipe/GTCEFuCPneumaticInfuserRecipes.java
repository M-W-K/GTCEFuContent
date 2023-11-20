package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;

import gregtech.api.GTValues;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;

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
                .input(MetaItems.COVER_SOLAR_PANEL, 64)
                .chancedOutput(MetaItems.COVER_SOLAR_PANEL, 64, 9000, 0)
                .fluidInputs(Materials.Glowstone.getFluid(4800),
                        Materials.Ice.getFluid(13500))
                .fluidOutputs(GTCEFuCMaterials.LightEssence.getFluid(14000))
                .duration(200).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(GTCEFuCMaterials.LightEssence.getFluid(10000),
                        Materials.Blaze.getFluid(1440))
                .fluidOutputs(GTCEFuCMaterials.FireEnhancer.getFluid(10000))
                .duration(250).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(GTCEFuCMaterials.VaporSeed.getFluid(100),
                        Materials.Hydrogen.getFluid(30000))
                .fluidOutputs(GTCEFuCMaterials.TriniumReduced.getFluid(5000))
                .duration(400).EUt(GTValues.VA[GTValues.IV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(GTCEFuCMaterials.VaporSeed.getFluid(100),
                        Materials.Radon.getFluid(1500))
                .fluidOutputs(GTCEFuCMaterials.VoidEssence.getFluid(5000))
                .duration(400).EUt(GTValues.VA[GTValues.IV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.POWDER_ENDLIGHT)
                .fluidInputs(GTCEFuCMaterials.VaporSeed.getFluid(300),
                        Materials.Plutonium239.getFluid(72),
                        Materials.Argon.getPlasma(64000))
                .fluidOutputs(Materials.McGuffium239.getFluid(48))
                .duration(1380).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();
    }
}
