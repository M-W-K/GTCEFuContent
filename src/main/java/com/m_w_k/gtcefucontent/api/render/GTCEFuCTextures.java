package com.m_w_k.gtcefucontent.api.render;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import gregicality.multiblocks.GregicalityMultiblocks;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = GregicalityMultiblocks.MODID, value = Side.CLIENT)
public final class GTCEFuCTextures {

    public static SimpleOverlayRenderer INDESTRUCTIBLE_CASING;
    public static SimpleOverlayRenderer INDESTRUCTIBLE_PIPE_CASING;
    public static SimpleOverlayRenderer PLASMA_PIPE_CASING;
    public static SimpleOverlayRenderer PRESSURE_CASING;

    public static final List<SimpleOverlayRenderer> HEU_COMPONENT_EMPTY_OVERLAYS = new ArrayList<>(5);
    public static final List<SimpleOverlayRenderer> HEU_COMPONENT_FULL_OVERLAYS = new ArrayList<>(5);
    public static final List<SimpleOverlayRenderer> HEU_COMPONENT_ACTIVE_OVERLAYS = new ArrayList<>(5);

    private GTCEFuCTextures() {}

    public static void preInit(){
        INDESTRUCTIBLE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/indestructible_casing");
        INDESTRUCTIBLE_PIPE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/indestructible_pipe_casing");
        PLASMA_PIPE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/plasma_pipe_casing");
        PRESSURE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/high_pressure_casing");

        String[] heuComponentNames = new String[]{"endpoint_standard", "endpoint_returning",
                "pipe_holder_standard", "pipe_holder_conductive", "pipe_holder_expanded"};

        for (String heuComponentName : heuComponentNames) {
            HEU_COMPONENT_EMPTY_OVERLAYS.add(new SimpleOverlayRenderer("overlay/machine/heu/empty_" + heuComponentName));
            HEU_COMPONENT_FULL_OVERLAYS.add(new SimpleOverlayRenderer("overlay/machine/heu/full_" + heuComponentName));
            HEU_COMPONENT_ACTIVE_OVERLAYS.add(new SimpleOverlayRenderer("overlay/machine/heu/active_" + heuComponentName));
        }
    }
}
