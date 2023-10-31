package com.m_w_k.gtcefucontent.api.render;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import gregicality.multiblocks.GregicalityMultiblocks;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;

@Mod.EventBusSubscriber(modid = GregicalityMultiblocks.MODID, value = Side.CLIENT)
public final class GTCEFuCTextures {

    public static final SimpleOverlayRenderer INDESTRUCTIBLE_CASING;
    public static final SimpleOverlayRenderer INDESTRUCTIBLE_PIPE_CASING;
    public static final SimpleOverlayRenderer PLASMA_PIPE_CASING;
    public static final SimpleOverlayRenderer PRESSURE_CASING;

    private GTCEFuCTextures() {}

    static {
        INDESTRUCTIBLE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/indestructible_casing");
        INDESTRUCTIBLE_PIPE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/indestructible_pipe_casing");
        PLASMA_PIPE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/plasma_pipe_casing");
        PRESSURE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/high_pressure_casing");
    }
}
