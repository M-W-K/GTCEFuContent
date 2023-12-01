package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import static gregtech.api.metatileentity.multiblock.MultiblockControllerBase.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.ArrayUtils;

import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;

import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockWorldState;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.BlockInfo;
import gregtech.api.util.RelativeDirection;
import gregtech.common.blocks.*;
import gregtech.common.metatileentities.MetaTileEntities;

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
                .where('H', stateIndex(16))
                .where('C', stateIndex(20 + variant))
                .where('1', stateIndex(31))
                .where('2', stateIndex(32))
                .where('3', stateIndex(33))
                .where('4', stateIndex(34))
                .where('I',
                        getFluidHatchAlternate(variant)
                                .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(2, 4)))
                .where('O',
                        getFluidHatchAlternate(variant)
                                .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(4)))
                .where('Y',
                        stateIndex(5).or(stateIndex(6))
                                .or(getEnergyHatches().setPreviewCount((int) (8 * Math.pow(2.0, variant)))))
                .where('D', air())
                .where('#', any());
    }

    // static MultiblockShapeInfo.Builder whereify(MultiblockShapeInfo.Builder pattern, int variant) {
    // return pattern
    // }

    private static TraceabilityPredicate getEnergyHatches() {
        return metaTileEntities(MetaTileEntities.ENERGY_INPUT_HATCH_16A[4],
                MetaTileEntities.ENERGY_INPUT_HATCH_4A[5],
                MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.UHV]);
    }

    private static TraceabilityPredicate getFluidHatchAlternate(int variant) {
        return states(switch (variant) {
            default -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.HSSE_STURDY);
            case 2 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF);
            case 3 -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                    .getState(BlockLargeMultiblockCasing.CasingType.ATOMIC_CASING);
        });
    }

    private static TraceabilityPredicate stateIndex(int id) {
        return states(switch (id) {
            default -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                    .getState(BlockLargeMultiblockCasing.CasingType.ATOMIC_CASING);
            case 1 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.HSSE_STURDY);
            case 3 -> MetaBlocks.WIRE_COIL.getState(BlockWireCoil.CoilType.HSS_G);
            case 4 -> GCYMMetaBlocks.UNIQUE_CASING
                    .getState(BlockUniqueCasing.UniqueCasingType.MOLYBDENUM_DISILICIDE_COIL);
            case 5 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK3);
            case 6 -> MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
            case 7 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK2);
            case 8 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_COIL);
            case 9 -> GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT);
            case 10 -> MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.FILTER_CASING_STERILE);
            case 11 -> MetaBlocks.MULTIBLOCK_CASING
                    .getState(BlockMultiblockCasing.MultiblockCasingType.EXTREME_ENGINE_INTAKE_CASING);
            case 12 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF);
            case 13 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN);
            case 14 -> MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE);
            case 15 -> GTCEFuCMetaBlocks.HARDENED_CASING
                    .getState(GTCEFuCBlockHardenedCasing.CasingType.PLASMA_PIPE_CASING);
            case 16 -> MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE);
            case 21 -> MetaBlocks.COMPRESSED.get(Materials.SteelMagnetic).getBlock(Materials.SteelMagnetic);
            case 22 -> MetaBlocks.COMPRESSED.get(Materials.NeodymiumMagnetic).getBlock(Materials.NeodymiumMagnetic);
            case 23 -> MetaBlocks.COMPRESSED.get(Materials.SamariumMagnetic).getBlock(Materials.SamariumMagnetic);
            case 31 -> MetaBlocks.COMPUTER_CASING.getState(BlockComputerCasing.CasingType.COMPUTER_CASING);
            case 32 -> MetaBlocks.COMPUTER_CASING.getState(BlockComputerCasing.CasingType.ADVANCED_COMPUTER_CASING);
            case 33 -> MetaBlocks.COMPUTER_CASING.getState(BlockComputerCasing.CasingType.COMPUTER_HEAT_VENT);
            case 34 -> MetaBlocks.COMPUTER_CASING.getState(BlockComputerCasing.CasingType.HIGH_POWER_CASING);
        });
    }

    // modified to allow for facing control
    public static TraceabilityPredicate metaTileEntitiesModified(EnumFacing facing,
                                                                 MetaTileEntity... metaTileEntities) {
        ResourceLocation[] ids = Arrays.stream(metaTileEntities).filter(Objects::nonNull)
                .map(tile -> tile.metaTileEntityId).toArray(ResourceLocation[]::new);
        return tilePredicate((state, tile) -> ArrayUtils.contains(ids, tile.metaTileEntityId),
                getCandidates(metaTileEntities), facing);
    }

    // I had to add 2 more compile-time dependencies to get this to work. Regret.
    // Also exact duplicate of code from MultiblockControllerBase, but it was private.
    // I don't like reflection.
    private static Supplier<BlockInfo[]> getCandidates(MetaTileEntity... metaTileEntities) {
        return () -> Arrays.stream(metaTileEntities).filter(Objects::nonNull).map(tile -> {
            MetaTileEntityHolder holder = new MetaTileEntityHolder();
            holder.setMetaTileEntity(tile);
            holder.getMetaTileEntity().onPlacement();
            holder.getMetaTileEntity().setFrontFacing(EnumFacing.SOUTH);
            return new BlockInfo(MetaBlocks.MACHINE.getDefaultState(), holder);
        }).toArray(BlockInfo[]::new);
    }

    public static TraceabilityPredicate tilePredicate(@Nonnull BiFunction<BlockWorldState, MetaTileEntity, Boolean> predicate,
                                                      @Nullable Supplier<BlockInfo[]> candidates, EnumFacing facing) {
        return new TraceabilityPredicate(blockWorldState -> {
            TileEntity tileEntity = blockWorldState.getTileEntity();
            if (!(tileEntity instanceof IGregTechTileEntity))
                return false;
            MetaTileEntity metaTileEntity = ((IGregTechTileEntity) tileEntity).getMetaTileEntity();
            if (predicate.apply(blockWorldState, metaTileEntity)) {
                if (metaTileEntity.getFrontFacing() != facing) return false;

                if (metaTileEntity instanceof IMultiblockPart) {
                    Set<IMultiblockPart> partsFound = blockWorldState.getMatchContext().getOrCreate("MultiblockParts",
                            HashSet::new);
                    partsFound.add((IMultiblockPart) metaTileEntity);
                }
                return true;
            }
            return false;
        }, candidates);
    }

    static final FactoryBlockPattern FUSION_STACK = FactoryBlockPattern
            .start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
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

    static final FactoryBlockPattern FUSION_ARRAY = FactoryBlockPattern
            .start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
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
                    "###FSSSESSSF####LVL####FSSSESSSF###",
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
                    "###FSSSESSSF####LLL####FSSSESSSF###",
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
                    "###LTTL#LTTL###LTLTL###LTTL#LTTL###",
                    "S##LTTL#LTTL###LTLTL###LTTL#LTTL##S",
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
                    "####TT###TT#####T#T#####TT###TT####",
                    "S###TT###TT#####T#T#####TT###TT###S",
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
                    "###KTTK#KTTK###KTKTK###KTTK#KTTK###",
                    "S##KTTK#KTTK###KTKTK###KTTK#KTTK##S",
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

    // this thing truly is massive, isn't it
    static final FactoryBlockPattern FUSION_COMPLEX = FactoryBlockPattern
            .start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
            .aisle(
                    "####ESEEESE#############ESEEESE####",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "######S#S#################S#S######",
                    "#######E###################E#######",
                    "######S#S#################S#S######",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "######S#S#################S#S######",
                    "#######E###################E#######",
                    "######S#S#################S#S######",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "####ESEEESE#############ESEEESE####")
            .aisle(
                    "#####SE#ES###############SE#ES#####",
                    "######E#E#################E#E######",
                    "E#####E#E#################E#E#####E",
                    "SEEEKKE#EKK#############KKE#EKKEEES",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E###KKS#SKK#############KKS#SKK###E",
                    "E######C###################C######E",
                    "E###KKS#SKK#############KKS#SKK###E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "SEEEKK###KK#############KK###KKEEES",
                    "E#################################E",
                    "###################################",
                    "###################################",
                    "###################################",
                    "####KK###KK#############KK###KK####",
                    "###KZZK#KZZK###########KZZK#KZZK###",
                    "####KK###KK#############KK###KK####",
                    "###################################",
                    "###################################",
                    "###################################",
                    "E#################################E",
                    "SEEEKK###KK#############KK###KKEEES",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E###KKS#SKK#############KKS#SKK###E",
                    "E######C###################C######E",
                    "E###KKS#SKK#############KKS#SKK###E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "SEEEKKE#EKK#############KKE#EKKEEES",
                    "E#####E#E#################E#E#####E",
                    "######E#E#################E#E######",
                    "#####SE#ES###############SE#ES#####")
            .aisle(
                    "#####S###S######ESE######S###S#####",
                    "###################################",
                    "###################################",
                    "S##KZZK#KZZK###########KZZK#KZZK##S",
                    "###ZTTZ#ZTTZ###########ZTTZ#ZTTZ###",
                    "###ZTTZ#ZTTZ###########ZTTZ#ZTTZ###",
                    "###KTTK#KTTK###########KTTK#KTTK###",
                    "####TT#C#TT#############TT#C#TT####",
                    "###KTTK#KTTK###########KTTK#KTTK###",
                    "###ZTTZ#ZTTZ###########ZTTZ#ZTTZ###",
                    "###ZTTZ#ZTTZ###########ZTTZ#ZTTZ###",
                    "S##KTTK#KTTK###########KTTK#KTTK##S",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT####LLLLL####TT###TT####",
                    "###KTTK#KTTK###LLLLL###KTTK#KTTK###",
                    "###ZTTZ#ZTTZ###LLLLL###ZTTZ#ZTTZ###",
                    "###KTTK#KTTK###LLLLL###KTTK#KTTK###",
                    "####TT###TT####LLLLL####TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "S##KTTK#KTTK###########KTTK#KTTK##S",
                    "###ZTTZ#ZTTZ###########ZTTZ#ZTTZ###",
                    "###ZTTZ#ZTTZ###########ZTTZ#ZTTZ###",
                    "###KTTK#KTTK###########KTTK#KTTK###",
                    "####TT#C#TT#############TT#C#TT####",
                    "###KTTK#KTTK###########KTTK#KTTK###",
                    "###ZTTZ#ZTTZ###########ZTTZ#ZTTZ###",
                    "###ZTTZ#ZTTZ###########ZTTZ#ZTTZ###",
                    "S##KZZK#KZZK###########KZZK#KZZK##S",
                    "###################################",
                    "###################################",
                    "#####S###S######ESE######S###S#####")
            .aisle(
                    "#####S###S#######S#######S###S#####",
                    "###################################",
                    "##############E#####E##############",
                    "S###KK###KKEEES#####SEEEKK###KK###S",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "####KK###KK###E#####E###KK###KK####",
                    "#######C######E#####E######C#######",
                    "####KK###KK###E#####E###KK###KK####",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "S###KK###KKEEES#####SEEEKK###KK###S",
                    "##############E#####E##############",
                    "###################################",
                    "###################################",
                    "###############LLVLL###############",
                    "####KK###KK####LHTHL####KK###KK####",
                    "###KTTK#KTTK###VTLTV###KTTK#KTTK###",
                    "####KK###KK####LHTHL####KK###KK####",
                    "###############LLVLL###############",
                    "###################################",
                    "###################################",
                    "##############E#####E##############",
                    "S###KK###KKEEES#####SEEEKK###KK###S",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "####KK###KK###E#####E###KK###KK####",
                    "#######C######E#####E######C#######",
                    "####KK###KK###E#####E###KK###KK####",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "S###KK###KKEEES#####SEEEKK###KK###S",
                    "##############E#####E##############",
                    "###################################",
                    "#####S###S#######S#######S###S#####")
            .aisle(
                    "#####S###S#######S#######S###S#####",
                    "###################################",
                    "###################################",
                    "S#############S#####S#############S",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "###################################",
                    "#######C###################C#######",
                    "###################################",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "S#############S#####S#############S",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###############LLVLL###############",
                    "###############LHTHL###############",
                    "####TT###TT####VTLTV####TT###TT####",
                    "###############LHTHL###############",
                    "###############LLVLL###############",
                    "###################################",
                    "###################################",
                    "###################################",
                    "S#############S#####S#############S",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "###################################",
                    "#######C###################C#######",
                    "###################################",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "S#############S#####S#############S",
                    "###################################",
                    "###################################",
                    "#####S###S#######S#######S###S#####")
            .aisle(
                    "#####S###S#######S#######S###S#####",
                    "######M#M#################M#M######",
                    "######M#M#################M#M######",
                    "S###LL###LL###S#####S###LL###LL###S",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "#MM#LL###LL#MM#######MM#LL###LL#MM#",
                    "#######C###################C#######",
                    "#MM#LL###LL#MM#######MM#LL###LL#MM#",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "S###LL###LL###S#####S###LL###LL###S",
                    "######M#M#################M#M######",
                    "######M#M#################M#M######",
                    "###################################",
                    "###############LLVLL###############",
                    "###LLLLLLLLL###LHTHL###LLLLLLLLL###",
                    "###LTTLLLTTL###VTLTV###LTTLLLTTL###",
                    "###LLLLLLLLL###LHTHL###LLLLLLLLL###",
                    "###############LLVLL###############",
                    "###################################",
                    "######M#M#################M#M######",
                    "######M#M#################M#M######",
                    "S###LL###LL###S#####S###LL###LL###S",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "#MM#LL###LL#MM#######MM#LL###LL#MM#",
                    "#######C###################C#######",
                    "#MM#LL###LL#MM#######MM#LL###LL#MM#",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "S###LL###LL###S#####S###LL###LL###S",
                    "######M#M#################M#M######",
                    "######M#M#################M#M######",
                    "#####S###S#######S#######S###S#####")
            .aisle(
                    "#####SEEESEEEEEEESEEEEEEESEEES#####",
                    "#####E###E###############E###E#####",
                    "#####EAAAE###############EAAAE#####",
                    "S###AAMAMAA###SEEEEES###AAMAMAA###S",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "##AMAA###AAMA#########AMAA###AAMA##",
                    "##AA###C###AA#########AA###C###AA##",
                    "##AMAA###AAMA#########AMAA###AAMA##",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "S###AAMAMAA###SEEEEES###AAMAMAA###S",
                    "######AAA#################AAA######",
                    "###################################",
                    "###################################",
                    "###############LLVLL###############",
                    "###LVVVVVVVL###LHTHL###LVVVVVVVL###",
                    "###VTTTTTTTV###VTLTV###VTTTTTTTV###",
                    "###LVVVVVVVL###LHTHL###LVVVVVVVL###",
                    "###############LLVLL###############",
                    "###################################",
                    "###################################",
                    "######AAA#################AAA######",
                    "S###AAMAMAA###SEEEEES###AAMAMAA###S",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "##AMAA###AAMA#########AMAA###AAMA##",
                    "##AA###C###AA#########AA###C###AA##",
                    "##AMAA###AAMA#########AMAA###AAMA##",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "S###AAMAMAA###SEEEEES###AAMAMAA###S",
                    "#####EAAAE###############EAAAE#####",
                    "#####E###E###############E###E#####",
                    "#####SEEESEEEEEEESEEEEEEESEEES#####")
            .aisle(
                    "#####S###S#######S#######S###S#####",
                    "######FFF#################FFF######",
                    "####FFSSSFF#############FFSSSFF####",
                    "SEEFSSSESSSFEES#H#H#SEEFSSSESSSFEES",
                    "E#FSPRMEMRPSF###H#H###FSPRMEMRPSF#E",
                    "E#FSRZ#E#ZRSF###H#H###FSRZ#E#ZRSF#E",
                    "EFSSM#####MSSF##H#H##FSSM#####MSSFE",
                    "EFSEEE#C#EEESF##H#H##FSEEE#C#EEESFE",
                    "EFSSM#####MSSF##H#H##FSSM#####MSSFE",
                    "E#FSRZ#E#ZRSF###H#H###FSRZ#E#ZRSF#E",
                    "E#FSPRMEMRPSF###H#H###FSPRMEMRPSF#E",
                    "SEEFSSSESSSFEES#H#H#SEEFSSSESSSFEES",
                    "E###FFSSSFF###E#H#H#E###FFSSSFF###E",
                    "E#####FFF#####E#H#H#E#####FFF#####E",
                    "E#############E#H#H#E#############E",
                    "E#############EEHVHEE#############E",
                    "E##LVVVVVVVL###LHTHL###LVVVVVVVL##E",
                    "E##VTTTTTTTV###VTLTV###VTTTTTTTV##E",
                    "E##LVVVVVVVL###LHTHL###LVVVVVVVL##E",
                    "E#############EEHVHEE#############E",
                    "E#############E#H#H#E#############E",
                    "E#####FFF#####E#H#H#E#####FFF#####E",
                    "E###FFSSSFF###E#H#H#E###FFSSSFF###E",
                    "SEEFSSSESSSFEES#H#H#SEEFSSSESSSFEES",
                    "E#FSPRMEMRPSF###H#H###FSPRMEMRPSF#E",
                    "E#FSRZ#E#ZRSF###H#H###FSRZ#E#ZRSF#E",
                    "EFSSM#####MSSF##H#H##FSSM#####MSSFE",
                    "EFSEEE#C#EEESF##H#H##FSEEE#C#EEESFE",
                    "EFSSM#####MSSF##H#H##FSSM#####MSSFE",
                    "E#FSRZ#E#ZRSF###H#H###FSRZ#E#ZRSF#E",
                    "E#FSPRMEMRPSF###H#H###FSPRMEMRPSF#E",
                    "SEEFSSSESSSFEES#H#H#SEEFSSSESSSFEES",
                    "####FFSSSFF#############FFSSSFF####",
                    "######FFF#################FFF######",
                    "#####S###S#######S#######S###S#####")
            .aisle(
                    "#####SJJJS#######S#######SJJJS#####",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "###YDDNNNDDY####222####YDDNNNDDY###",
                    "S#YUPNNWNNPUY#S#618#S#YUPNNWNNPUY#S",
                    "#JDPPRMWMRPPDJ##618##JDPPRMWMRPPDJ#",
                    "#JDNRZ#W#ZRNDJ##618##JDNRZ#W#ZRNDJ#",
                    "JDNNM##W##MNNDJ#618#JDNNM##W##MNNDJ",
                    "JDNWWWWCWWWWNDJ#111#JDNWWWWCWWWWNDJ",
                    "JDNNM##W##MNNDJ#618#JDNNM##W##MNNDJ",
                    "#JDNRZ#W#ZRNDJ##618##JDNRZ#W#ZRNDJ#",
                    "#JDPPRMWMRPPDJ##618##JDPPRMWMRPPDJ#",
                    "S#YUPNNWNNPUY#S#618#S#YUPNNWNNPUY#S",
                    "###YDDNNNDDY####222####YDDNNNDDY###",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "######JJJ#################JJJ######",
                    "##############SKZVZKS##############",
                    "###LVVVVVVVL###ZHTHZ###LVVVVVVVL###",
                    "###VTTTTTTTV###VTLTV###VTTTTTTTV###",
                    "###LVVVVVVVL###ZHTHZ###LVVVVVVVL###",
                    "##############SKZVZKS##############",
                    "######JJJ#################JJJ######",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "###YDDNNNDDY####222####YDDNNNDDY###",
                    "S#YUPNNWNNPUY#S#618#S#YUPNNWNNPUY#S",
                    "#JDPPRMWMRPPDJ##618##JDPPRMWMRPPDJ#",
                    "#JDNRZ#W#ZRNDJ##618##JDNRZ#W#ZRNDJ#",
                    "JDNNM##W##MNNDJ#618#JDNNM##W##MNNDJ",
                    "JDNWWWWCWWWWNDJ#111#JDNWWWWCWWWWNDJ",
                    "JDNNM##W##MNNDJ#618#JDNNM##W##MNNDJ",
                    "#JDNRZ#W#ZRNDJ##618##JDNRZ#W#ZRNDJ#",
                    "#JDPPRMWMRPPDJ##618##JDPPRMWMRPPDJ#",
                    "S#YUPNNWNNPUY#S#618#S#YUPNNWNNPUY#S",
                    "###YDDNNNDDY####222####YDDNNNDDY###",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "#####SJJJS#######S#######SJJJS#####")
            .aisle(
                    "#####S#E#S#######S#######S#E#S#####",
                    "######FFF#################FFF######",
                    "####FFSSSFF#####111#####FFSSSFF####",
                    "S##FSSSESSSF##S#323#S##FSSSESSSF##S",
                    "##FSPRMEMRPSF###323###FSPRMEMRPSF##",
                    "##FSRZ#E#ZRSF###323###FSRZ#E#ZRSF##",
                    "#FSSM#####MSSF##323##FSSM#####MSSF#",
                    "EFSEEE#C#EEESFE#424#EFSEEE#C#EEESFE",
                    "#FSSM#####MSSF##323##FSSM#####MSSF#",
                    "##FSRZ#E#ZRSF###323###FSRZ#E#ZRSF##",
                    "##FSPRMEMRPSF###323###FSPRMEMRPSF##",
                    "S##FSSSESSSF##S#323#S##FSSSESSSF##S",
                    "####FFSSSFF#####111#####FFSSSFF####",
                    "######FFF#################FFF######",
                    "#######E###################E#######",
                    "##############SKZVZKS##############",
                    "###LVVVVVVVL###ZHTHZ###LVVVVVVVL###",
                    "###VTTTTTTTV###VTLTV###VTTTTTTTV###",
                    "###LVVVVVVVL###ZHTHZ###LVVVVVVVL###",
                    "##############SKZVZKS##############",
                    "#######E###################E#######",
                    "######FFF#################FFF######",
                    "####FFSSSFF#####111#####FFSSSFF####",
                    "S##FSSSESSSF##S#323#S##FSSSESSSF##S",
                    "##FSPRMEMRPSF###323###FSPRMEMRPSF##",
                    "##FSRZ#E#ZRSF###323###FSRZ#E#ZRSF##",
                    "#FSSM#####MSSF##323##FSSM#####MSSF#",
                    "EFSEEE#C#EEESFE#424#EFSEEE#C#EEESFE",
                    "#FSSM#####MSSF##323##FSSM#####MSSF#",
                    "##FSRZ#E#ZRSF###323###FSRZ#E#ZRSF##",
                    "##FSPRMEMRPSF###323###FSPRMEMRPSF##",
                    "S##FSSSESSSF##S#323#S##FSSSESSSF##S",
                    "####FFSSSFF#####111#####FFSSSFF####",
                    "######FFF#################FFF######",
                    "#####S#E#S#######S#######S#E#S#####")
            .aisle(
                    "#####S#E#S#######S#######S#E#S#####",
                    "#######E###################E#######",
                    "#####EEEEE######111######EEEEE#####",
                    "S##GGGEGEGGG##S#527#S##GGGEGEGGG##S",
                    "###GPGMGMGPG####527####GPGMGMGPG###",
                    "##EGGG#E#GGGE###527###EGGG#E#GGGE##",
                    "##EEM#WWW#MEE###527###EEM#WWW#MEE##",
                    "EEEGGEWCWEGG44444244444GGEWCWEGGEEE",
                    "##EEM#WWW#MEE###527###EEM#WWW#MEE##",
                    "##EGGG#E#GGGE###527###EGGG#E#GGGE##",
                    "###GPGMGMGPG####527####GPGMGMGPG###",
                    "S##GGGEGEGGG##S#527#S##GGGEGEGGG##S",
                    "#####EEEEE######111######EEEEE#####",
                    "#######E###################E#######",
                    "#######E###################E#######",
                    "##############SKZVZKS##############",
                    "###LVVVVVVVL###ZHTHZ###LVVVVVVVL###",
                    "###VTTTTTTTV###VTLTV###VTTTTTTTV###",
                    "###LVVVVVVVL###ZHTHZ###LVVVVVVVL###",
                    "##############SKZVZKS##############",
                    "#######E###################E#######",
                    "#######E###################E#######",
                    "#####EEEEE######111######EEEEE#####",
                    "S##GGGEGEGGG##S#527#S##GGGEGEGGG##S",
                    "###GPGMGMGPG####527####GPGMGMGPG###",
                    "##EGGG#E#GGGE###527###EGGG#E#GGGE##",
                    "##EEM#WWW#MEE###527###EEM#WWW#MEE##",
                    "EEEGGEWCWEGG44444244444GGEWCWEGGEEE",
                    "##EEM#WWW#MEE###527###EEM#WWW#MEE##",
                    "##EGGG#E#GGGE###527###EGGG#E#GGGE##",
                    "###GPGMGMGPG####527####GPGMGMGPG###",
                    "S##GGGEGEGGG##S#527#S##GGGEGEGGG##S",
                    "#####EEEEE######111######EEEEE#####",
                    "#######E###################E#######",
                    "#####S#E#S#######S#######S#E#S#####")
            .aisle(
                    "#####S#E#S#######S#######S#E#S#####",
                    "######FFF#################FFF######",
                    "####FFSSSFF#####111#####FFSSSFF####",
                    "S##FSSSESSSF##S#323#S##FSSSESSSF##S",
                    "##FSPRMEMRPSF###323###FSPRMEMRPSF##",
                    "##FSRZ#E#ZRSF###323###FSRZ#E#ZRSF##",
                    "#FSSM#####MSSF##323##FSSM#####MSSF#",
                    "EFSEEE#C#EEESFE#424#EFSEEE#C#EEESFE",
                    "#FSSM#####MSSF##323##FSSM#####MSSF#",
                    "##FSRZ#E#ZRSF###323###FSRZ#E#ZRSF##",
                    "##FSPRMEMRPSF###323###FSPRMEMRPSF##",
                    "S##FSSSESSSF##S#323#S##FSSSESSSF##S",
                    "####FFSSSFF#####111#####FFSSSFF####",
                    "######FFF#################FFF######",
                    "#######E###################E#######",
                    "########LLLL##SKZVZKS##LLLL########",
                    "###LVVVVLLTTTTTTHTHTTTTTTLLVVVVL###",
                    "###VTTTTTTTV###TTTTT###VTTTTTTTV###",
                    "###LVVVVLLTTTTTTHTHTTTTTTLLVVVVL###",
                    "########LLLL##SKZVZKS##LLLL########",
                    "#######E###################E#######",
                    "######FFF#################FFF######",
                    "####FFSSSFF#####111#####FFSSSFF####",
                    "S##FSSSESSSF##S#323#S##FSSSESSSF##S",
                    "##FSPRMEMRPSF###323###FSPRMEMRPSF##",
                    "##FSRZ#E#ZRSF###323###FSRZ#E#ZRSF##",
                    "#FSSM#####MSSF##323##FSSM#####MSSF#",
                    "EFSEEE#C#EEESFE#424#EFSEEE#C#EEESFE",
                    "#FSSM#####MSSF##323##FSSM#####MSSF#",
                    "##FSRZ#E#ZRSF###323###FSRZ#E#ZRSF##",
                    "##FSPRMEMRPSF###323###FSPRMEMRPSF##",
                    "S##FSSSESSSF##S#323#S##FSSSESSSF##S",
                    "####FFSSSFF#####111#####FFSSSFF####",
                    "######FFF#################FFF######",
                    "#####S#E#S#######S#######S#E#S#####")
            .aisle(
                    "#####SJJJS#######S#######SJJJS#####",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "###YDDNNNDDY####222####YDDNNNDDY###",
                    "S#YUPNNWNNPUY#S#618#S#YUPNNWNNPUY#S",
                    "#JDPPRMWMRPPDJ##618##JDPPRMWMRPPDJ#",
                    "#JDNRZ#W#ZRNDJ##618##JDNRZ#W#ZRNDJ#",
                    "JDNNM##W##MNNDJ#618#JDNNM##W##MNNDJ",
                    "JDNWWWWCWWWWNDJ#111#JDNWWWWCWWWWNDJ",
                    "JDNNM##W##MNNDJ#618#JDNNM##W##MNNDJ",
                    "#JDNRZ#W#ZRNDJ##618##JDNRZ#W#ZRNDJ#",
                    "#JDPPRMWMRPPDJ##618##JDPPRMWMRPPDJ#",
                    "S#YUPNNWNNPUY#S#618#S#YUPNNWNNPUY#S",
                    "###YDDNNNDDY####222####YDDNNNDDY###",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "######JJJ#################JJJ######",
                    "########LVVL##SKZVZKS##LVVL########",
                    "###LVVVVLTTV###ZHTHZ###VTTLVVVVL###",
                    "###VTTTTTTTV###VTLTV###VTTTTTTTV###",
                    "###LVVVVLTTV###ZHTHZ###VTTLVVVVL###",
                    "########LVVL##SKZVZKS##LVVL########",
                    "######JJJ#################JJJ######",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "###YDDNNNDDY####222####YDDNNNDDY###",
                    "S#YUPNNWNNPUY#S#618#S#YUPNNWNNPUY#S",
                    "#JDPPRMWMRPPDJ##618##JDPPRMWMRPPDJ#",
                    "#JDNRZ#W#ZRNDJ##618##JDNRZ#W#ZRNDJ#",
                    "JDNNM##W##MNNDJ#618#JDNNM##W##MNNDJ",
                    "JDNWWWWCWWWWNDJ#111#JDNWWWWCWWWWNDJ",
                    "JDNNM##W##MNNDJ#618#JDNNM##W##MNNDJ",
                    "#JDNRZ#W#ZRNDJ##618##JDNRZ#W#ZRNDJ#",
                    "#JDPPRMWMRPPDJ##618##JDPPRMWMRPPDJ#",
                    "S#YUPNNWNNPUY#S#618#S#YUPNNWNNPUY#S",
                    "###YDDNNNDDY####222####YDDNNNDDY###",
                    "####JJDDDJJ#############JJDDDJJ####",
                    "#####SJJJS#######S#######SJJJS#####")
            .aisle(
                    "#####S###S#######S#######S###S#####",
                    "######FFF#################FFF######",
                    "####FFSSSFF#############FFSSSFF####",
                    "SEEFSSSESSSFEES#H#H#SEEFSSSESSSFEES",
                    "E#FSPRMEMRPSF###H#H###FSPRMEMRPSF#E",
                    "E#FSRZ#E#ZRSF###H#H###FSRZ#E#ZRSF#E",
                    "EFSSM#####MSSF##H#H##FSSM#####MSSFE",
                    "EFSEEE#C#EEESF##H#H##FSEEE#C#EEESFE",
                    "EFSSM#####MSSF##H#H##FSSM#####MSSFE",
                    "E#FSRZ#E#ZRSF###H#H###FSRZ#E#ZRSF#E",
                    "E#FSPRMEMRPSF###H#H###FSPRMEMRPSF#E",
                    "SEEFSSSESSSFEES#H#H#SEEFSSSESSSFEES",
                    "E###FFSSSFF###E#H#H#E###FFSSSFF###E",
                    "E#####FFF#####E#H#H#E#####FFF#####E",
                    "E#############E#H#H#E#############E",
                    "E#######LVVL##EEHVHEE##LVVL#######E",
                    "E##LVVVVLTTV###LHTHL###VTTLVVVVL##E",
                    "E##VTTTTTTTV###VTLTV###VTTTTTTTV##E",
                    "E##LVVVVLTTV###LHTHL###VTTLVVVVL##E",
                    "E#######LVVL##EEHVHEE##LVVL#######E",
                    "E#############E#H#H#E#############E",
                    "E#####FFF#####E#H#H#E#####FFF#####E",
                    "E###FFSSSFF###E#H#H#E###FFSSSFF###E",
                    "SEEFSSSESSSFEES#H#H#SEEFSSSESSSFEES",
                    "E#FSPRMEMRPSF###H#H###FSPRMEMRPSF#E",
                    "E#FSRZ#E#ZRSF###H#H###FSRZ#E#ZRSF#E",
                    "EFSSM#####MSSF##H#H##FSSM#####MSSFE",
                    "EFSEEE#C#EEESF##H#H##FSEEE#C#EEESFE",
                    "EFSSM#####MSSF##H#H##FSSM#####MSSFE",
                    "E#FSRZ#E#ZRSF###H#H###FSRZ#E#ZRSF#E",
                    "E#FSPRMEMRPSF###H#H###FSPRMEMRPSF#E",
                    "SEEFSSSESSSFEES#H#H#SEEFSSSESSSFEES",
                    "####FFSSSFF#############FFSSSFF####",
                    "######FFF#################FFF######",
                    "#####S###S#######S#######S###S#####")
            .aisle(
                    "#####SEEESEEEEEEESEEEEEEESEEES#####",
                    "#####E###E###############E###E#####",
                    "#####EAAAE###############EAAAE#####",
                    "S###AAMAMAA###SEEEEES###AAMAMAA###S",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "##AMAA###AAMA#########AMAA###AAMA##",
                    "##AA###CW##AA#########AA##WC###AA##",
                    "##AMAA###AAMA#########AMAA###AAMA##",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "S###AAMAMAA###SEEEEES###AAMAMAA###S",
                    "######AAA#################AAA######",
                    "###################################",
                    "###################################",
                    "########LVVL###LLLLL###LVVL########",
                    "###LVVVVLTTV###LLLLL###VTTLVVVVL###",
                    "###VTTTTTTTV###LLLLL###VTTTTTTTV###",
                    "###LVVVVLTTV###LLLLL###VTTLVVVVL###",
                    "########LVVL###LLLLL###LVVL########",
                    "###################################",
                    "###################################",
                    "######AAA#################AAA######",
                    "S###AAMAMAA###SEEEEES###AAMAMAA###S",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "##AMAA###AAMA#########AMAA###AAMA##",
                    "##AA###CW##AA#########AA##WC###AA##",
                    "##AMAA###AAMA#########AMAA###AAMA##",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "###ATTA#ATTA###########ATTA#ATTA###",
                    "S###AAMAMAA###SEEEEES###AAMAMAA###S",
                    "#####EAAAE###############EAAAE#####",
                    "#####E###E###############E###E#####",
                    "#####SEEESEEEEEEESEEEEEEESEEES#####")
            .aisle(
                    "#####S###S#######S#######S###S#####",
                    "######M#M#################M#M######",
                    "######M#M#################M#M######",
                    "S###LL###LL###S#####S###LL###LL###S",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "#MM#LL###LL#MM#######MM#LL###LL#MM#",
                    "#######CWWWWWWWWWWWWWWWWWWWC#######",
                    "#MM#LL###LL#MM###W###MM#LL###LL#MM#",
                    "###LTTL#LTTL#####W#####LTTL#LTTL###",
                    "###LTTL#LTTL#####W#####LTTL#LTTL###",
                    "S###LL###LL###S##W##S###LL###LL###S",
                    "######M#M########W########M#M######",
                    "######M#M########W########M#M######",
                    "#################W#################",
                    "########LLLL#####W#####LLLL########",
                    "###LLLLLLTTL###S#W#S###LTTLLLLLL###",
                    "###LLLLLLLLL#####W#####LLLLLLLLL###",
                    "###LLLLLLTTL###S#W#S###LTTLLLLLL###",
                    "########LLLL#####W#####LLLL########",
                    "#################W#################",
                    "######M#M########W########M#M######",
                    "######M#M########W########M#M######",
                    "S###LL###LL###S##W##S###LL###LL###S",
                    "###LTTL#LTTL#####W#####LTTL#LTTL###",
                    "###LTTL#LTTL#####W#####LTTL#LTTL###",
                    "#MM#LL###LL#MM###W###MM#LL###LL#MM#",
                    "#######CWWWWWWWWWWWWWWWWWWWC#######",
                    "#MM#LL###LL#MM#######MM#LL###LL#MM#",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "###LTTL#LTTL###########LTTL#LTTL###",
                    "S###LL###LL###S#####S###LL###LL###S",
                    "######M#M#################M#M######",
                    "######M#M#################M#M######",
                    "#####S###S#######S#######S###S#####")
            .aisle(
                    "#####S###S#######S#######S###S#####",
                    "###################################",
                    "###################################",
                    "S#############S#####S#############S",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "###################################",
                    "#######C###################C#######",
                    "###################################",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "S#############S#####S#############S",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "#########TT####SAAAS####TT#########",
                    "#################W#################",
                    "#########TT####SAAAS####TT#########",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "S#############S#####S#############S",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "###################################",
                    "#######C###################C#######",
                    "###################################",
                    "####TT###TT#############TT###TT####",
                    "####TT###TT#############TT###TT####",
                    "S#############S#####S#############S",
                    "###################################",
                    "###################################",
                    "#####S###S#######S#######S###S#####")
            .aisle(
                    "#####S###S#######S#######S###S#####",
                    "###################################",
                    "##############E#####E##############",
                    "S###KK###KKEEES#####SEEEKK###KK###S",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "####KK###KK###E#####E###KK###KK####",
                    "#######C######E#####E######C#######",
                    "####KK###KK###E#####E###KK###KK####",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "S###KK###KKEEES#####SEEEKK###KK###S",
                    "##############E#####E##############",
                    "###################################",
                    "###################################",
                    "#########KK#############KK#########",
                    "########KTTK###IAAAO###KTTK########",
                    "########KKKK###IWWWO###KKKK########",
                    "########KTTK###IAXAO###KTTK########",
                    "#########KK#############KK#########",
                    "###################################",
                    "###################################",
                    "##############E#####E##############",
                    "S###KK###KKEEES#####SEEEKK###KK###S",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "####KK###KK###E#####E###KK###KK####",
                    "#######C######E#####E######C#######",
                    "####KK###KK###E#####E###KK###KK####",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "###KTTK#KTTK##E#####E##KTTK#KTTK###",
                    "S###KK###KKEEES#####SEEEKK###KK###S",
                    "##############E#####E##############",
                    "###################################",
                    "#####S###S#######S#######S###S#####")
            .aisle(
                    "#####S###S######ESE######S###S#####",
                    "###################################",
                    "###################################",
                    "S##KZZK#KZZK###########KZZK#KZZK##S",
                    "###ZTTTTTTTZ###########ZTTTTTTTZ###",
                    "###ZTTTTTTTZ###########ZTTTTTTTZ###",
                    "###KZZK#KTTK###########KTTK#KZZK###",
                    "#######C#TT#############TT#C#######",
                    "###KZZK#KTTK###########KTTK#KZZK###",
                    "###ZTTTTTTTZ###########ZTTTTTTTZ###",
                    "###ZTTTTTTTZ###########ZTTTTTTTZ###",
                    "S##KZZK#KTTK###########KTTK#KZZK##S",
                    "#########TT#############TT#########",
                    "#########TT#############TT#########",
                    "#########TT#############TT#########",
                    "########KTTK###########KTTK########",
                    "########ZTTZ###OAAAI###ZTTZ########",
                    "########ZTTZ###OAAAI###ZTTZ########",
                    "########ZTTZ###OAAAI###ZTTZ########",
                    "########KTTK###########KTTK########",
                    "#########TT#############TT#########",
                    "#########TT#############TT#########",
                    "#########TT#############TT#########",
                    "S##KZZK#KTTK###########KTTK#KZZK##S",
                    "###ZTTTTTTTZ###########ZTTTTTTTZ###",
                    "###ZTTTTTTTZ###########ZTTTTTTTZ###",
                    "###KZZK#KTTK###########KTTK#KZZK###",
                    "#######C#TT#############TT#C#######",
                    "###KZZK#KTTK###########KTTK#KZZK###",
                    "###ZTTTTTTTZ###########ZTTTTTTTZ###",
                    "###ZTTTTTTTZ###########ZTTTTTTTZ###",
                    "S##KZZK#KZZK###########KZZK#KZZK##S",
                    "###################################",
                    "###################################",
                    "#####S###S######ESE######S###S#####")
            .aisle(
                    "#####SE#ES###############SE#ES#####",
                    "######E#E#################E#E######",
                    "E#####E#E#################E#E#####E",
                    "SEEEKKE#EKK#############KKE#EKKEEES",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E###KKS#SKK#############KKS#SKK###E",
                    "E######C###################C######E",
                    "E###KKS#SKK#############KKS#SKK###E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "SEEEKK###KK#############KK###KKEEES",
                    "E#################################E",
                    "###################################",
                    "###################################",
                    "#########KK#############KK#########",
                    "########KZZK###########KZZK########",
                    "########KZZK###########KZZK########",
                    "########KZZK###########KZZK########",
                    "#########KK#############KK#########",
                    "###################################",
                    "###################################",
                    "E#################################E",
                    "SEEEKK###KK#############KK###KKEEES",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E###KKS#SKK#############KKS#SKK###E",
                    "E######C###################C######E",
                    "E###KKS#SKK#############KKS#SKK###E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "E##KZZK#KZZK###########KZZK#KZZK##E",
                    "SEEEKKE#EKK#############KKE#EKKEEES",
                    "E#####E#E#################E#E#####E",
                    "######E#E#################E#E######",
                    "#####SE#ES###############SE#ES#####")
            .aisle(
                    "####ESEEESE#############ESEEESE####",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "######S#S#################S#S######",
                    "#######E###################E#######",
                    "######S#S#################S#S######",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "######S#S#################S#S######",
                    "#######E###################E#######",
                    "######S#S#################S#S######",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "###################################",
                    "####ESEEESE#############ESEEESE####");
}
