package com.m_w_k.gtcefucontent.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;

import gregtech.api.unification.material.event.MaterialEvent;

@Mod.EventBusSubscriber(modid = GTCEFuContent.MODID)
public final class GTCEFuCEventHandlers {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerMaterials(MaterialEvent event) {
        GTCEFuCMaterials.register();
    }
}
