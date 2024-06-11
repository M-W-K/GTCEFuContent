package com.m_w_k.gtcefucontent.api.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import com.m_w_k.gtcefucontent.client.renderer.texture.cube.AxisAlignedCubeRenderer;

import gregicality.multiblocks.GregicalityMultiblocks;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = GregicalityMultiblocks.MODID, value = Side.CLIENT)
public final class GTCEFuCTextures {

    public static SimpleOverlayRenderer INDESTRUCTIBLE_CASING;
    public static SimpleOverlayRenderer INDESTRUCTIBLE_PIPE_CASING;
    public static SimpleOverlayRenderer PLASMA_PIPE_CASING;
    public static SimpleOverlayRenderer THERMOSTABLE_CERAMIC;
    public static SimpleOverlayRenderer PRESSURE_CASING;

    public static final List<AxisAlignedCubeRenderer> HEU_COMPONENT_EMPTY_OVERLAYS = new ArrayList<>(5);
    public static final List<AxisAlignedCubeRenderer> HEU_COMPONENT_FULL_OVERLAYS = new ArrayList<>(5);
    public static final List<AxisAlignedCubeRenderer> HEU_COMPONENT_ACTIVE_OVERLAYS = new ArrayList<>(5);

    private GTCEFuCTextures() {}

    public static void preInit() {
        INDESTRUCTIBLE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/indestructible_casing");
        INDESTRUCTIBLE_PIPE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/indestructible_pipe_casing");
        PLASMA_PIPE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/plasma_pipe_casing");
        THERMOSTABLE_CERAMIC = new SimpleOverlayRenderer("casings/standard_casing/thermostable_ceramic");
        PRESSURE_CASING = new SimpleOverlayRenderer("casings/standard_casing/high_pressure_casing");

        String[] heuComponentNames = new String[] { "endpoint_standard", "endpoint_returning",
                "pipe_holder_standard", "pipe_holder_conductive", "pipe_holder_expanded" };

        for (String heuComponentName : heuComponentNames) {
            HEU_COMPONENT_EMPTY_OVERLAYS
                    .add(new AxisAlignedCubeRenderer("overlay/machine/heu/" + heuComponentName + "/empty"));
            HEU_COMPONENT_FULL_OVERLAYS
                    .add(new AxisAlignedCubeRenderer("overlay/machine/heu/" + heuComponentName + "/full"));
            HEU_COMPONENT_ACTIVE_OVERLAYS
                    .add(new AxisAlignedCubeRenderer("overlay/machine/heu/" + heuComponentName + "/active"));
        }
    }
}
