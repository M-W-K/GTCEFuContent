package com.m_w_k.gtcefucontent.asm.hooks;

import java.util.List;
import java.util.Objects;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;

import gregtech.api.util.FluidTooltipUtil;

@SuppressWarnings("unused")
public class FluidTooltipUtilHooks {

    public static List<String> fluidTooltipOverride(FluidStack fluid) {
        if (fluid == null) return null;
        List<String> tooltip = FluidTooltipUtil.getFluidTooltip(fluid.getFluid());
        GTCEFuCUtil.fluidStackTooltipOverride(fluid, tooltip);
        return tooltip;
    }

    public static void checkAndOverride(FluidStack fluid, List<String> tooltip) {
        if (Objects.equals(fluid.getFluid().getUnlocalizedName(), "fluid.void_starlight")) {
            for (int i = 0; i < tooltip.size(); i++) {
                String string = tooltip.get(i);
                if (Objects.equals(string,
                        I18n.format("gregtech.fluid.temperature", Integer.MAX_VALUE))) {
                    tooltip.set(i, I18n.format("gtcefucontent.material.void_starlight.temperature"));
                    tooltip.add(i, I18n.format("gtcefucontent.material.void_starlight.info"));
                }
            }
        }
        if (fluid.getFluid() instanceof EutecticFluid eutecticFluid) {
            for (int i = 0; i < tooltip.size(); i++) {
                if (Objects.equals(tooltip.get(i),
                        I18n.format("gregtech.fluid.temperature", eutecticFluid.getTemperature()))) {
                    tooltip.set(i, I18n.format("gregtech.fluid.temperature", eutecticFluid.getTemperature(fluid)));
                }
            }
        }
    }
}
