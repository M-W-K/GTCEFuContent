package com.m_w_k.gtcefucontent.api.fluids;

import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;

import gregicality.multiblocks.api.unification.material.GCYMMaterialIconTypes;
import gregtech.api.fluids.store.FluidStorageKey;
import gregtech.api.unification.material.info.MaterialIconType;

@SuppressWarnings("CodeBlock2Expr")
final public class GTCEFuCFluidStorageKeys {

    public static final FluidStorageKey COLD;
    public static final FluidStorageKey HOT;

    private GTCEFuCFluidStorageKeys() {}

    static {
        COLD = new FluidStorageKey(GTCEFuCUtil.gtcefucId("cold"), new MaterialIconType("frozen"), (s) -> {
            return "cold." + s;
        }, (m) -> {
            return "gtcefucontent.fluid.cold";
        });
        HOT = new FluidStorageKey(GTCEFuCUtil.gtcefucId("hot"), GCYMMaterialIconTypes.molten, (s) -> {
            return "hot." + s;
        }, (m) -> {
            return "gtcefucontent.fluid.hot";
        });
    }
}
