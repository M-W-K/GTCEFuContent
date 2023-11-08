package com.m_w_k.gtcefucontent.api.capability;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandlerModifiable;

import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.MetaTileEntityHeatExchanger;

import gregtech.api.unification.material.Material;

public interface IHEUComponent extends IItemHandlerModifiable {

    boolean hasValidPiping();

    /**
     * Internal use only
     */
    BlockPos getPos();

    @Nullable
    Material getPipeMaterial();

    HEUComponentType getComponentType();

    enum HEUComponentType {

        E_STANDARD,
        E_RETURNING,
        H_STANDARD,
        H_CONDUCTIVE,
        H_EXPANDED;

        public boolean isEndpoint() {
            return this == E_STANDARD || this == E_RETURNING;
        }

        public boolean isLargeInventory() {
            return this == H_CONDUCTIVE || this == H_EXPANDED;
        }
    }

    default void notifyController(MetaTileEntityHeatExchanger controller) {
        controller.addNotifiedHeuComponent(this);
    }
}
