package com.m_w_k.gtcefucontent.api.fluids.fluidType;

import gregtech.api.fluids.fluidType.FluidTypeLiquid;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidTypeHot extends FluidTypeLiquid {
    public FluidTypeHot(@Nonnull String name, @Nullable String prefix, @Nullable String suffix, @Nonnull String localization) {
        super(name, prefix, suffix, localization);
    }

    @Override
    protected void setFluidProperties(@Nonnull Fluid fluid) {
        super.setFluidProperties(fluid);
    }
}
