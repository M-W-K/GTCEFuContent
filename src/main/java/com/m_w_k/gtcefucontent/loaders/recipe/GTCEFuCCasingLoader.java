package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockAdvancedCasing;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockStandardCasing;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;

import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.ConfigHolder;
import gregtech.common.items.MetaItems;

public final class GTCEFuCCasingLoader {

    public static void init() {
        ModHandler.addShapedRecipe(true, "casing_indestructible", GTCEFuCMetaBlocks.HARDENED_CASING
                .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.INDESTRUCTIBLE_CASING,
                        ConfigHolder.recipes.casingsPerCraft),
                "PhP", "PFP", "PwP",
                'P', new UnificationEntry(OrePrefix.plate, Materials.Neutronium),
                'F', new UnificationEntry(OrePrefix.frameGt, Materials.Neutronium));

        ModHandler.addShapedRecipe(true, "casing_pipe_indestructible", GTCEFuCMetaBlocks.HARDENED_CASING
                .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.INDESTRUCTIBLE_PIPE_CASING,
                        ConfigHolder.recipes.casingsPerCraft),
                "PEP", "EFE", "PEP",
                'P', new UnificationEntry(OrePrefix.plateDouble, Materials.Neutronium),
                'F', new UnificationEntry(OrePrefix.frameGt, Materials.Neutronium),
                'E', new UnificationEntry(OrePrefix.pipeNormalFluid, Materials.Neutronium));

        ModHandler.addShapedRecipe(true, "high_pressure_casing", GTCEFuCMetaBlocks.STANDARD_CASING
                .getItemVariant(GTCEFuCBlockStandardCasing.CasingType.PRESSURE_CASING,
                        ConfigHolder.recipes.casingsPerCraft),
                "PhP", "PFP", "PwP",
                'P', new UnificationEntry(OrePrefix.plate, GTCEFuCMaterials.TitaniumPressureAlloy),
                'F', new UnificationEntry(OrePrefix.frameGt, Materials.HSSE));

        ModHandler.addShapedRecipe(true, "unstable_hyperstatic_casing", GTCEFuCMetaBlocks.STANDARD_CASING
                .getItemVariant(GTCEFuCBlockStandardCasing.CasingType.UNSTABLE_HYPERSTATIC_CASING,
                        ConfigHolder.recipes.casingsPerCraft),
                "PhP", "PFP", "PwP",
                'P', new UnificationEntry(OrePrefix.plate, GTCEFuCMaterials.UnstableNaquadahAlloy),
                'F', new UnificationEntry(OrePrefix.frameGt, Materials.NaquadahAlloy));
        ModHandler.addShapedRecipe(true, "casing_forging",
                GTCEFuCMetaBlocks.STANDARD_CASING
                        .getItemVariant(GTCEFuCBlockStandardCasing.CasingType.FORGING_CASING,
                                ConfigHolder.recipes.casingsPerCraft),
                "PhP", "PFP", "PwP", 'P', new UnificationEntry(OrePrefix.plate, GTCEFuCMaterials.CobaltAlloy), 'F',
                new UnificationEntry(OrePrefix.frameGt, Materials.Aluminium));

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, Materials.Neutronium, 6)
                .input(OrePrefix.frameGt, Materials.Neutronium)
                .circuitMeta(6)
                .outputs(GTCEFuCMetaBlocks.HARDENED_CASING
                        .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.INDESTRUCTIBLE_CASING,
                                ConfigHolder.recipes.casingsPerCraft))
                .duration(50).EUt(16).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCMetaBlocks.STANDARD_CASING
                        .getItemVariant(GTCEFuCBlockStandardCasing.CasingType.UNSTABLE_HYPERSTATIC_CASING),
                        GTCEFuCMetaItems.INFINITY_REAGENT.getStackForm(32),
                        GTCEFuCMetaItems.POWDER_ENDLIGHT.getStackForm())
                .input(MetaItems.FIELD_GENERATOR_UV, 6)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV)
                .fluidInputs(GTCEFuCMaterials.RealityEssence.getFluid(500))
                .outputs(GTCEFuCMetaBlocks.ADVANCED_CASING
                        .getItemVariant(GTCEFuCBlockAdvancedCasing.AdvancedCasingType.NULL_FIELD_CASING))
                .circuitMeta(6)
                .duration(100).EUt(GTValues.VA[GTValues.UV]).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.pipeLargeFluid, Materials.Duranium, 6)
                .input(OrePrefix.plate, GCYMMaterials.Trinaquadalloy, 4)
                .input(MetaItems.NEUTRON_REFLECTOR, 4)
                .input(MetaItems.FLUID_REGULATOR_UV)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72))
                .outputs(GTCEFuCMetaBlocks.HARDENED_CASING
                        .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.PLASMA_PIPE_CASING, 16))
                .circuitMeta(2)
                .duration(600).EUt(GTValues.VA[GTValues.IV]).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, GTCEFuCMaterials.TitaniumPressureAlloy, 6)
                .input(OrePrefix.frameGt, Materials.HSSE)
                .circuitMeta(6)
                .outputs(GTCEFuCMetaBlocks.STANDARD_CASING
                        .getItemVariant(GTCEFuCBlockStandardCasing.CasingType.PRESSURE_CASING,
                                ConfigHolder.recipes.casingsPerCraft))
                .duration(50).EUt(16).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, GTCEFuCMaterials.UnstableNaquadahAlloy, 6)
                .input(OrePrefix.frameGt, Materials.NaquadahAlloy)
                .circuitMeta(6)
                .outputs(GTCEFuCMetaBlocks.STANDARD_CASING
                        .getItemVariant(GTCEFuCBlockStandardCasing.CasingType.UNSTABLE_HYPERSTATIC_CASING,
                                ConfigHolder.recipes.casingsPerCraft))
                .duration(50).EUt(16).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, GTCEFuCMaterials.CobaltAlloy, 6)
                .input(OrePrefix.frameGt, Materials.Aluminium)
                .notConsumable(new IntCircuitIngredient(6))
                .outputs(GTCEFuCMetaBlocks.STANDARD_CASING
                        .getItemVariant(GTCEFuCBlockStandardCasing.CasingType.FORGING_CASING,
                                ConfigHolder.recipes.casingsPerCraft))
                .duration(50).EUt(16).buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .inputs(GTCEFuCMetaBlocks.STANDARD_CASING
                        .getItemVariant(GTCEFuCBlockStandardCasing.CasingType.UNSTABLE_HYPERSTATIC_CASING))
                .fluidInputs(GTCEFuCMaterials.StabilityEssence.getFluid(65))
                .outputs(GTCEFuCMetaBlocks.HARDENED_CASING
                        .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.HYPERSTATIC_CASING))
                .duration(80).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, Materials.Invar)
                .input(OrePrefix.plate, GTCEFuCMaterials.ThermostableCeramic, 6)
                .fluidInputs(Materials.Concrete.getFluid(GTValues.L))
                .outputs(GTCEFuCMetaBlocks.STANDARD_CASING
                        .getItemVariant(GTCEFuCBlockStandardCasing.CasingType.THERMOSTABLE_CERAMIC,
                                ConfigHolder.recipes.casingsPerCraft))
                .duration(200).EUt(192).buildAndRegister();
    }
}
