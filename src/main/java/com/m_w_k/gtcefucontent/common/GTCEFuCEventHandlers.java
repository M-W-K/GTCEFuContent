package com.m_w_k.gtcefucontent.common;

import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;
import com.m_w_k.gtcefucontent.common.misc.PlayerDimEquipChecker;

import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.unification.material.event.MaterialEvent;

@Mod.EventBusSubscriber(modid = GTCEFuContent.MODID)
public final class GTCEFuCEventHandlers {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerMaterials(MaterialEvent event) {
        GTCEFuCMaterials.register();
    }

    @SubscribeEvent
    public static void dimCheck(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            // check every second
            if (FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter() % 20 == 0) {
                PlayerDimEquipChecker.checkPlayer(event.player);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
        EntityEquipmentSlot slot = event.getSlot();
        if (event.getFrom().isEmpty() || slot == EntityEquipmentSlot.MAINHAND || slot == EntityEquipmentSlot.OFFHAND)
            return;

        ItemStack stack = event.getFrom();
        if (!(stack.getItem() instanceof ArmorMetaItem) || stack.getItem().equals(event.getTo().getItem()))
            return;

        ArmorMetaItem<?>.ArmorMetaValueItem valueItem = ((ArmorMetaItem<?>) stack.getItem()).getItem(stack);
        if (valueItem == null) return;
        if (valueItem.isItemEqual(GTCEFuCMetaItems.GAS_MASK.getStackForm())) {
            event.getEntityLiving().removePotionEffect(MobEffects.NIGHT_VISION);
        }
    }
}
