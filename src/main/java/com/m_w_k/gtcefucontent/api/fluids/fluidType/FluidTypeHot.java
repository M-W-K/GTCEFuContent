package com.m_w_k.gtcefucontent.api.fluids.fluidType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fluids.Fluid;

import gregtech.api.fluids.fluidType.FluidTypeLiquid;

public class FluidTypeHot extends FluidTypeLiquid {

    public FluidTypeHot(@Nonnull String name, @Nullable String prefix, @Nullable String suffix,
                        @Nonnull String localization) {
        super(name, prefix, suffix, localization);
    }

    @Override
    protected void setFluidProperties(@Nonnull Fluid fluid) {
        super.setFluidProperties(fluid);
    }
}
