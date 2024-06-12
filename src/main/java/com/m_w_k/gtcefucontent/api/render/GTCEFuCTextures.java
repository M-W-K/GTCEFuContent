package com.m_w_k.gtcefucontent.api.render;

import java.util.ArrayList;
import java.util.List;

import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import com.m_w_k.gtcefucontent.client.renderer.texture.cube.AxisAlignedCubeRenderer;

import gregicality.multiblocks.GregicalityMultiblocks;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = GregicalityMultiblocks.MODID, value = Side.CLIENT)
public final class GTCEFuCTextures {

    public static SimpleOverlayRenderer INDESTRUCTIBLE_CASING = simple("casings/hardened_casing/indestructible_casing");
    public static SimpleOverlayRenderer INDESTRUCTIBLE_PIPE_CASING = simple("casings/hardened_casing/indestructible_pipe_casing");
    public static SimpleOverlayRenderer PLASMA_PIPE_CASING = simple("casings/hardened_casing/plasma_pipe_casing");
    public static SimpleOverlayRenderer THERMOSTABLE_CERAMIC = simple("casings/standard_casing/thermostable_ceramic");
    public static SimpleOverlayRenderer PRESSURE_CASING = simple("casings/standard_casing/high_pressure_casing");
    public static SimpleOverlayRenderer UNSTABLE_HYPERSTATIC_CASING = simple("casings/standard_casing/unstable_hyperstatic_casing");
    public static SimpleOverlayRenderer HYPERSTATIC_CASING = simple("casings/hardened_casing/hyperstatic_casing");

    public static final List<AxisAlignedCubeRenderer> HEU_COMPONENT_EMPTY_OVERLAYS = new ArrayList<>(5);
    public static final List<AxisAlignedCubeRenderer> HEU_COMPONENT_FULL_OVERLAYS = new ArrayList<>(5);
    public static final List<AxisAlignedCubeRenderer> HEU_COMPONENT_ACTIVE_OVERLAYS = new ArrayList<>(5);

    public static final OrientedOverlayRenderer NAQ_REACTOR_OVERLAY = oriented("generators/naquadah");

    public static final OrientedOverlayRenderer ANTIMATTER_COMPRESSOR_OVERLAY =
            oriented("machines/antimatter_compressor");



    private GTCEFuCTextures() {}

    public static void preInit() {

        String[] heuComponentNames = new String[] { "endpoint_standard", "endpoint_returning",
                "pipe_holder_standard", "pipe_holder_conductive", "pipe_holder_expanded" };

        for (String heuComponentName : heuComponentNames) {
            HEU_COMPONENT_EMPTY_OVERLAYS
                    .add(axisAligned("machines/heu/" + heuComponentName + "/empty"));
            HEU_COMPONENT_FULL_OVERLAYS
                    .add(axisAligned("machines/heu/" + heuComponentName + "/full"));
            HEU_COMPONENT_ACTIVE_OVERLAYS
                    .add(axisAligned("machines/heu/" + heuComponentName + "/active"));
        }
    }

    private static SimpleOverlayRenderer simple(String basePath) {
        return new SimpleOverlayRenderer("gtcefucontent:" + basePath);
    }

    private static OrientedOverlayRenderer oriented(String basePath) {
        return new OrientedOverlayRenderer("gtcefucontent:" + basePath);
    }

    private static AxisAlignedCubeRenderer axisAligned(String basePath) {
        return new AxisAlignedCubeRenderer("gtcefucontent:" + basePath);
    }
}
