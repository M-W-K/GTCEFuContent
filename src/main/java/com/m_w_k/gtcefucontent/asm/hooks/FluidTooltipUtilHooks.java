package com.m_w_k.gtcefucontent.asm.hooks;

import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;
import gregtech.api.util.FluidTooltipUtil;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class FluidTooltipUtilHooks {

    public static List<String> fluidTooltipOverride(FluidStack fluid) {
        if (fluid == null) return null;
        List<String> tooltip = FluidTooltipUtil.getFluidTooltip(fluid.getFluid());
        if (fluid.getFluid() instanceof EutecticFluid eutecticFluid) {
            for (int i = 0; i < tooltip.size(); i++) {
                String string = tooltip.get(i);
                if (Objects.equals(string,
                        I18n.format("gregtech.fluid.temperature", eutecticFluid.getTemperature()))) {
                    tooltip.set(i, I18n.format("gregtech.fluid.temperature", eutecticFluid.getTemperature(fluid)));
                }
            }
        }
        return tooltip;
    }
}
