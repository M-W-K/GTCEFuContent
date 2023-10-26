package com.m_w_k.gtcefucontent.api.render;

import gregicality.multiblocks.GregicalityMultiblocks;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = GregicalityMultiblocks.MODID, value = Side.CLIENT)
public final class GTCEFuCTextures {
    public static SimpleOverlayRenderer INDESTRUCTIBLE_CASING;
    public static SimpleOverlayRenderer INDESTRUCTIBLE_PIPE_CASING;
    public static SimpleOverlayRenderer PLASMA_PIPE_CASING;
    public static SimpleOverlayRenderer PRESSURE_CASING;

    private GTCEFuCTextures() {}

    public static void preInit() {
        INDESTRUCTIBLE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/indestructible_casing");
        INDESTRUCTIBLE_PIPE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/indestructible_pipe_casing");
        PLASMA_PIPE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/plasma_pipe_casing");
        PRESSURE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/high_pressure_casing");
    }
}
