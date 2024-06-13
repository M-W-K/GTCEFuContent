package com.m_w_k.gtcefucontent.api.fluids.void_starlight;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityVoidStarlight extends TileEntity {

    @SideOnly(Side.CLIENT)
    public boolean shouldRenderFace(EnumFacing p_184313_1_) {
        return true;
        // return this.getBlockType().getDefaultState().shouldSideBeRendered(this.world, this.getPos(), p_184313_1_);
    }
}
