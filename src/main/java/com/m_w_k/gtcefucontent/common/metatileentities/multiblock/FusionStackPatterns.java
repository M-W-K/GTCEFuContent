package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;
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
                .where('W', stateIndex(15))
                .where('K', stateIndex(12))
                .where('L', stateIndex(13))
                .where('T', stateIndex(14))
                .where('C', stateIndex(20 + variant))
                .where('I', getFluidHatchAlternate(variant).or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(2, 4)))
                .where('O', getFluidHatchAlternate(variant).or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(4)))
                .where('Y', stateIndex(5).or(getEnergyHatches(variant).setPreviewCount((int) (8 * Math.pow(2.0, variant)))))
                .where('D', air())
                .where('#', any());
    }

//    static MultiblockShapeInfo.Builder whereify(MultiblockShapeInfo.Builder pattern, int variant) {
//        return pattern
//    }

    private static TraceabilityPredicate getEnergyHatches(int variant) {
        TraceabilityPredicate predicate = metaTileEntities(MetaTileEntities.ENERGY_INPUT_HATCH_16A[4]);
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
            case 12 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF);
            case 13 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN);
            case 14 -> MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE);
            case 15 -> GTCEFuCMetaBlocks.HARDENED_CASING.getState(GTCEFuCBlockHardenedCasing.CasingType.PLASMA_PIPE_CASING);
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
                    "S#YUPNNWNNPUY#S",
                    "#JDPPRMWMRPPDJ#",
                    "#JDNRZ#I#ZRNDJ#",
                    "JDNNM#####MNNDJ",
                    "JDNWWO#C#OWWNDJ",
                    "JDNNM#####MNNDJ",
                    "#JDNRZ#I#ZRNDJ#",
                    "#JDPPRMWMRPPDJ#",
                    "S#YUPNNWNNPUY#S",
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
                    "S#YUPNNWNNPUY#S",
                    "#JDPPRMWMRPPDJ#",
                    "#JDNRZ#I#ZRNDJ#",
                    "JDNNM#####MNNDJ",
                    "JDNWWO#C#OWWNDJ",
                    "JDNNM#####MNNDJ",
                    "#JDNRZ#I#ZRNDJ#",
                    "#JDPPRMWMRPPDJ#",
                    "S#YUPNNWNNPUY#S",
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
    static final FactoryBlockPattern FUSION_ARRAY = FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
            .aisle(
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "E#################################E",
                    "S#################################S",
                    "E#####S#S#################S#S#####E",
                    "E######E###################E######E",
                    "E#####S#S#################S#S#####E",
                    "S#################################S",
                    "E#################################E",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################")
            .aisle(
                    "##ESEEEEEEESE#########ESEEEEEEESE##",
                    "###E#######E###########E#######E###",
                    "###E#######E###########E#######E###",
                    "###EKK###KKE#####K#####EKK###KKE###",
                    "###KZZK#KZZK####KZK####KZZK#KZZK###",
                    "S##KZZK#KZZK####KZK####KZZK#KZZK##S",
                    "EEEEKKS#SKK######K######KKS#SKKEEEE",
                    "#######C###################C#######",
                    "EEEEKKS#SKK######K######KKS#SKKEEEE",
                    "S##KZZK#KZZK####KZK####KZZK#KZZK##S",
                    "###KZZK#KZZK####KZK####KZZK#KZZK###",
                    "###EKK###KKE#####K#####EKK###KKE###",
                    "###E#######E###########E#######E###",
                    "###E#######E###########E#######E###",
                    "##ESEEEEEEESE#########ESEEEEEEESE##")
            .aisle(
                    "###S#######S###########S#######S###",
                    "###################################",
                    "###################################",
                    "###KZZK#KZZK####KZK####KZZK#KZZK###",
                    "###ZTTTTTTTTTTTTTTTTTTTTTTTTTTTZ###",
                    "S##ZTTTTTTTTTTTTTTTTTTTTTTTTTTTZ##S",
                    "###KZZK#KZZK####KZK####KZZK#KZZK###",
                    "#######C###################C#######",
                    "###KZZK#KZZK####KZK####KZZK#KZZK###",
                    "S##ZTTTTTTTTTTTTTTTTTTTTTTTTTTTZ##S",
                    "###ZTTTTTTTTTTTTTTTTTTTTTTTTTTTZ###",
                    "###KZZK#KZZK####KZK####KZZK#KZZK###",
                    "###################################",
                    "###################################",
                    "###S#######S###########S#######S###")
            .aisle(
                    "###S#######S###########S#######S###",
                    "###################################",
                    "###################################",
                    "####KK###KK######K######KK###KK####",
                    "###KTTK#KTTK####KTK####KTTK#KTTK###",
                    "S##KTTK#KTTK####KTK####KTTK#KTTK##S",
                    "####KK###KK######K######KK###KK####",
                    "#######C###################C#######",
                    "####KK###KK######K######KK###KK####",
                    "S##KTTK#KTTK####KTK####KTTK#KTTK##S",
                    "###KTTK#KTTK####KTK####KTTK#KTTK###",
                    "####KK###KK######K######KK###KK####",
                    "###################################",
                    "###################################",
                    "###S#######S###########S#######S###")
            .aisle(
                    "###S#######S###########S#######S###",
                    "###################################",
                    "###################################",
                    "###################################",
                    "####TT###TT######T######TT###TT####",
                    "S###TT###TT######T######TT###TT###S",
                    "###################################",
                    "#######C###################C#######",
                    "###################################",
                    "S###TT###TT######T######TT###TT###S",
                    "####TT###TT######T######TT###TT####",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###S#######S###########S#######S###")
            .aisle(
                    "###S#######S###########S#######S###",
                    "######M#M#################M#M######",
                    "######M#M#################M#M######",
                    "####LL###LL#####LLL#####LL###LL####",
                    "###LTTL#LTTL####LTL####LTTL#LTTL###",
                    "S##LTTL#LTTL####LTL####LTTL#LTTL##S",
                    "#MM#LL###LL#MM##LLL##MM#LL###LL#MM#",
                    "#######C########LLL########C#######",
                    "#MM#LL###LL#MM##LLL##MM#LL###LL#MM#",
                    "S##LTTL#LTTL####LTL####LTTL#LTTL##S",
                    "###LTTL#LTTL####LTL####LTTL#LTTL###",
                    "####LL###LL#####LLL#####LL###LL####",
                    "######M#M#################M#M######",
                    "######M#M#################M#M######",
                    "###S#######S###########S#######S###")
            .aisle(
                    "###S#######S###########S#######S###",
                    "###################################",
                    "######AAA#################AAA######",
                    "####AAMAMAA#####LVL#####AAMAMAA####",
                    "###ATTA#ATTA####VTV####ATTA#ATTA###",
                    "SEEATTA#ATTA####VTV####ATTA#ATTAEES",
                    "E#AMAA###AAMA###VTV###AMAA###AAMA#E",
                    "E#AA###C###AA###VTV###AA###C###AA#E",
                    "E#AMAA###AAMA###VTV###AMAA###AAMA#E",
                    "SEEATTA#ATTA####VTV####ATTA#ATTAEES",
                    "###ATTA#ATTA####VTV####ATTA#ATTA###",
                    "####AAMAMAA#####LVL#####AAMAMAA####",
                    "######AAA#################AAA######",
                    "###################################",
                    "###S#######S###########S#######S###")
            .aisle(
                    "###SEEEEEEESEEEEEEEEEEESEEEEEEES###",
                    "###E##FFF##E###########E##FFF##E###",
                    "###EFFSSSFFE###########EFFSSSFFE###",
                    "###FSSSESSSF####LVL####FSSSESSSF###",
                    "##FSPRMEMRPSF###VTV###FSPRMEMRPSF##",
                    "S#FSRZ#E#ZRSF###VTV###FSRZ#E#ZRSF#S",
                    "#FSSM#####MSSF##VTV##FSSM#####MSSF#",
                    "#FSEEE#C#EEESF##VTV##FSEEE#C#EEESF#",
                    "#FSSM#####MSSF##VTV##FSSM#####MSSF#",
                    "S#FSRZ#E#ZRSF###VTV###FSRZ#E#ZRSF#S",
                    "##FSPRMEMRPSF###VTV###FSPRMEMRPSF##",
                    "###FSSSESSSF####LVL####FSSSESSSF###",
                    "###EFFSSSFFE###########EFFSSSFFE###",
                    "###E##FFF##E###########E##FFF##E###",
                    "###SEEEEEEESEEEEEEEEEEESEEEEEEES###")
            .aisle(
                    "###S##JJJ##S###########S##JJJ##S###",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "###YDDNNNDDY###########YDDNNNDDY###",
                    "##YUPNNWNNPUY###LVL###YUPNNWNNPUY##",
                    "#JDPPRMWMRPPDJ##VTV##JDPPRMWMRPPDJ#",
                    "SJDNRZ#W#ZRNDJ##VTV##JDNRZ#W#ZRNDJS",
                    "JDNNM##W##MNNDJ#VTV#JDNNM##W##MNNDJ",
                    "JDNWWWWCWWWWNDJ#VTV#JDNWWWWCWWWWNDJ",
                    "JDNNM##W##MNNDJ#VTV#JDNNM##W##MNNDJ",
                    "SJDNRZ#W#ZRNDJ##VTV##JDNRZ#W#ZRNDJS",
                    "#JDPPRMWMRPPDJ##VTV##JDPPRMWMRPPDJ#",
                    "##YUPNNWNNPUY###LVL###YUPNNWNNPUY##",
                    "###YDDNNNDDY###########YDDNNNDDY###",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "###S##JJJ##S###########S##JJJ##S###")
            .aisle(
                    "###S###E###S###########S###E###S###",
                    "######FFF#################FFF######",
                    "####FFSSSFF#############FFSSSFF####",
                    "###FSSSESSSF####LVL####FSSSESSSF###",
                    "##FSPRMEMRPSF###VTV###FSPRMEMRPSF##",
                    "S#FSRZ#E#ZRSF###VTV###FSRZ#E#ZRSF#S",
                    "#FSSM##W##MSSF##VTV##FSSM##W##MSSF#",
                    "EFSEEEWCWEEESFE#VTV#EFSEEEWCWEEESFE",
                    "#FSSM##W##MSSF##VTV##FSSM##W##MSSF#",
                    "S#FSRZ#E#ZRSF###VTV###FSRZ#E#ZRSF#S",
                    "##FSPRMEMRPSF###VTV###FSPRMEMRPSF##",
                    "###FSSSESSSF####LVL####FSSSESSS####",
                    "####FFSSSFF#############FFSSSFF####",
                    "######FFF#################FFF######",
                    "###S###E###S###########S###E###S###")
            .aisle(
                    "###S###E###S###########S###E###S###",
                    "#######E###################E#######",
                    "#####EEEEE###############EEEEE#####",
                    "###GGGEGEGGG####LVL####GGGEGEGGG###",
                    "###GPGMGMGPG####VTV####GPGMGMGPG###",
                    "S#EGGG#E#GGGE###VTV###EGGG#E#GGGE#S",
                    "##EEM#WWW#MEE###VTV###EEM#WWW#MEE##",
                    "EEEGGEWCWEGGEEE#VTV#EEEGGEWCWEGGEEE",
                    "##EEM#WWW#MEE###VTV###EEM#WWW#MEE##",
                    "S#EGGG#E#GGGE###VTV###EGGG#E#GGGE#S",
                    "###GPGMGMGPG####VTV####GPGMGMGPG###",
                    "###GGGEGEGGG####LVL####GGGEGEGGG###",
                    "#####EEEEE###############EEEEE#####",
                    "#######E###################E#######",
                    "###S###E###S###########S###E###S###")
            .aisle(
                    "###S###E###S###########S###E###S###",
                    "######FFF#################FFF######",
                    "####FFSSSFF#############FFSSSFF####",
                    "###FSSSESSSF###LLLLL###FSSSESSSF###",
                    "##FSPRMEMRPSF##LLTLL##FSPRMEMRPSF##",
                    "S#FSRZ#E#ZRSF##LLTLL##FSRZ#E#ZRSF#S",
                    "#FSSM##W##MSSF#LLLLL#FSSM##W##MSSF#",
                    "EFSEEEWCWEEESFE#LLL#EFSEEEWCWEEESFE",
                    "#FSSM##W##MSSF##LLL##FSSM##W##MSSF#",
                    "S#FSRZ#E#ZRSF###LLL###FSRZ#E#ZRSF#S",
                    "##FSPRMEMRPSF###LTL###FSPRMEMRPSF##",
                    "###FSSSESSSF####LLL####FSSSESSS####",
                    "####FFSSSFF#############FFSSSFF####",
                    "######FFF#################FFF######",
                    "###S###E###S###########S###E###S###")
            .aisle(
                    "###S##JJJ##S###########S##JJJ##S###",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "###YDDNNNDDY###########YDDNNNDDY###",
                    "##YUPNNWNNPUY##LVVVL##YUPNNWNNPUY##",
                    "#JDPPRMWMRPPDJ#VTTTV#JDPPRMWMRPPDJ#",
                    "SJDNRZ#W#ZRNDJ#VTTTV#JDNRZ#W#ZRNDJS",
                    "JDNNM##W##MNNDJLVVVLJDNNM##W##MNNDJ",
                    "JDNWWWWCWWWWNDJ#####JDNWWWWCWWWWNDJ",
                    "JDNNM##W##MNNDJ#####JDNNM##W##MNNDJ",
                    "SJDNRZ#W#ZRNDJ#######JDNRZ#W#ZRNDJS",
                    "#JDPPRMWMRPPDJ###T###JDPPRMWMRPPDJ#",
                    "##YUPNNWNNPUY#########YUPNNWNNPUY##",
                    "###YDDNNNDDY###########YDDNNNDDY###",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "###S##JJJ##S###########S##JJJ##S###")
            .aisle(
                    "###SEEEEEEESEEEEEEEEEEESEEEEEEES###",
                    "###E##FFF##E###########E##FFF##E###",
                    "###EFFSSSFFE###########EFFSSSFFE###",
                    "###FSSSESSSF###LVVVL###FSSSESSSF###",
                    "##FSPRMEMRPSF##VTTTV##FSPRMEMRPSF##",
                    "S#FSRZ#E#ZRSF##VTTTV##FSRZ#E#ZRSF#S",
                    "#FSSM#####MSSF#LVVVL#FSSM#####MSSF#",
                    "#FSEEE#CWEEESF#######FSEEEWC#EEESF#",
                    "#FSSM#####MSSF#######FSSM#####MSSF#",
                    "S#FSRZ#E#ZRSF###KKK###FSRZ#E#ZRSF#S",
                    "##FSPRMEMRPSF###KTK###FSPRMEMRPSF##",
                    "###FSSSESSSF####KKK####FSSSESSSF###",
                    "###EFFSSSFFE###########EFFSSSFFE###",
                    "###E##FFF##E###########E##FFF##E###",
                    "###SEEEEEEESEEEEEEEEEEESEEEEEEES###")
            .aisle(
                    "###S#######S###########S#######S###",
                    "###################################",
                    "######AAA#################AAA######",
                    "####AAMAMAA####LVVVL####AAMAMAA####",
                    "###ATTA#ATTA###VTTTV###ATTA#ATTA###",
                    "SEEATTA#ATTA###VTTTV###ATTA#ATTAEES",
                    "E#AMAA###AAMA##LVVVL##AMAA###AAMA#E",
                    "E#AA###CW##AA#########AA##WC###AA#E",
                    "E#AMAA###AAMA###KKK###AMAA###AAMA#E",
                    "SEEATTA#ATTA####IWI####ATTA#ATTAEES",
                    "###ATTA#ATTA####ITI####ATTA#ATTA###",
                    "####AAMAMAA#####KXK#####AAMAMAA####",
                    "######AAA#################AAA######",
                    "###################################",
                    "###S#######S###########S#######S###")
            .aisle(
                    "###S#######S###########S#######S###",
                    "######M#M#################M#M######",
                    "######M#M#################M#M######",
                    "####LL###LL####LLLLL####LL###LL####",
                    "###LTTL#LTTL###LTTTL###LTTL#LTTL###",
                    "S##LTTL#LTTL###LTTTL###LTTL#LTTL##S",
                    "#MM#LL###LL#MM#LLLLL#MM#LL###LL#MM#",
                    "#######CWWWWWWWWWWWWWWWWWWWC#######",
                    "#MM#LL###LL#MM##KWK##MM#LL###LL#MM#",
                    "S##LTTL#LTTL####OWO####LTTL#LTTL##S",
                    "###LTTL#LTTL####OKO####LTTL#LTTL###",
                    "####LL###LL#####KKK#####LL###LL####",
                    "######M#M#################M#M######",
                    "######M#M#################M#M######",
                    "###S#######S###########S#######S###")
            .aisle(
                    "###S#######S###########S#######S###",
                    "###################################",
                    "###################################",
                    "###################################",
                    "####TT###TT#####TTT#####TT###TT####",
                    "S###TT###TT#####TTT#####TT###TT###S",
                    "###################################",
                    "#######C###################C#######",
                    "################KKK################",
                    "S###TT###TT#####KKK#####TT###TT###S",
                    "####TT###TT#####KKK#####TT###TT####",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###S#######S###########S#######S###")
            .aisle(
                    "###S#######S###########S#######S###",
                    "###################################",
                    "###################################",
                    "####KK###KK#####KKK#####KK###KK####",
                    "###KTTK#KTTK###KTTTK###KTTK#KTTK###",
                    "S##KTTK#KTTK###KTTTK###KTTK#KTTK##S",
                    "####KK###KK#####KKK#####KK###KK####",
                    "#######C###################C#######",
                    "####KK###KK#############KK###KK####",
                    "S##KTTK#KTTK###########KTTK#KTTK##S",
                    "###KTTK#KTTK###########KTTK#KTTK###",
                    "####KK###KK#############KK###KK####",
                    "###################################",
                    "###################################",
                    "###S#######S###########S#######S###")
            .aisle(
                    "###S#######S###########S#######S###",
                    "###################################",
                    "###################################",
                    "###KZZK#KZZK###KZZZK###KZZK#KZZK###",
                    "###ZTTTTTTTTTTTTTTTTTTTTTTTTTTTZ###",
                    "S##ZTTTTTTTTTTTTTTTTTTTTTTTTTTTZ##S",
                    "###KTTK#KTTK###KZZZK###KTTK#KTTK###",
                    "####TT#C#TT#############TT#C#TT####",
                    "###KTTK#KTTK###########KTTK#KTTK###",
                    "S##ZTTZ#ZTTZ###########ZTTZ#ZTTZ##S",
                    "###ZTTZ#ZTTZ###########ZTTZ#ZTTZ###",
                    "###KZZK#KZZK###########KZZK#KZZK###",
                    "###################################",
                    "###################################",
                    "###S#######S###########S#######S###")
            .aisle(
                    "##ESEEEEEEESE#########ESEEEEEEESE##",
                    "###E#######E###########E#######E###",
                    "###E#######E###########E#######E###",
                    "###EKK###KKE####KKK####EKK###KKE###",
                    "###KZZK#KZZK###KZZZK###KZZK#KZZK###",
                    "S##KZZK#KZZK###KZZZK###KZZK#KZZK##S",
                    "EEEEKKS#SKK#####KKK#####KKS#SKKEEEE",
                    "#######C###################C#######",
                    "EEEEKKS#SKK#############KKS#SKKEEEE",
                    "S##KZZK#KZZK###########KZZK#KZZK##S",
                    "###KZZK#KZZK###########KZZK#KZZK###",
                    "###EKK###KKE###########EKK###KKE###",
                    "###E#######E###########E#######E###",
                    "###E#######E###########E#######E###",
                    "##ESEEEEEEESE#########ESEEEEEEESE##")
            .aisle(
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "E#################################E",
                    "S#################################S",
                    "E#####S#S#################S#S#####E",
                    "E######E###################E######E",
                    "E#####S#S#################S#S#####E",
                    "S#################################S",
                    "E#################################E",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################");
    static final FactoryBlockPattern FUSION_COMPLEX = FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP);
}
