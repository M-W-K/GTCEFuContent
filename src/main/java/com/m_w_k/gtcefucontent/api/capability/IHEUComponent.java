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

        public int pipesPerSlot() {
            return switch (this) {
                case E_STANDARD -> 32;
                case E_RETURNING -> 40;
                default -> 16;
            };
        }

        public int slotsPerComponent() {
            return switch (this) {
                case H_EXPANDED, H_CONDUCTIVE -> 2;
                default -> 1;
            };
        }
    }

    default void notifyController(MetaTileEntityHeatExchanger controller) {
        controller.addNotifiedHeuComponent(this);
    }
}
