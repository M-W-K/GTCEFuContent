package com.m_w_k.gtcefucontent.common.metatileentities;

import static com.m_w_k.gtcefucontent.api.capability.IHEUComponent.HEUComponentType.*;
import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.gtcefucId;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.m_w_k.gtcefucontent.api.capability.IHEUComponent;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.*;

import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.multiblockpart.heu.MetaTileEntityHEUComponent;
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

    public static final Map<IHEUComponent.HEUComponentType, MetaTileEntityHEUComponent> HEU_COMPONENTS = new HashMap<>(5);

    private GTCEFuCMetaTileEntities() {}

    private static final AtomicInteger MULTI_ID = new AtomicInteger(22500);
    private static final AtomicInteger TILE_ID = new AtomicInteger(22530);

    public static void init() {
        INFINITY_EXTRACTOR = registerMetaTileEntity(MULTI_ID.getAndIncrement(),
                new MetaTileEntityInfinityExtractor(gtcefucId("infinity_extractor")));
        PNEUMATIC_INFUSER = registerMetaTileEntity(MULTI_ID.getAndIncrement(),
                new MetaTileEntityPneumaticInfuser(gtcefucId("pneumatic_infuser")));
        SYMPATHETIC_COMBUSTOR = registerMetaTileEntity(MULTI_ID.getAndIncrement(),
                new MetaTileEntitySympatheticCombustor(gtcefucId("sympathetic_combustor")));
        FORGING_FURNACE = registerMetaTileEntity(MULTI_ID.getAndIncrement(),
                new MetaTileEntityForgingFurnace(gtcefucId("forging_furnace")));
        ELECTRODE_SMELTER = registerMetaTileEntity(MULTI_ID.getAndIncrement(),
                new MetaTileEntityElectrodeSmelter(gtcefucId("electrode_blast_smelter")));
        FUSION_STACK[0] = registerMetaTileEntity(MULTI_ID.getAndIncrement(),
                new MetaTileEntityFusionStack(gtcefucId("fusion_stack.stack"), GTValues.UHV));
        FUSION_STACK[1] = registerMetaTileEntity(MULTI_ID.getAndIncrement(),
                new MetaTileEntityFusionStack(gtcefucId("fusion_stack.array"), GTValues.UEV));
        FUSION_STACK[2] = registerMetaTileEntity(MULTI_ID.getAndIncrement(),
                new MetaTileEntityFusionStack(gtcefucId("fusion_stack.complex"), GTValues.UIV));
        STAR_SIPHON = registerMetaTileEntity(MULTI_ID.getAndIncrement(),
                new MetaTileEntityStarSiphon(gtcefucId("star_siphon")));
        ANTIMATTER_COMPRESSOR = registerMetaTileEntity(MULTI_ID.getAndIncrement(),
                new MetaTileEntityAntimatterCompressor(gtcefucId("antimatter_compressor")));

        HEU_COMPONENTS.put(E_STANDARD, registerMetaTileEntity(TILE_ID.getAndIncrement(),
                new MetaTileEntityHEUComponent(gtcefucId("heu_endpoint.standard"), E_STANDARD)));
        HEU_COMPONENTS.put(E_RETURNING, registerMetaTileEntity(TILE_ID.getAndIncrement(),
                new MetaTileEntityHEUComponent(gtcefucId("heu_endpoint.returning"), E_RETURNING)));
        HEU_COMPONENTS.put(H_STANDARD, registerMetaTileEntity(TILE_ID.getAndIncrement(),
                new MetaTileEntityHEUComponent(gtcefucId("heu_pipe_holder.standard"), H_STANDARD)));
        HEU_COMPONENTS.put(H_CONDUCTIVE, registerMetaTileEntity(TILE_ID.getAndIncrement(),
                new MetaTileEntityHEUComponent(gtcefucId("heu_pipe_holder.conductive"), H_CONDUCTIVE)));
        HEU_COMPONENTS.put(H_EXPANDED, registerMetaTileEntity(TILE_ID.getAndIncrement(),
                new MetaTileEntityHEUComponent(gtcefucId("heu_pipe_holder.expanded"), H_EXPANDED)));
    }
}
