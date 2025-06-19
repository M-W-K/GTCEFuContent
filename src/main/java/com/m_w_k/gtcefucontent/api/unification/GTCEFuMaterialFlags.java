package com.m_w_k.gtcefucontent.api.unification;

import gregtech.api.unification.material.info.MaterialFlag;

public final class GTCEFuMaterialFlags {

    /**
     * Use to disable forging recipes from generating where this material is an input. Not compatible with ore entries.
     */
    public static final MaterialFlag NO_FORGING_RECIPES_IN = new MaterialFlag.Builder("no_forging_recipes_in")
            .build();

    /**
     * Use to disable forging recipes from forging this material as an output.
     */
    public static final MaterialFlag NO_FORGING_OUT = new MaterialFlag.Builder("no_forging_out")
            .build();

    private GTCEFuMaterialFlags() {}
}
