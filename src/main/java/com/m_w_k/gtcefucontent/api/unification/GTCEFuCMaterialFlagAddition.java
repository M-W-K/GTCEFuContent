package com.m_w_k.gtcefucontent.api.unification;

import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlags;

public class GTCEFuCMaterialFlagAddition {

    private GTCEFuCMaterialFlagAddition() {}

    public static void init() {
        GCYMMaterials.TantalumCarbide.addFlags(MaterialFlags.GENERATE_FRAME);

        Materials.Invar.addFlags(MaterialFlags.GENERATE_FOIL);

        Materials.Thorium.addFlags(MaterialFlags.GENERATE_DENSE);

        Materials.MagnesiumDiboride.addFlags(MaterialFlags.GENERATE_DOUBLE_PLATE);
        Materials.ManganesePhosphide.addFlags(MaterialFlags.GENERATE_DENSE);
    }
}
