package com.m_w_k.gtcefucontent.api.unification.properties;

import gregtech.api.unification.material.properties.IMaterialProperty;
import gregtech.api.unification.material.properties.MaterialProperties;
import gregtech.api.unification.material.properties.PropertyKey;


public class GTCEFuCHeatCapacityProperty implements IMaterialProperty {
    private final int thermalCapacityIngot;

    public GTCEFuCHeatCapacityProperty(int thermalCapacity, boolean ingotCapacity) {
        this.thermalCapacityIngot = thermalCapacity * (ingotCapacity ? 1 : 144);
    }

    public int getThermalCapacityFluid() {
        return thermalCapacityIngot / 144;
    }

    public int getThermalCapacityIngot() {
        return thermalCapacityIngot;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {}
}
