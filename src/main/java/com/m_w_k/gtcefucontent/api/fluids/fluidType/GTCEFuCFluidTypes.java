package com.m_w_k.gtcefucontent.api.fluids.fluidType;

import crafttweaker.annotations.ZenRegister;
import gregtech.api.fluids.fluidType.FluidType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenClass("mods.gtcefucontent.material.FluidTypes")
@ZenRegister
public class GTCEFuCFluidTypes {

    @ZenProperty
    public static final FluidType HOT = new FluidTypeHot("hot", null, "hot", "gtcefucontent.fluid.hot");
}
