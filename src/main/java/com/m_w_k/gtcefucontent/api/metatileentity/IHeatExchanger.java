package com.m_w_k.gtcefucontent.api.metatileentity;

import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.FluidTankList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public interface IHeatExchanger {

    default double getMaintenanceDurationMultiplier() {
        return 1;
    }

    IMultipleTankHandler getInputFluidInventory();
    
    IMultipleTankHandler getOutputFluidInventory();

    List<IFluidHandler> getNotifiedFluidInputList();

    int getHEUCount();

    double getSpeedBonus();
}
