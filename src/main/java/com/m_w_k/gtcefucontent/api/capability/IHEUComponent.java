package com.m_w_k.gtcefucontent.api.capability;

import gregtech.api.unification.material.Material;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public interface IHEUComponent extends IItemHandlerModifiable {
    boolean hasValidPiping();
    @Nullable
    Material getPipeMaterial();
    HEUComponentType getComponentType();

    enum HEUComponentType {
        E_STANDARD, E_RETURNING, H_STANDARD, H_CONDUCTIVE, H_EXPANDED;

        public boolean isEndpoint() {
            return this == E_STANDARD || this == E_RETURNING;
        }

        public boolean isLargeInventory() {
            return this == H_CONDUCTIVE || this == H_EXPANDED;
        }
    }
}
