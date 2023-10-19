package com.m_w_k.gtcefucontent.common.metatileentities;


import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.*;
import gregtech.api.GTValues;
import net.minecraft.util.ResourceLocation;

public class GTCEFuCMetaTileEntities {

    public static MetaTileEntityInfinityExtractor INFINITY_EXTRACTOR;
    public static MetaTileEntitySympatheticCombustor SYMPATHETIC_COMBUSTOR;
    public static MetaTileEntityForgingFurnace FORGING_FURNACE;

    public static final MetaTileEntityFusionStack[] FUSION_STACK = new MetaTileEntityFusionStack[3];
    private GTCEFuCMetaTileEntities() {}

    public static void init() {
        INFINITY_EXTRACTOR = registerMetaTileEntity(22500, new MetaTileEntityInfinityExtractor(gtcefucId("infinity_extractor")));
        SYMPATHETIC_COMBUSTOR = registerMetaTileEntity(22501, new MetaTileEntitySympatheticCombustor(gtcefucId("sympathetic_combustor")));
        FORGING_FURNACE = registerMetaTileEntity(22502, new MetaTileEntityForgingFurnace(gtcefucId("forging_furnace")));
        FUSION_STACK[0] = registerMetaTileEntity(22503, new MetaTileEntityFusionStack(gtcefucId("fusion_stack.stack"), GTValues.UHV));
        FUSION_STACK[1] = registerMetaTileEntity(22504, new MetaTileEntityFusionStack(gtcefucId("fusion_stack.array"), GTValues.UEV));
        FUSION_STACK[2] = registerMetaTileEntity(22505, new MetaTileEntityFusionStack(gtcefucId("fusion_stack.complex"), GTValues.UIV));
    }

    private static ResourceLocation gtcefucId(String name) {
        return new ResourceLocation(GTCEFuContent.MODID, name);
    }
}
