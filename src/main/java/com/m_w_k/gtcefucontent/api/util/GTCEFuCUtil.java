package com.m_w_k.gtcefucontent.api.util;

import net.minecraft.util.ResourceLocation;

import com.m_w_k.gtcefucontent.GTCEFuContent;

import crazypants.enderio.base.init.ModObject;

public final class GTCEFuCUtil {

    private static Boolean stellarAlloyPresent = null;

    public static boolean stellarAlloyCheck() {
        if (stellarAlloyPresent == null) {
            stellarAlloyPresent = ModObject.itemAlloyEndergyIngot.getItem() != null &&
                    ModObject.itemAlloyEndergyBall.getItem() != null &&
                    ModObject.itemAlloyEndergyNugget.getItem() != null;
            if (!stellarAlloyPresent)
                GTCEFuContent.log("Stellar Alloy not found. This is a critical recipe problem.",
                        GTCEFuContent.LogType.ERROR);
        }
        return stellarAlloyPresent;
    }

    public static ResourceLocation gtcefucId(String name) {
        return new ResourceLocation(GTCEFuContent.MODID, name);
    }
}
