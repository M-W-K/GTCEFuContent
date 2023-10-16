package com.m_w_k.gtcefucontent.common.metatileentities;


import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.*;
import net.minecraft.util.ResourceLocation;

public class GTCEFuCMetaTileEntities {

    public static MetaTileEntityInfinityExtractor INFINITY_EXTRACTOR;

    private GTCEFuCMetaTileEntities() {}

    public static void init() {
        INFINITY_EXTRACTOR = registerMetaTileEntity(2100, new MetaTileEntityInfinityExtractor(gtcefucId("infinity_extractor")));
    }

    private static ResourceLocation gtcefucId(String name) {
        return new ResourceLocation(GTCEFuContent.MODID, name);
    }
}
