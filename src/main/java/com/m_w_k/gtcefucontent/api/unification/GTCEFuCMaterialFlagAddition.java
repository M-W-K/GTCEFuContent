package com.m_w_k.gtcefucontent.api.unification;

import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.FluidState;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.Elements;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.properties.PropertyKey;

import static gregtech.api.unification.material.Materials.STD_METAL;
import static gregtech.api.unification.material.Materials.Thorium;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_DENSE;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_ROD;
import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static gregtech.api.util.GTUtility.gregtechId;

public final class GTCEFuCMaterialFlagAddition {

    private GTCEFuCMaterialFlagAddition() {}

    public static void init() {
        GCYMMaterials.TantalumCarbide.addFlags(MaterialFlags.GENERATE_FRAME);

        Materials.Invar.addFlags(MaterialFlags.GENERATE_FOIL);
        Materials.Invar.addFlags(MaterialFlags.GENERATE_LONG_ROD);
        Materials.HSSE.addFlags(MaterialFlags.GENERATE_BOLT_SCREW);

        Materials.Thorium.addFlags(MaterialFlags.GENERATE_DENSE);
        Thorium.getProperty(PropertyKey.FLUID).getStorage()
                .enqueueRegistration(FluidStorageKeys.PLASMA, new FluidBuilder().state(FluidState.PLASMA));

        Materials.MagnesiumDiboride.addFlags(MaterialFlags.GENERATE_DOUBLE_PLATE);
        Materials.ManganesePhosphide.addFlags(MaterialFlags.GENERATE_DENSE);
    }
}
