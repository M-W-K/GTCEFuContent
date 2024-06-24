package com.m_w_k.gtcefucontent.mixins.gregtech;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.m_w_k.gtcefucontent.api.damagesources.GTCEFuCDamageSources;

import gregtech.common.items.armor.QuarkTechSuite;

@Mixin(QuarkTechSuite.class)
public class QuarkTechSuiteMixin {

    @Inject(method = "handleUnblockableDamage", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void extendedHandleUnblockableDamageCheck(EntityLivingBase entity, ItemStack armor, DamageSource source,
                                                      double damage, EntityEquipmentSlot equipmentSlot,
                                                      CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && GTCEFuCDamageSources.isQTUnblockable(source)) cir.setReturnValue(false);
    }
}
