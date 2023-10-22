package com.m_w_k.gtcefucontent.loaders.recipe;

import com.latmod.mods.projectex.item.ProjectEXItems;
import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockAdvancedCasing;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;
import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockCleanroomCasing;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;

public final class GTCEFuCRecipeLoader {
    private GTCEFuCRecipeLoader() {}

    public static void init() {
        GTCEFuContent.log("Starting recipe construction...");

        GTCEFuCInfinityExtractorRecipes.init();
        GTCEFuCPneumaticInfuserRecipes.init();
        GTCEFuCSympatheticCombustorRecipes.init();
        GTCEFuCAntimatterCompressorRecipes.init();

        GTCEFuCCasingLoader.init();
        GTCEFuMiscRecipes.init();

        controllerRecipes();

        GTCEFuContent.log("Recipe construction complete.");
    }

    public static void initLate() {
        // Adjust/create recipeMaps that depend on iterating through other recipeMaps.

        // We can't change the behavior of the cutter recipeMap setup itself without being overly invasive.
        GTCEFuMiscRecipes.cutterUpdate();

        // The forging furnace recipeMap is completely new, but dependent on the blast furnace recipeMap.
        GTCEFuCForgingFurnaceRecipes.init();

        // The fusion stack recipeMaps are dependent on the normal fusion recipemap
        GTCEFuCFusionStackRecipes.init();
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

        ModHandler.addShapedRecipe(true, "forging_furnace", GTCEFuCMetaTileEntities.FORGING_FURNACE.getStackForm(),
                "RGR", "FAC", "RIR",
                'F', MetaTileEntities.FORGE_HAMMER[GTValues.IV].getStackForm(),
                'A', MetaTileEntities.ELECTRIC_BLAST_FURNACE.getStackForm(),
                'C', MetaTileEntities.VACUUM_FREEZER.getStackForm(),
                'G', new UnificationEntry(OrePrefix.dust, Materials.Graphene),
                'R', MetaItems.ROBOT_ARM_IV.getStackForm(),
                'I', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.LuV));

        // Assembler recipes

        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.FUSION_REACTOR[2].getStackForm(2))
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV, 4)
                .input(OrePrefix.ingot, Materials.SteelMagnetic, 24)
                .input(OrePrefix.plate, Materials.Neutronium)
                .input(OrePrefix.dust, Materials.Lutetium, 64)
                .input(OrePrefix.dust, Materials.Lutetium, 16)
                .input(MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT, 64)
                .input(OrePrefix.wireGtSingle, Materials.RutheniumTriniumAmericiumNeutronate, 32)
                .fluidInputs(Materials.SolderingAlloy.getFluid(GTValues.L * 12))
                .fluidInputs(Materials.IndiumGalliumPhosphide.getFluid(GTValues.L * 8))
                .output(GTCEFuCMetaTileEntities.FUSION_STACK[0])
                .research(b -> b
                        .researchStack(MetaTileEntities.FUSION_REACTOR[2].getStackForm())
                        .CWUt(192)
                        .EUt(GTValues.VA[GTValues.UV]))
                .duration(1000).EUt(GTValues.VA[GTValues.UV]).buildAndRegister();

        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(GTCEFuCMetaTileEntities.FUSION_STACK[0].getStackForm(2))
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV, 8)
                .input(OrePrefix.ingot, Materials.NeodymiumMagnetic, 32)
                .input(OrePrefix.plateDouble, Materials.Neutronium)
                .input(OrePrefix.dust, Materials.Europium, 64)
                .input(OrePrefix.dust, Materials.Europium, 32)
                .input(MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT, 64)
                .input(OrePrefix.wireGtDouble, Materials.RutheniumTriniumAmericiumNeutronate, 32)
                .fluidInputs(Materials.SolderingAlloy.getFluid(GTValues.L * 12))
                .fluidInputs(Materials.Osmiridium.getFluid(GTValues.L * 8))
                .output(GTCEFuCMetaTileEntities.FUSION_STACK[1])
                .research(b -> b
                        .researchStack(GTCEFuCMetaTileEntities.FUSION_STACK[0].getStackForm())
                        .CWUt(288)
                        .EUt(GTValues.VA[GTValues.UHV]))
                .duration(1200).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();

        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.FUSION_REACTOR[2].getStackForm(2))
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV, 12)
                .input(OrePrefix.ingot, Materials.SamariumMagnetic, 48)
                .input(OrePrefix.plateDense, Materials.Neutronium)
                .input(OrePrefix.dust, Materials.Darmstadtium, 64)
                .input(OrePrefix.dust, Materials.Darmstadtium, 64)
                .input(MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT, 64)
                .input(OrePrefix.wireGtQuadruple, Materials.RutheniumTriniumAmericiumNeutronate, 32)
                .fluidInputs(Materials.SolderingAlloy.getFluid(GTValues.L * 12))
                .fluidInputs(Materials.NaquadahAlloy.getFluid(GTValues.L * 8))
                .output(GTCEFuCMetaTileEntities.FUSION_STACK[2])
                .research(b -> b
                        .researchStack(GTCEFuCMetaTileEntities.FUSION_STACK[1].getStackForm())
                        .CWUt(384)
                        .EUt(GTValues.VA[GTValues.UEV]))
                .duration(1200).EUt(GTValues.VA[GTValues.UEV]).buildAndRegister();


        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(MetaTileEntities.STEAM_COMPRESSOR_STEEL)
                .input(ProjectEXItems.FINAL_STAR)
                .inputs(MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.STEEL_SOLID))
                .input(MetaItems.ELECTRIC_PUMP_UV, 4)
                .input(MetaItems.SENSOR_UV, 6)
                .input(MetaItems.NEUTRON_REFLECTOR, 12)
                .input(MetaItems.ROBOT_ARM_UV, 2)
                .input(MetaItems.CONVEYOR_MODULE_UV, 2)
                .input(OrePrefix.wireGtHex, Materials.RutheniumTriniumAmericiumNeutronate, 32)
                .input(MetaTileEntities.POWER_SUBSTATION)
                .fluidInputs(Materials.SolderingAlloy.getFluid(GTValues.L * 16))
                .fluidInputs(Materials.RutheniumTriniumAmericiumNeutronate.getFluid(GTValues.L * 9 * 64))
                .output(GTCEFuCMetaTileEntities.ANTIMATTER_COMPRESSOR)
                .research(b -> b
                        .researchStack(GTCEFuCMetaBlocks.ADVANCED_CASING.getItemVariant(GTCEFuCBlockAdvancedCasing.AdvancedCasingType.NULL_FIELD_CASING))
                        .CWUt(512)
                        .EUt(GTValues.VA[GTValues.UEV]))
                .duration(1400).EUt(GTValues.VA[GTValues.UIV]).buildAndRegister();
    }
}
