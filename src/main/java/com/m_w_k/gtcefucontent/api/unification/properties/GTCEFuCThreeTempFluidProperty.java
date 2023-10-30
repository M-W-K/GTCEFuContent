package com.m_w_k.gtcefucontent.api.unification.properties;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.Fluid;

import com.google.common.base.Preconditions;

import gregtech.api.unification.material.properties.IMaterialProperty;
import gregtech.api.unification.material.properties.MaterialProperties;
import gregtech.api.unification.material.properties.PropertyKey;
import net.minecraftforge.fluids.FluidStack;

public class GTCEFuCThreeTempFluidProperty implements IMaterialProperty {

    private Fluid fluidCold;
    private Fluid fluidHot;
    private int temperatureCold;
    private int temperatureHot;
    private final int thermalCapacity;

    public GTCEFuCThreeTempFluidProperty(int temperatureCold, int temperatureHot, int thermalCapacity) {
        this.temperatureCold = temperatureCold;
        this.temperatureHot = temperatureHot;
        this.thermalCapacity = thermalCapacity;
    }

    public Fluid getFluidCold() {
        return fluidCold;
    }

    public FluidStack getFluidCold(int amount) {
        return new FluidStack(fluidCold, amount);
    }

    public Fluid getFluidHot() {
        return fluidHot;
    }

    public FluidStack getFluidHot(int amount) {
        return new FluidStack(fluidHot, amount);
    }

    /**
     * internal usage only
     */
    public void setFluidCold(Fluid materialFluid) {
        this.fluidCold = materialFluid;
    }

    /**
     * internal usage only
     */
    public void setFluidHot(Fluid materialFluid) {
        this.fluidHot = materialFluid;
    }

    public void setTemperatureCold(int fluidTemperature) {
        Preconditions.checkArgument(fluidTemperature > 0, "Invalid temperature");
        this.temperatureCold = fluidTemperature;
    }

    public void setTemperatureHot(int fluidTemperature) {
        Preconditions.checkArgument(fluidTemperature > 0, "Invalid temperature");
        this.temperatureHot = fluidTemperature;
    }

    public int getTemperatureCold() {
        return temperatureCold;
    }

    public int getTemperatureHot() {
        return temperatureHot;
    }

    public int getThermalCapacity() {
        return thermalCapacity;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        properties.ensureSet(PropertyKey.FLUID);
    }
}
