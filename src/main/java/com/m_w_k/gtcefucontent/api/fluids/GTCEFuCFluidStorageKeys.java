package com.m_w_k.gtcefucontent.api.fluids;

import static gregtech.api.util.GTUtility.gregtechId;

import java.util.function.UnaryOperator;

import gregtech.api.fluids.FluidState;
import gregtech.api.fluids.store.FluidStorageKey;
import gregtech.api.unification.material.info.MaterialIconType;
import gregtech.api.unification.material.properties.PropertyKey;

public class GTCEFuCFluidStorageKeys {

    public static final FluidStorageKey EUTECTIC = new FluidStorageKey(gregtechId("eutectic"),
            MaterialIconType.liquid,
            UnaryOperator.identity(),
            m -> m.hasProperty(PropertyKey.DUST) ? "gregtech.fluid.liquid_generic" : "gregtech.fluid.generic",
            FluidState.LIQUID);
}
