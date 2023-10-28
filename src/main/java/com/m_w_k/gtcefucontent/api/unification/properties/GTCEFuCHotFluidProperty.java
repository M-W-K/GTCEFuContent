package com.m_w_k.gtcefucontent.api.unification.properties;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.Fluid;

import com.google.common.base.Preconditions;

import gregtech.api.unification.material.properties.IMaterialProperty;
import gregtech.api.unification.material.properties.MaterialProperties;
import gregtech.api.unification.material.properties.PropertyKey;

public class GTCEFuCHotFluidProperty implements IMaterialProperty {

    private Fluid fluid;
    private int temperature;

    public GTCEFuCHotFluidProperty(int temperature) {
        this.temperature = temperature;
    }

    @Nonnull
    public Fluid getFluid() {
        return fluid;
    }

    /**
     * internal usage only
     */
    public void setFluid(@Nonnull Fluid materialFluid) {
        Preconditions.checkNotNull(materialFluid);
        this.fluid = materialFluid;
    }

    public void setTemperature(int fluidTemperature) {
        Preconditions.checkArgument(fluidTemperature > 0, "Invalid temperature");
        this.temperature = fluidTemperature;
    }

    public int getTemperature() {
        return temperature;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        properties.ensureSet(PropertyKey.FLUID);
    }
}
