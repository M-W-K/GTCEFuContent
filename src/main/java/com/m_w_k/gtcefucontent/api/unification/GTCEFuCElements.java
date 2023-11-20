package com.m_w_k.gtcefucontent.api.unification;

import crafttweaker.annotations.ZenRegister;
import gregtech.api.unification.Element;
import gregtech.api.unification.Elements;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("mods.gtcefucontent.material.Elements")
@ZenRegister
public class GTCEFuCElements {
    private GTCEFuCElements() {}

    public static final Element Ke1 = Elements.add(125, 196, -1, null, "TriniumReduced", "Ke-", true);

}
