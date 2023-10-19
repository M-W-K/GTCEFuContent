package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.RelativeDirection;
import gregtech.common.blocks.*;
import gregtech.common.metatileentities.MetaTileEntities;

import static gregtech.api.metatileentity.multiblock.MultiblockControllerBase.*;

final class FusionStackPatterns {
    static void init() {
        whereify(FUSION_STACK, 1);
        whereify(FUSION_ARRAY, 2);
        whereify(FUSION_COMPLEX, 3);
    }

    private static void whereify(FactoryBlockPattern pattern, int variant) {
        pattern
                .where('E', stateIndex(1))
                .where('S', frames(Materials.HSSS))
                .where('G', stateIndex(3))
                .where('A', stateIndex(0))
                .where('M', stateIndex(4))
                .where('F', stateIndex(5))
                .where('B', stateIndex(6).or(stateIndex(5)))
                .where('P', stateIndex(7))
                .where('U', stateIndex(8))
                .where('V', stateIndex(9))
                .where('R', stateIndex(10))
                .where('Z', stateIndex(11))
                .where('N', frames(Materials.Neutronium))
                .where('J', stateIndex(5).or(stateIndex(6)))
                .where('C', stateIndex(20 + variant))
                .where('I', getFluidHatchAlternate(variant).or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(2, 4)))
                .where('O', getFluidHatchAlternate(variant).or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(4)))
                .where('Y', stateIndex(5).or(getEnergyHatches(variant).setPreviewCount(16)))
                .where('D', air())
                .where('#', any());
    }

//    static MultiblockShapeInfo.Builder whereify(MultiblockShapeInfo.Builder pattern, int variant) {
//        return pattern
//                .where('E', stateIndex(1))
//                .where('S', frames(Materials.HSSS))
//                .where('G', stateIndex(3))
//                .where('A', stateIndex(0))
//                .where('M', stateIndex(4))
//                .where('F', stateIndex(5))
//                .where('B', stateIndex(6).or(stateIndex(5)))
//                .where('P', stateIndex(7))
//                .where('U', stateIndex(8))
//                .where('V', stateIndex(9))
//                .where('R', stateIndex(10))
//                .where('Z', stateIndex(11))
//                .where('N', frames(Materials.Neutronium))
//                .where('J', stateIndex(5).or(stateIndex(6)))
//                .where('C', stateIndex(20 + variant))
//                .where('I', getFluidHatchAlternate(variant).or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(2, 4)))
//                .where('O', getFluidHatchAlternate(variant).or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(4)))
//                .where('Y', stateIndex(5).or(getEnergyHatches(variant)))
//                .where('D', air())
//                .where('#', any());
//    }

    private static TraceabilityPredicate getEnergyHatches(int variant) {
        // I don't know if it's legal to use substation hatches, but that ain't stopping me!
        TraceabilityPredicate predicate = metaTileEntities(MetaTileEntities.SUBSTATION_ENERGY_INPUT_HATCH[4])
                .or(metaTileEntities(MetaTileEntities.ENERGY_INPUT_HATCH_16A[4]));
        if (variant <= 2) {
            predicate = predicate.or(metaTileEntities(MetaTileEntities.ENERGY_INPUT_HATCH_4A[5]));
            if (variant == 1) {
                predicate = predicate.or(metaTileEntities(MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.UHV]));
            }
        }
        return predicate;
    }

    private static TraceabilityPredicate getFluidHatchAlternate(int variant) {
        return states(switch (variant) {
            default -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.HSSE_STURDY);
            case 2 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF);
            case 3 -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.ATOMIC_CASING);
        });
    }

    private static TraceabilityPredicate stateIndex(int id) {
        return states(switch (id) {
            default -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.ATOMIC_CASING);
            case 1 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.HSSE_STURDY);
            case 3 -> MetaBlocks.WIRE_COIL.getState(BlockWireCoil.CoilType.HSS_G);
            case 4 -> GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.MOLYBDENUM_DISILICIDE_COIL);
            case 5 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK3);
            case 6 -> MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
            case 7 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK2);
            case 8 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_COIL);
            case 9 -> GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT);
            case 10 -> MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.FILTER_CASING);
            case 11 -> MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.EXTREME_ENGINE_INTAKE_CASING);
            case 13 -> MetaBlocks.COMPRESSED.get(Materials.Darmstadtium).getBlock(Materials.Darmstadtium);
            case 21 -> MetaBlocks.COMPRESSED.get(Materials.SteelMagnetic).getBlock(Materials.SteelMagnetic);
            case 22 -> MetaBlocks.COMPRESSED.get(Materials.NeodymiumMagnetic).getBlock(Materials.NeodymiumMagnetic);
            case 23 -> MetaBlocks.COMPRESSED.get(Materials.SamariumMagnetic).getBlock(Materials.SamariumMagnetic);
        });
    }
    static final FactoryBlockPattern FUSION_STACK = FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
            .aisle(
                    "####ESEEESE####",
                    "#####E###E#####",
                    "#####EAAAE#####",
                    "####AAMAMAA####",
                    "###AVVASAVVA###",
                    "###AVVASAVVA###",
                    "##AMAA#S#AAMA##",
                    "##AASSSESSSAA##",
                    "##AMAA#S#AAMA##",
                    "###AVVASAVVA###",
                    "###AVVASAVVA###",
                    "####AAMAMAA####",
                    "#####EAAAE#####",
                    "#####E###E#####",
                    "####ESEEESE####")
            .aisle(
                    "#####S###S#####",
                    "######FFF######",
                    "E###FFSSSFF###E",
                    "SEEFSSSESSSFEES",
                    "E#FSPRMEMRPSF#E",
                    "E#FSRZ#E#ZRSF#E",
                    "EFSSM#####MSSFE",
                    "EFSEEE#C#EEESFE",
                    "EFSSM#####MSSFE",
                    "E#FSRZ#E#ZRSF#E",
                    "E#FSPRMEMRPSF#E",
                    "SEEFSSSESSSFEES",
                    "E###FFSSSFF###E",
                    "######FFF######",
                    "#####S###S#####")
            .aisle(
                    "#####SJJJS#####",
                    "####JJDDDJJ####",
                    "###YDDNNNDDY###",
                    "S#YUPNNSNNPUY#S",
                    "#JDPPRMSMRPPDJ#",
                    "#JDNRZ#I#ZRNDJ#",
                    "JDNNM#####MNNDJ",
                    "JDNSSI#C#ISSNDJ",
                    "JDNNM#####MNNDJ",
                    "#JDNRZ#I#ZRNDJ#",
                    "#JDPPRMSMRPPDJ#",
                    "S#YUPNNSNNPUY#S",
                    "###YDDNNNDDY###",
                    "####JJDDDJJ####",
                    "#####SJJJS#####")
            .aisle(
                    "#####S#E#S#####",
                    "######FFF######",
                    "####FFSSSFF####",
                    "S##FSSSESSSF##S",
                    "##FSPRMEMRPSF##",
                    "##FSRZ#E#ZRSF##",
                    "#FSSM#####MSSF#",
                    "EFSEEE#C#EEESFE",
                    "#FSSM#####MSSF#",
                    "##FSRZ#E#ZRSF##",
                    "##FSPRMEMRPSF##",
                    "S##FSSSESSSF##S",
                    "####FFSSSFF####",
                    "######FFF######",
                    "#####S#E#S#####")
            .aisle(
                    "#####S#E#S#####",
                    "#######E#######",
                    "#####EEEEE#####",
                    "S##GGGEGEGGG##S",
                    "###GPGMGMGPG###",
                    "##EGGG#E#GGGE##",
                    "##EEM#####MEE##",
                    "EEEGGE#C#EGGEEE",
                    "##EEM#####MEE##",
                    "##EGGG#E#GGGE##",
                    "###GPGMGMGPG###",
                    "S##GGGEGEGGG##S",
                    "#####EEEEE#####",
                    "#######E#######",
                    "#####S#X#S#####")
            .aisle(
                    "#####S#E#S#####",
                    "######FFF######",
                    "####FFSSSFF####",
                    "S##FSSSESSSF##S",
                    "##FSPRMEMRPSF##",
                    "##FSRZ#E#ZRSF##",
                    "#FSSM#####MSSF#",
                    "EFSEEE#C#EEESFE",
                    "#FSSM#####MSSF#",
                    "##FSRZ#E#ZRSF##",
                    "##FSPRMEMRPSF##",
                    "S##FSSSESSSF##S",
                    "####FFSSSFF####",
                    "######FFF######",
                    "#####S#E#S#####")
            .aisle(
                    "#####SJJJS#####",
                    "####JJDDDJJ####",
                    "###YDDNNNDDY###",
                    "S#YUPNNSNNPUY#S",
                    "#JDPPRMSMRPPDJ#",
                    "#JDNRZ#O#ZRNDJ#",
                    "JDNNM#####MNNDJ",
                    "JDNSSO#C#OSSNDJ",
                    "JDNNM#####MNNDJ",
                    "#JDNRZ#O#ZRNDJ#",
                    "#JDPPRMSMRPPDJ#",
                    "S#YUPNNSNNPUY#S",
                    "###YDDNNNDDY###",
                    "####JJDDDJJ####",
                    "#####SJJJS#####")
            .aisle(
                    "#####S###S#####",
                    "######FFF######",
                    "E###FFSSSFF###E",
                    "SEEFSSSESSSFEES",
                    "E#FSPRMEMRPSF#E",
                    "E#FSRZ#E#ZRSF#E",
                    "EFSSM#####MSSFE",
                    "EFSEEE#C#EEESFE",
                    "EFSSM#####MSSFE",
                    "E#FSRZ#E#ZRSF#E",
                    "E#FSPRMEMRPSF#E",
                    "SEEFSSSESSSFEES",
                    "E###FFSSSFF###E",
                    "######FFF######",
                    "#####S###S#####")
            .aisle(
                    "####ESEEESE####",
                    "#####E###E#####",
                    "#####EAAAE#####",
                    "####AAMAMAA####",
                    "###AVVASAVVA###",
                    "###AVVASAVVA###",
                    "##AMAA#S#AAMA##",
                    "##AASSSESSSAA##",
                    "##AMAA#S#AAMA##",
                    "###AVVASAVVA###",
                    "###AVVASAVVA###",
                    "####AAMAMAA####",
                    "#####EAAAE#####",
                    "#####E###E#####",
                    "####ESEEESE####");
    static final FactoryBlockPattern FUSION_ARRAY = FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP);
    static final FactoryBlockPattern FUSION_COMPLEX = FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP);
}
