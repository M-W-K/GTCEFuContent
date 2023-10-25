package com.m_w_k.gtcefucontent.common;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import gregtech.api.unification.material.event.MaterialEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = GTCEFuContent.MODID)
public class GTCEFuCEventHandlers {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerMaterials(MaterialEvent event) {
        GTCEFuCMaterials.register();
    }
}
