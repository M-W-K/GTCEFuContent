package com.m_w_k.gtcefucontent.api.metatileentity;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.recipes.RecipeMap;

public abstract class GTCEFuCMultiShapeMultiblockController extends GCYMRecipeMapMultiblockController {

    public GTCEFuCMultiShapeMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        if (this.getWorld() != null && this.getPos() != null) {
            if (this.getWorld().getTileEntity(this.getPos()) instanceof IGregTechTileEntity gtte) {
                if (gtte.getMetaTileEntity() instanceof GTCEFuCMultiShapeMultiblockController controller) {
                    return getStructurePattern(controller.getRecipeMapIndex());
                }
            }
        }
        return getStructurePattern(0);
    }

    protected abstract @NotNull BlockPattern getStructurePattern(int index);

    @Override
    public void checkStructurePattern() {
        if (!this.isStructureFormed()) {
            this.reinitializeStructurePattern();
        }
        super.checkStructurePattern();
    }

    @Override
    public void setRecipeMapIndex(int index) {
        super.setRecipeMapIndex(index);
        this.reinitializeStructurePattern();
    }
}
