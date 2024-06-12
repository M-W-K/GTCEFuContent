package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;
import net.minecraft.item.ItemStack;

import com.latmod.mods.projectex.item.ProjectEXItems;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockAdvancedCasing;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;

import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregicality.multiblocks.common.metatileentities.GCYMMetaTileEntities;
import gregtech.api.GTValues;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;

public class GTCEFuCControllerLoader {

    private GTCEFuCControllerLoader() {}

    public static void init() {
        ModHandler.addShapedRecipe(true, "infinity_extractor",
                GTCEFuCMetaTileEntities.INFINITY_EXTRACTOR.getStackForm(),
                "SRS", "CEC", "SRS",
                'S', MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.STEEL_SOLID),
                'R', MetaItems.ROBOT_ARM_EV.getStackForm(),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.EV),
                'E', MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.EV));

        ModHandler.addShapedRecipe(true, "sympathetic_combustor",
                GTCEFuCMetaTileEntities.SYMPATHETIC_COMBUSTOR.getStackForm(),
                "IGN", "CMP", "IGN",
                'G', new UnificationEntry(OrePrefix.gear, Materials.Titanium),
                'C', MetaItems.CONVEYOR_MODULE_EV.getStackForm(),
                'P', MetaItems.FLUID_REGULATOR_EV.getStackForm(),
                'M', MetaTileEntities.LARGE_COMBUSTION_ENGINE.getStackForm(),
                'N', new UnificationEntry(OrePrefix.pipeNonupleFluid, Materials.Titanium),
                'I', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.EV));

        ModHandler.addShapedRecipe(true, "pneumatic_infuser", GTCEFuCMetaTileEntities.PNEUMATIC_INFUSER.getStackForm(),
                "PRP", "IOI", "PCP",
                'R', MetaItems.ROBOT_ARM_IV,
                'P', MetaItems.ELECTRIC_PUMP_IV,
                'I', MetaItems.ELECTRIC_PISTON_IV,
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.IV),
                'O', MetaTileEntities.CANNER[GTValues.IV].getStackForm());

        ModHandler.addShapedRecipe(true, "electrode_blast_smelter",
                GTCEFuCMetaTileEntities.ELECTRODE_SMELTER.getStackForm(),
                "CTC", "PHP", "DAD",
                'A', GCYMMetaTileEntities.ALLOY_BLAST_SMELTER.getStackForm(),
                'H', GCYMMetaTileEntities.MEGA_BLAST_FURNACE.getStackForm(),
                'P', MetaItems.ELECTRIC_PUMP_UV,
                'D', new UnificationEntry(OrePrefix.plateDouble, Materials.Duranium),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.UHV),
                'T', new UnificationEntry(OrePrefix.plateDense, Materials.Thorium));

        MetaItem<?>.MetaValueItem[] pumps = { MetaItems.ELECTRIC_PUMP_LuV, MetaItems.ELECTRIC_PUMP_ZPM,
                MetaItems.ELECTRIC_PUMP_UV };
        Material[] pipeMaterials = { Materials.NiobiumTitanium, Materials.Europium, Materials.Duranium };
        Material[] tiers = { MarkerMaterials.Tier.LuV, MarkerMaterials.Tier.ZPM, MarkerMaterials.Tier.UV };
        for (int i = 0; i < 3; i++) {
            ItemStack controller = GTCEFuCMetaTileEntities.HEAT_EXCHANGER[i].getStackForm();
            ModHandler.addShapedRecipe(true, controller.getTranslationKey(), controller,
                    "MCM", "PHP", "MCM",
                    'H', MetaTileEntities.FLUID_HEATER[GTValues.LuV + i].getStackForm(),
                    'M', new UnificationEntry(OrePrefix.pipeQuadrupleFluid, pipeMaterials[i]),
                    'P', pumps[i],
                    'C', new UnificationEntry(OrePrefix.circuit, tiers[i]));
        }

        ModHandler.addShapedRecipe(true, "basic_heat_reclaimer",
                GTCEFuCMetaTileEntities.HEAT_RECLAIMER[0].getStackForm(),
                "AFA", "ACA", "AFA",
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.HV),
                'A', new UnificationEntry(OrePrefix.plate, Materials.Asbestos),
                'F', MetaItems.FIELD_GENERATOR_MV);

        ModHandler.addShapedRecipe(true, "advanced_heat_reclaimer",
                GTCEFuCMetaTileEntities.HEAT_RECLAIMER[1].getStackForm(),
                "AFA", "ACA", "AFA",
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.IV),
                'A', new UnificationEntry(OrePrefix.plateDense, Materials.Asbestos),
                'F', MetaItems.FIELD_GENERATOR_EV);

        ModHandler.addShapedRecipe(true, "heat_disperser",
                GTCEFuCMetaTileEntities.HEAT_DISPERSER.getStackForm(),
                "PPP", "DCD", "PPP",
                'P', MetaItems.FLUID_REGULATOR_IV,
                'D', new UnificationEntry(OrePrefix.gear, Materials.DamascusSteel),
                'C', MetaTileEntities.HULL[GTValues.IV].getStackForm());

        ModHandler.addShapedRecipe(true, "naq_fuel_cell_packer",
                GTCEFuCMetaTileEntities.NAQ_FUEL_CELL_PACKER.getStackForm(),
                "SSS", "APA", "FCF",
                'S', MetaItems.SENSOR_ZPM,
                'A', MetaItems.ROBOT_ARM_ZPM,
                'P', MetaTileEntities.PACKER[GTValues.ZPM].getStackForm(),
                'F', MetaItems.FIELD_GENERATOR_ZPM,
                'C', GTCEFuCMetaBlocks.HARDENED_CASING
                        .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.HYPERSTATIC_CASING));

        // Assembler recipes

        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(GCYMMetaTileEntities.STEAM_ENGINE)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.ZPM, 2)
                .input(OrePrefix.cableGtOctal, Materials.BlackSteel, 64)
                .input(OrePrefix.cableGtHex, Materials.TungstenSteel, 40)
                .input(OrePrefix.gearSmall, Materials.HSSG, 64)
                .input(OrePrefix.gearSmall, Materials.HSSG, 64)
                .input(OrePrefix.gear, Materials.BlackBronze, 64)
                .input(OrePrefix.stickLong, Materials.RhodiumPlatedPalladium, 32)
                .input(OrePrefix.screw, Materials.Apatite, 64)
                .input(OrePrefix.screw, Materials.Apatite, 24)
                .fluidInputs(Materials.SolderingAlloy.getFluid(GTValues.L * 4),
                        Materials.StyreneButadieneRubber.getFluid(12000),
                        Materials.Lubricant.getFluid(12000))
                .output(GTCEFuCMetaTileEntities.MEGA_STEAM_ENGINE)
                .stationResearch(b -> b
                        .researchStack(MetaTileEntities.LARGE_STEAM_TURBINE.getStackForm())
                        .CWUt(16)
                        .EUt(GTValues.VA[GTValues.IV]))
                .duration(600).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.FORGE_HAMMER[GTValues.IV].getStackForm(),
                        MetaTileEntities.ELECTRIC_BLAST_FURNACE.getStackForm(),
                        MetaTileEntities.VACUUM_FREEZER.getStackForm(),
                        MetaItems.ROBOT_ARM_IV.getStackForm(8))
                .input(OrePrefix.foil, Materials.Invar, 24)
                .input(OrePrefix.dust, Materials.Graphene, 8)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.LuV, 2)
                .input(OrePrefix.frameGt, GCYMMaterials.TantalumCarbide, 4)
                .fluidInputs(Materials.SolderingAlloy.getFluid(GTValues.L * 4),
                        Materials.Lubricant.getFluid(2000),
                        GTCEFuCMaterials.EutecticCaesiumSodiumPotassium.getFluid(10000))
                .output(GTCEFuCMetaTileEntities.FORGING_FURNACE)
                .scannerResearch(b -> b
                        .researchStack(GCYMMetaTileEntities.ALLOY_BLAST_SMELTER.getStackForm())
                        .EUt(GTValues.VA[GTValues.EV]))
                .duration(800).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(MetaTileEntities.FUSION_REACTOR[2].getStackForm(2))
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV, 4)
                .input(OrePrefix.ingot, Materials.SteelMagnetic, 24)
                .input(OrePrefix.plate, Materials.Neutronium)
                .input(OrePrefix.dust, Materials.Lutetium, 64)
                .input(OrePrefix.dust, Materials.Lutetium, 16)
                .input(OrePrefix.stick, Materials.Thorium, 64)
                .input(MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT, 64)
                .input(OrePrefix.wireGtSingle, Materials.RutheniumTriniumAmericiumNeutronate, 32)
                .input(MetaItems.SENSOR_UV, 8)
                .input(MetaItems.FIELD_GENERATOR_UV, 8)
                .fluidInputs(Materials.SolderingAlloy.getFluid(GTValues.L * 12),
                        Materials.IndiumGalliumPhosphide.getFluid(GTValues.L * 8),
                        Materials.Bismuth.getFluid(GTValues.L * 5))
                .output(GTCEFuCMetaTileEntities.FUSION_STACK[0])
                .stationResearch(b -> b
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
                .input(OrePrefix.stick, Materials.Plutonium241, 64)
                .input(MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT, 64)
                .input(OrePrefix.wireGtDouble, Materials.RutheniumTriniumAmericiumNeutronate, 32)
                .input(MetaItems.ELECTRIC_PUMP_EV, 32)
                .fluidInputs(Materials.SolderingAlloy.getFluid(GTValues.L * 12),
                        Materials.Osmiridium.getFluid(GTValues.L * 8),
                        GTCEFuCMaterials.EutecticCaesiumPotassiumGalliumNaquadahEnriched.getFluid(50000))
                .output(GTCEFuCMetaTileEntities.FUSION_STACK[1])
                .stationResearch(b -> b
                        .researchStack(GTCEFuCMetaTileEntities.FUSION_STACK[0].getStackForm())
                        .CWUt(288)
                        .EUt(GTValues.VA[GTValues.UHV]))
                .duration(1200).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();

        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(GTCEFuCMetaTileEntities.FUSION_STACK[1].getStackForm(2))
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV, 12)
                .input(OrePrefix.ingot, Materials.SamariumMagnetic, 48)
                .input(OrePrefix.plateDense, Materials.Neutronium)
                .input(OrePrefix.dust, Materials.Darmstadtium, 64)
                .input(OrePrefix.dust, Materials.Darmstadtium, 64)
                .input(OrePrefix.stick, Materials.Uranium235, 64)
                .input(MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT, 64)
                .input(OrePrefix.wireGtQuadruple, Materials.RutheniumTriniumAmericiumNeutronate, 32)
                .input(MetaBlocks.OPTICAL_PIPES[0], 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(GTValues.L * 12),
                        Materials.NaquadahAlloy.getFluid(GTValues.L * 8),
                        Materials.PCBCoolant.getFluid(30000))
                .output(GTCEFuCMetaTileEntities.FUSION_STACK[2])
                .stationResearch(b -> b
                        .researchStack(GTCEFuCMetaTileEntities.FUSION_STACK[1].getStackForm())
                        .CWUt(384)
                        .EUt(GTValues.VA[GTValues.UEV]))
                .duration(1200).EUt(GTValues.VA[GTValues.UEV]).buildAndRegister();

        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(MetaTileEntities.STEAM_EXTRACTOR_STEEL)
                .inputs(GTCEFuCMetaBlocks.ADVANCED_CASING
                        .getItemVariant(GTCEFuCBlockAdvancedCasing.AdvancedCasingType.NULL_FIELD_CASING),
                        MetaTileEntities.FUSION_REACTOR[1].getStackForm(2),
                        MetaTileEntities.FUSION_REACTOR[2].getStackForm())
                .input(OrePrefix.circuit, MarkerMaterials.Tier.ULV, 64)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.ULV, 64)
                .input(OrePrefix.plateDouble, Materials.MagnesiumDiboride)
                .input(OrePrefix.plateDense, Materials.ManganesePhosphide)
                .input(OrePrefix.cableGtOctal, Materials.RutheniumTriniumAmericiumNeutronate, 16)
                .fluidInputs(Materials.SolderingAlloy.getFluid(GTValues.L * 16),
                        Materials.NickelZincFerrite.getFluid(GTValues.L * 8))
                .output(GTCEFuCMetaTileEntities.STAR_SIPHON)
                .stationResearch(b -> b
                        .researchStack(GTCEFuCMetaBlocks.ADVANCED_CASING
                                .getItemVariant(GTCEFuCBlockAdvancedCasing.AdvancedCasingType.NULL_FIELD_CASING))
                        .CWUt(480)
                        .EUt(GTValues.VA[GTValues.UEV]))
                .duration(1400).EUt(GTValues.VA[GTValues.UEV]).buildAndRegister();

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
                .fluidInputs(Materials.SolderingAlloy.getFluid(GTValues.L * 16),
                        Materials.RutheniumTriniumAmericiumNeutronate.getFluid(GTValues.L * 9 * 64))
                .output(GTCEFuCMetaTileEntities.ANTIMATTER_COMPRESSOR)
                .stationResearch(b -> b
                        .researchStack(GTCEFuCMetaTileEntities.STAR_SIPHON.getStackForm())
                        .CWUt(576)
                        .EUt(GTValues.VA[GTValues.UEV]))
                .duration(1600).EUt(GTValues.VA[GTValues.UEV]).buildAndRegister();
    }
}
