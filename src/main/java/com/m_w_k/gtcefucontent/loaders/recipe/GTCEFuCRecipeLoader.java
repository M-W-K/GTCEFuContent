package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;

public class GTCEFuCRecipeLoader {
    private GTCEFuCRecipeLoader() {}

    public static void init() {
        GTCEFuContent.log("Starting recipe construction...");
        GTCEFuCInfinityExtractorRecipes.init();
        GTCEFuCSympatheticCombustorRecipes.init();
        GTCEFuMiscRecipes.init();
        controllerRecipes();
        GTCEFuContent.log("Recipe construction complete.");
    }

    private static void controllerRecipes() {
        ModHandler.addShapedRecipe(true, "infinity_extractor", GTCEFuCMetaTileEntities.INFINITY_EXTRACTOR.getStackForm(),
                "SRS", "CEC", "SRS",
                'S', MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.STEEL_SOLID),
                'R', MetaItems.ROBOT_ARM_EV.getStackForm(),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.EV),
                'E', MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.EV));

        ModHandler.addShapedRecipe(true, "sympathetic_combustor", GTCEFuCMetaTileEntities.SYMPATHETIC_COMBUSTOR.getStackForm(),
                "IGN", "CMP", "IGN",
                'G', new UnificationEntry(OrePrefix.gear, Materials.Titanium),
                'C', MetaItems.CONVEYOR_MODULE_EV.getStackForm(),
                'P', MetaItems.FLUID_REGULATOR_EV.getStackForm(),
                'M', MetaTileEntities.LARGE_COMBUSTION_ENGINE.getStackForm(),
                'N', new UnificationEntry(OrePrefix.pipeNonupleFluid, Materials.Titanium),
                'I', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.EV));
    }
}
