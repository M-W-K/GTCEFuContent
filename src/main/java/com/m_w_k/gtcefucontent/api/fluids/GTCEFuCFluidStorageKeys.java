package com.m_w_k.gtcefucontent.api.fluids;

import static gregtech.api.util.GTUtility.gregtechId;

import org.jetbrains.annotations.NotNull;

import gregtech.api.fluids.FluidState;
import gregtech.api.fluids.store.FluidStorageKey;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconType;
import gregtech.api.unification.material.properties.FluidProperty;
import gregtech.api.unification.material.properties.PropertyKey;

public final class GTCEFuCFluidStorageKeys {

    public static final FluidStorageKey EUTECTIC = new FluidStorageKey(gregtechId("eutectic"),
            MaterialIconType.liquid,
            m -> prefixedRegistryName("eutectic.", GTCEFuCFluidStorageKeys.EUTECTIC, m),
            m -> m.hasProperty(PropertyKey.DUST) ? "gregtech.fluid.liquid_generic" : "gregtech.fluid.generic",
            FluidState.LIQUID);

    private GTCEFuCFluidStorageKeys() {}

    /**
     * @param prefix   the prefix string for the registry name
     * @param key      the key which does not require the prefix
     * @param material the material to create a registry name for
     * @return the registry name
     */
    private static @NotNull String prefixedRegistryName(@NotNull String prefix, @NotNull FluidStorageKey key,
                                                        @NotNull Material material) {
        FluidProperty property = material.getProperty(PropertyKey.FLUID);
        if (property != null && property.getPrimaryKey() != key) {
            return prefix + material.getName();
        }
        return material.getName();
    }
}
