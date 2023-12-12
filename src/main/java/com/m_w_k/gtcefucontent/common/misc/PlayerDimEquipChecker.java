package com.m_w_k.gtcefucontent.common.misc;

import static net.minecraft.inventory.EntityEquipmentSlot.*;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import com.m_w_k.gtcefucontent.api.damagesources.GTCEFuCDamageSources;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;

import gregtech.api.damagesources.DamageSources;
import gregtech.api.unification.material.Materials;
import gregtech.common.items.MetaItems;

public final class PlayerDimEquipChecker {

    private static FluidStack oxyStack;

    public static void init() {
        oxyStack = Materials.Oxygen.getFluid(1);
    }

    public static void checkPlayer(EntityPlayer player) {
        if (player.dimension == 0) return;
        if (player.dimension == 6) miningDimCheck(player);
        else if (player.dimension == -1) netherCheck(player);
        else if (player.dimension == 1) endCheck(player);
    }

    private static void miningDimCheck(EntityPlayer player) {
        // simple gas mask
        if (GTCEFuCMetaItems.SIMPLE_GAS_MASK.isItemEqual(player.getItemStackFromSlot(HEAD)) ||
                sealedHelmetCheck(player)) {
            if (drainOxy(player)) {
                return;
            }
        }
        player.attackEntityFrom(GTCEFuCDamageSources.getSuffocationDamage(), 2.0F);
    }

    private static void netherCheck(EntityPlayer player) {
        // gt armor helmet
        if (sealedHelmetCheck(player)) {
            if (drainOxy(player)) {
                return;
            }
        }
        player.attackEntityFrom(GTCEFuCDamageSources.getToxicAtmoDamage(), 3.0F);
    }

    private static void endCheck(EntityPlayer player) {
        // gt armor helmet
        if (sealedHelmetCheck(player)) {
            if (drainOxy(player)) {
                armorCheck(player);
                return;
            }
        }
        player.attackEntityFrom(GTCEFuCDamageSources.getToxicAtmoDamage(), 2.0F);
        armorCheck(player);
    }

    private static boolean sealedHelmetCheck(EntityPlayer player) {
        ItemStack headItem = player.getItemStackFromSlot(HEAD);
        return GTCEFuCMetaItems.GAS_MASK.isItemEqual(headItem) || MetaItems.NANO_HELMET.isItemEqual(headItem) ||
                MetaItems.QUANTUM_HELMET.isItemEqual(headItem);
    }

    private static void armorCheck(EntityPlayer player) {
        // gt armor everything
        ItemStack headItem = player.getItemStackFromSlot(HEAD);
        ItemStack chestItem = player.getItemStackFromSlot(CHEST);
        ItemStack legItem = player.getItemStackFromSlot(LEGS);
        ItemStack feetItem = player.getItemStackFromSlot(FEET);
        int count = 0;
        if (!MetaItems.QUANTUM_HELMET.isItemEqual(headItem)) count += 5;
        if (!MetaItems.QUANTUM_CHESTPLATE.isItemEqual(chestItem) &&
                !MetaItems.QUANTUM_CHESTPLATE_ADVANCED.isItemEqual(chestItem))
            count += 8;
        if (!MetaItems.QUANTUM_LEGGINGS.isItemEqual(legItem)) count += 7;
        if (!MetaItems.QUANTUM_BOOTS.isItemEqual(feetItem)) count += 4;
        player.attackEntityFrom(DamageSources.getRadioactiveDamage(), count / 24f);
    }

    private static boolean drainOxy(EntityPlayer player) {
        // don't drain if we are in creative
        if (player.isCreative()) return true;
        Optional<IFluidHandlerItem> tank = player.inventory.mainInventory.stream()
                .map(a -> a.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
                .filter(Objects::nonNull)
                .filter(a -> {
                    FluidStack drain = a.drain(oxyStack, false);
                    return drain != null && drain.amount > 0;
                }).findFirst();
        tank.ifPresent(a -> a.drain(oxyStack, true));
        return tank.isPresent();
    }
}
