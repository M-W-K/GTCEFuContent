package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.api.render.GTCEFuCTextures;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class MetaTileEntityNaqFuelCellPacker extends RecipeMapMultiblockController {

    public MetaTileEntityNaqFuelCellPacker(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTCEFuCRecipeMaps.NAQ_FUEL_CELL_PACKER_RECIPES);
        this.recipeMapWorkable = new NaqFuelCellPackerRecipeLogic(this);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        TraceabilityPredicate casing = getCasingState(0).setMinGlobalLimited(150);
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT)
                .aisle("#######", "#AAAAA#", "#AAAAA#", "#AAXAA#", "#AAAAA#", "#AAAAA#", "#######")
                .aisle("#CCCCC#", "C     C", "C     C", "C     C", "C     C", "C     C", "#CCCCC#")
                .aisle("#######", "#CCCCC#", "#CCCCC#", "#CCCCC#", "#CCCCC#", "#CCCCC#", "#######")
                .aisle("#######", "#C###C#", "#######", "#######", "#######", "#C###C#", "#######")
                .aisle("#######", "#C###C#", "#######", "#######", "#######", "#C###C#", "#######")
                .aisle("#######", "#C###C#", "#######", "#######", "#######", "#C###C#", "#######")
                .aisle("#######", "#CCCCC#", "###C###", "#######", "###C###", "#CCCCC#", "#######")
                .aisle("#######", "#C###C#", "#######", "#######", "#######", "#C###C#", "#######")
                .aisle("#######", "#C###C#", "#######", "#######", "#######", "#C###C#", "#######")
                .aisle("#######", "#C###C#", "#######", "#######", "#######", "#C###C#", "#######")
                .aisle("#######", "#CCCCC#", "#CCCCC#", "#CCCCC#", "#CCCCC#", "#CCCCC#", "#######")
                .aisle("#CCCCC#", "C     C", "C     C", "C     C", "C     C", "C     C", "#CCCCC#")
                .aisle("#######", "#AAAAA#", "#AAAAA#", "#AAAAA#", "#AAAAA#", "#AAAAA#", "#######")
                .where('A', casing
                        .or(autoAbilities(true, true, true, true, true, true, false)))
                .where('C', casing)
                .where('X', selfPredicate())
                .where(' ', air())
                .where('#', any())
                .build();
    }

    @Nonnull
    protected static TraceabilityPredicate getCasingState(int id) {
        return states(switch (id) {
            default -> GTCEFuCMetaBlocks.HARDENED_CASING
                    .getState(GTCEFuCBlockHardenedCasing.CasingType.HYPERSTATIC_CASING);
            case 1 -> MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.GRATE_CASING);
            case 2 -> MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE);
            case 5 -> MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX);
            case 6 -> MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
        });
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("gtcefucontent.machine.naq_fuel_cell_packer.warn"));
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTCEFuCTextures.HYPERSTATIC_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityNaqFuelCellPacker(this.metaTileEntityId);
    }

    protected void doFailureExplosion() {
        EnumFacing offsetDirection = RelativeDirection.BACK.getRelativeFacing(this.getFrontFacing(),
                this.getUpwardsFacing(), this.isFlipped());
        getWorld().createExplosion(null,
                getPos().getX() + 0.5 + offsetDirection.getXOffset() * this.getOffsetToCenter(),
                getPos().getY() + 0.5 + offsetDirection.getYOffset() * this.getOffsetToCenter(),
                getPos().getZ() + 0.5 + offsetDirection.getZOffset() * this.getOffsetToCenter(),
                60, true);
    }

    protected int getOffsetToCenter() {
        return 6;
    }

    protected class NaqFuelCellPackerRecipeLogic extends MultiblockRecipeLogic {

        int explosionPoint = Integer.MAX_VALUE;

        public NaqFuelCellPackerRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        protected void updateRecipeProgress() {
            super.updateRecipeProgress();
            if (this.progressTime > this.explosionPoint) {
                completeRecipe();
                doFailureExplosion();
            }
        }

        @Override
        protected void completeRecipe() {
            this.explosionPoint = Integer.MAX_VALUE;
            super.completeRecipe();
        }

        @Override
        protected void setupRecipe(Recipe recipe) {
            super.setupRecipe(recipe);
            if (this.itemOutputs.isEmpty()) {
                // explosion occurs anywhere from 25% progress to 95%
                this.explosionPoint = (int) ((0.7 * Math.random() + 0.25) * this.maxProgressTime);
            } else this.explosionPoint = Integer.MAX_VALUE;
        }

        @Override
        public @NotNull NBTTagCompound serializeNBT() {
            NBTTagCompound tag = super.serializeNBT();
            tag.setInteger("ExplosionPoint", this.explosionPoint);
            return tag;
        }

        @Override
        public void deserializeNBT(@NotNull NBTTagCompound compound) {
            super.deserializeNBT(compound);
            this.explosionPoint = compound.getInteger("ExplosionPoint");
            if (this.explosionPoint == 0) this.explosionPoint = Integer.MAX_VALUE;
        }
    }
}
