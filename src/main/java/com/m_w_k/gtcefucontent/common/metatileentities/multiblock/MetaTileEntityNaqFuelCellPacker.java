package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.api.render.GTCEFuCTextures;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;

import codechicken.lib.vec.Vector3;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.RelativeDirection;
import gregtech.client.particle.GTLaserBeamParticle;
import gregtech.client.particle.GTParticleManager;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.*;

public class MetaTileEntityNaqFuelCellPacker extends RecipeMapMultiblockController {

    private static final ResourceLocation LASER_LOCATION = GTUtility.gregtechId("textures/fx/laser/laser.png");
    private static final ResourceLocation LASER_HEAD_LOCATION = GTUtility
            .gregtechId("textures/fx/laser/laser_start.png");

    @SideOnly(Side.CLIENT)
    protected GTLaserBeamParticle @Nullable [] beam;

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
                .aisle("#######", "#CCCCC#", "###C###", "### ###", "###C###", "#CCCCC#", "#######")
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
        });
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("gtcefucontent.machine.naq_fuel_cell_packer.warn"));
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTCEFuCTextures.HYPERSTATIC_CASING;
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return GTCEFuCTextures.NAQ_FUEL_CELL_PACKER_OVERLAY;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityNaqFuelCellPacker(this.metaTileEntityId);
    }

    @Override
    public void update() {
        super.update();
        if (this.getWorld().isRemote) handleLaser();
    }

    @SideOnly(Side.CLIENT)
    protected void handleLaser() {
        if (this.beam == null) this.beam = new GTLaserBeamParticle[2];
        if (this.isActive() && this.beam[0] == null) {
            createLaser();
        } else if (!this.isActive() && this.beam[0] != null) {
            beam[0].setExpired();
            beam[1].setExpired();
            beam[0] = null;
            beam[1] = null;
        }
    }

    @SideOnly(Side.CLIENT)
    protected void createLaser() {
        if (this.beam == null) this.beam = new GTLaserBeamParticle[2];
        Vector3 laserPos = getOffsetToCenter().add(getPos());
        beam[0] = createLaserParticle(laserPos.copy().subtract(0, 0.6, 0), laserPos.copy().add(0, 0.05, 0), false);
        beam[1] = createLaserParticle(laserPos.copy().subtract(0, 0.05, 0), laserPos.copy().add(0, 0.6, 0), true);
        GTParticleManager.INSTANCE.addEffect(beam[0]);
        GTParticleManager.INSTANCE.addEffect(beam[1]);
    }

    @SideOnly(Side.CLIENT)
    protected GTLaserBeamParticle createLaserParticle(Vector3 bottom, Vector3 top, boolean reverse) {
        return new GTLaserBeamParticle(this, bottom, top)
                .setBody(LASER_LOCATION)
                .setBeamHeight(0.25f)
                .setDoubleVertical(true)
                .setHead(LASER_HEAD_LOCATION)
                .setHeadWidth(0.2f)
                .setEmit(reverse ? -0.2f : 0.2f);
    }

    protected void doFailureExplosion() {
        Vector3 offset = getOffsetToCenter();
        getWorld().createExplosion(null,
                getPos().getX() + offset.x,
                getPos().getY() + offset.y,
                getPos().getZ() + offset.z,
                60, true);
    }

    protected Vector3 getOffsetToCenter() {
        EnumFacing offsetDirection = RelativeDirection.BACK.getRelativeFacing(this.getFrontFacing(),
                this.getUpwardsFacing(), this.isFlipped());
        Vector3 vec = new Vector3(offsetDirection.getXOffset(), offsetDirection.getYOffset(),
                offsetDirection.getZOffset());
        vec.multiply(6);
        vec.add(0.5, 0.5, 0.5);
        return vec;
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
