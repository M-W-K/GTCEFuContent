package com.m_w_k.gtcefucontent.api.render;

import gregicality.multiblocks.GregicalityMultiblocks;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = GregicalityMultiblocks.MODID, value = Side.CLIENT)
public final class GTCEFuCTextures {
    public static SimpleOverlayRenderer INDESTRUCTIBLE_CASING;

    private GTCEFuCTextures() {}

    public static void preInit() {
        INDESTRUCTIBLE_CASING = new SimpleOverlayRenderer("casings/hardened_casing/indestructible_casing");
    }
}
