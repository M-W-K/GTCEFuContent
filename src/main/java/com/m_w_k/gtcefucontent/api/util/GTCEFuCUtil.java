package com.m_w_k.gtcefucontent.api.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import com.m_w_k.gtcefucontent.GTCEFuContent;

import crazypants.enderio.base.init.ModObject;
import gregtech.api.metatileentity.MetaTileEntity;

import java.util.Arrays;
import java.util.OptionalDouble;

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

    /**
     * Returns a BlockPos offset from the controller by the given values
     *
     * @param controller the controller whose position we should reference
     * @param x          Positive is controller left, Negative is controller right
     * @param y          Positive is controller up, Negative is controller down
     * @param z          Positive is controller front, Negative is controller back
     * @return Offset BlockPos
     */
    public static BlockPos bbHelper(MetaTileEntity controller, int x, int y, int z) {
        return controller.getPos()
                .offset(controller.getFrontFacing(), z)
                .offset(controller.getFrontFacing().getOpposite().rotateY(), x)
                .offset(EnumFacing.UP, y);
    }

    public static double geometricMean(double... numbers) {
        OptionalDouble total = Arrays.stream(numbers).reduce((a, b) -> a * b);
        if (total.isPresent()) {
            return Math.pow(total.getAsDouble(), 1D / numbers.length);
        }
        else return 0;
    }
}
