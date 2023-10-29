package com.m_w_k.gtcefucontent.common.metatileentities;

import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.gtcefucId;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

import java.util.concurrent.atomic.AtomicInteger;

import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.*;

import gregtech.api.GTValues;

public class GTCEFuCMetaTileEntities {

    public static MetaTileEntityInfinityExtractor INFINITY_EXTRACTOR;
    public static MetaTileEntityPneumaticInfuser PNEUMATIC_INFUSER;
    public static MetaTileEntitySympatheticCombustor SYMPATHETIC_COMBUSTOR;
    public static MetaTileEntityForgingFurnace FORGING_FURNACE;
    public static MetaTileEntityElectrodeSmelter ELECTRODE_SMELTER;
    public static MetaTileEntityStarSiphon STAR_SIPHON;
    public static MetaTileEntityAntimatterCompressor ANTIMATTER_COMPRESSOR;

    public static final MetaTileEntityFusionStack[] FUSION_STACK = new MetaTileEntityFusionStack[3];

    private GTCEFuCMetaTileEntities() {}

    private static final AtomicInteger ID = new AtomicInteger(22500);

    public static void init() {
        INFINITY_EXTRACTOR = registerMetaTileEntity(ID.getAndIncrement(),
                new MetaTileEntityInfinityExtractor(gtcefucId("infinity_extractor")));
        PNEUMATIC_INFUSER = registerMetaTileEntity(ID.getAndIncrement(),
                new MetaTileEntityPneumaticInfuser(gtcefucId("pneumatic_infuser")));
        SYMPATHETIC_COMBUSTOR = registerMetaTileEntity(ID.getAndIncrement(),
                new MetaTileEntitySympatheticCombustor(gtcefucId("sympathetic_combustor")));
        FORGING_FURNACE = registerMetaTileEntity(ID.getAndIncrement(),
                new MetaTileEntityForgingFurnace(gtcefucId("forging_furnace")));
        ELECTRODE_SMELTER = registerMetaTileEntity(ID.getAndIncrement(),
                new MetaTileEntityElectrodeSmelter(gtcefucId("electrode_blast_smelter")));
        FUSION_STACK[0] = registerMetaTileEntity(ID.getAndIncrement(),
                new MetaTileEntityFusionStack(gtcefucId("fusion_stack.stack"), GTValues.UHV));
        FUSION_STACK[1] = registerMetaTileEntity(ID.getAndIncrement(),
                new MetaTileEntityFusionStack(gtcefucId("fusion_stack.array"), GTValues.UEV));
        FUSION_STACK[2] = registerMetaTileEntity(ID.getAndIncrement(),
                new MetaTileEntityFusionStack(gtcefucId("fusion_stack.complex"), GTValues.UIV));
        STAR_SIPHON = registerMetaTileEntity(ID.getAndIncrement(),
                new MetaTileEntityStarSiphon(gtcefucId("star_siphon")));
        ANTIMATTER_COMPRESSOR = registerMetaTileEntity(ID.getAndIncrement(),
                new MetaTileEntityAntimatterCompressor(gtcefucId("antimatter_compressor")));
    }
}
