package com.m_w_k.gtcefucontent;

import net.minecraft.util.SoundEvent;

import static gregtech.api.GregTechAPI.soundManager;

public class GTCEFuContentSoundEvents {
    public static SoundEvent FORGING_FURNACE;

    public static void register() {
        // Get the greg sound manager to register the sound as ours. How imperious.
        FORGING_FURNACE = soundManager.registerSound(GTCEFuContent.MODID, "tick.forging_furnace");
    }
}
