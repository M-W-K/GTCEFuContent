package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;
import net.minecraft.item.ItemStack;

import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockAdvancedCasing;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;

import crazypants.enderio.base.init.ModObject;
import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;

public final class GTCEFuCCasingLoader {

    public static void init() {
        ModHandler.addShapedRecipe(true, "casing_indestructible", GTCEFuCMetaBlocks.HARDENED_CASING
                .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.INDESTRUCTIBLE_CASING, 2),
                "PhP", "PFP", "PwP",
                'P', new UnificationEntry(OrePrefix.plateDouble, Materials.Neutronium),
                'F', new UnificationEntry(OrePrefix.frameGt, Materials.Neutronium));

        ModHandler.addShapedRecipe(true, "casing_pipe_indestructible", GTCEFuCMetaBlocks.HARDENED_CASING
                .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.INDESTRUCTIBLE_PIPE_CASING, 2),
                "PEP", "EFE", "PEP",
                'P', new UnificationEntry(OrePrefix.plateDense, Materials.Neutronium),
                'F', new UnificationEntry(OrePrefix.frameGt, Materials.Neutronium),
                'E', new UnificationEntry(OrePrefix.pipeNormalFluid, Materials.Neutronium));

        ModHandler.addShapedRecipe(true, "high_pressure_casing", GTCEFuCMetaBlocks.HARDENED_CASING
                .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.PRESSURE_CASING, 2),
                "PhP", "PFP", "PwP",
                'P', new UnificationEntry(OrePrefix.plate, Materials.RhodiumPlatedPalladium),
                'F', new UnificationEntry(OrePrefix.frameGt, Materials.HSSE));

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plateDouble, Materials.Neutronium, 6)
                .input(OrePrefix.frameGt, Materials.Neutronium)
                .circuitMeta(6)
                .outputs(GTCEFuCMetaBlocks.HARDENED_CASING
                        .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.INDESTRUCTIBLE_CASING, 2))
                .duration(50).EUt(16).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, Materials.RhodiumPlatedPalladium, 6)
                .input(OrePrefix.frameGt, Materials.HSSE)
                .circuitMeta(6)
                .outputs(GTCEFuCMetaBlocks.HARDENED_CASING
                        .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.PRESSURE_CASING, 2))
                .duration(50).EUt(16).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                        .getItemVariant(BlockLargeMultiblockCasing.CasingType.ATOMIC_CASING, 2),
                        GTCEFuCMetaItems.INFINITY_REAGENT.getStackForm(32),
                        GTCEFuCMetaItems.POWDER_ENDLIGHT.getStackForm())
                .input(MetaItems.FIELD_GENERATOR_UV, 6)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV)
                .fluidInputs(GTCEFuCMaterials.TriniumReduced.getFluid(1500))
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
    }
}
