package com.m_w_k.gtcefucontent.loaders.recipe;

import static com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials.*;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;

import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;

public class GTCEFuCEutecticLoader {

    private GTCEFuCEutecticLoader() {}

    public static void init() {
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, Materials.Caesium, 4)
                .input(OrePrefix.dust, Materials.Potassium, 4)
                .fluidInputs(Materials.SodiumPotassium.getFluid(1000))
                .fluidInputs(Materials.LiquidOxygen.getFluid(500))
                .fluidOutputs(EutecticCaesiumSodiumPotassium.getFluid(1000),
                        Materials.Oxygen.getFluid(500))
                .duration(1500).EUt(GTValues.VA[GTValues.MV]).buildAndRegister();

        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, Materials.Caesium, 16)
                .fluidInputs(Materials.Chlorine.getFluid(1000))
                .output(OrePrefix.dust, CaesiumChlorineMix, 17)
                .duration(4000).EUt(GTValues.VA[GTValues.MV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, CaesiumChlorineMix, 17)
                .fluidInputs(EutecticCaesiumSodiumPotassium.getFluid(1000),
                        Materials.NaquadahEnriched.getFluid(432),
                        Materials.Gallium.getFluid(576))
                .output(OrePrefix.dust, Materials.Salt)
                .fluidOutputs(EutecticCaesiumPotassiumGalliumNaquadahEnriched.getFluid(1000))
                .duration(160).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();
    }
}
