package com.m_w_k.gtcefucontent.api.metatileentity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.capability.IFluidHandler;

import gregtech.api.capability.IMultipleTankHandler;

public interface IHeatExchanger {

    default double getMaintenanceDurationMultiplier() {
        return 1;
    }

    IMultipleTankHandler getInputFluidInventory();

    IMultipleTankHandler getOutputFluidInventory();

    List<IFluidHandler> getNotifiedFluidInputList();

    List<IFluidHandler> getNotifiedFluidOutputList();

    int getHEUCount();

    double getSpeedBonus();

    @Nullable
    default Integer getTargetEutecticTemperature() {
        return null;
    }
}
