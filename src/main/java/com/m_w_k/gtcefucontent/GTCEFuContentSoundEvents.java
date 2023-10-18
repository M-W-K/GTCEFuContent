package com.m_w_k.gtcefucontent;

import net.minecraft.util.SoundEvent;

// borrow the GT sound manager; I'm not coding one myself!
import static gregtech.api.GregTechAPI.soundManager;

public class GTCEFuContentSoundEvents {
    public static SoundEvent FORGING_FURNACE;

    public static void register() {
        FORGING_FURNACE = soundManager.registerSound("tick.forging_furnace");
    }
}
