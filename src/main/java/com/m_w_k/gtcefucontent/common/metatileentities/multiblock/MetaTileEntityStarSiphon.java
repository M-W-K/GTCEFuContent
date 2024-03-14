package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;
import com.m_w_k.gtcefucontent.api.util.InterpolatingPoint;
import com.m_w_k.gtcefucontent.client.utils.GTCEFuCRotatableCubeRenderHelper;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockAdvancedCasing;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCStarSiphonRecipes;

import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.*;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.IProgressBarMultiblock;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.recipeproperties.FusionEUToStartProperty;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.api.util.interpolate.Eases;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.IRenderSetup;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.shader.postprocessing.BloomEffect;
import gregtech.client.shader.postprocessing.BloomType;
import gregtech.client.utils.*;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockFusionCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class MetaTileEntityStarSiphon extends RecipeMapMultiblockController
                                      implements IFastRenderMetaTileEntity, IBloomEffect, IProgressBarMultiblock {

    protected static final int NO_COLOR = 0;
    protected EnergyContainerList inputEnergyContainers;
    protected long heat = 0;
    private int fusionRingColor = NO_COLOR;
    private int starTier = -1;
    @SideOnly(Side.CLIENT)
    private boolean registeredBloomRenderTicket;

    private final GTCEFuCRotatableCubeRenderHelper cubeRenderHelper1;
    private final GTCEFuCRotatableCubeRenderHelper cubeRenderHelper2;
    private final GTCEFuCRotatableCubeRenderHelper cubeRenderHelper3;
    private final List<InterpolatingPoint> pointList = new ObjectArrayList<>(75);
    private final Random random = new Random();
    private float partialTicksPrev;

    public MetaTileEntityStarSiphon(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTCEFuCRecipeMaps.STAR_SIPHON_RECIPES);
        this.recipeMapWorkable = new StarSiphonRecipeLogic(this);
        this.energyContainer = new EnergyContainerHandler(this, Integer.MAX_VALUE, 0, 0, 0, 0) {

            @Nonnull
            @Override
            public String getName() {
                return GregtechDataCodes.FUSION_REACTOR_ENERGY_CONTAINER_TRAIT;
            }
        };
        this.cubeRenderHelper1 = new GTCEFuCRotatableCubeRenderHelper().setSize(1, 1, 1);
        this.cubeRenderHelper2 = new GTCEFuCRotatableCubeRenderHelper().setSize(0.6, 0.6, 0.6);
        this.cubeRenderHelper3 = new GTCEFuCRotatableCubeRenderHelper().setSize(0.3, 0.3, 0.3);
        for (int i = 0; i < 75; i++) {
            pointList.add(new InterpolatingPoint(InterpolatingPoint.InterpolationMode.ACCELERATING, 100));
        }
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "#######3#######",
                        "######3U3######",
                        "#######3#######",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "#######G#######",
                        "#######G#######",
                        "######UNU######",
                        "####GGNNNGG####",
                        "######UNU######",
                        "#######G#######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "######2N2######",
                        "####2223222####",
                        "###GNN3U3NNG###",
                        "####2223222####",
                        "######2N2######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "#######G#######",
                        "######YCY######",
                        "#######G#######",
                        "#######G#######",
                        "###Y#######Y###",
                        "##GCGG###GGCG##",
                        "###Y#######Y###",
                        "#######G#######",
                        "#######G#######",
                        "######YCY######",
                        "#######G#######",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "##2#########2##",
                        "#GNG#######GNG#",
                        "##2#########2##",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############")
                .aisle(
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "##2#########2##",
                        "#GNG#######GNG#",
                        "##2#########2##",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############")
                .aisle(
                        "#######3#######",
                        "######UNU######",
                        "####2223222####",
                        "###E#######E###",
                        "##2#########2##",
                        "##2#########2##",
                        "#U2#########2U#",
                        "3N3#########3N3",
                        "#U2#########2U#",
                        "##2#########2##",
                        "##2#########2##",
                        "###E#######E###",
                        "####2223222####",
                        "######UNU######",
                        "#######3#######")
                .aisle(
                        "######3U3######",
                        "####GGNNNGG####",
                        "###GNN3U3NNG###",
                        "##GCGG###GGCG##",
                        "#GNG#######GNG#",
                        "#GNG#######GNG#",
                        "3N3#########3N3",
                        "UNU#########UNU",
                        "3N3#########3N3",
                        "#GNG#######GNG#",
                        "#GNG#######GNG#",
                        "##GCGG###GGCG##",
                        "###GNN3U3NNG###",
                        "####GGNNNGG####",
                        "######3X3######")
                .aisle(
                        "#######3#######",
                        "######UNU######",
                        "####2223222####",
                        "###E#######E###",
                        "##2#########2##",
                        "##2#########2##",
                        "#U2#########2U#",
                        "3N3#########3N3",
                        "#U2#########2U#",
                        "##2#########2##",
                        "##2#########2##",
                        "###E#######E###",
                        "####2223222####",
                        "######UNU######",
                        "#######3#######")
                .aisle(
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "##2#########2##",
                        "#GNG#######GNG#",
                        "##2#########2##",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############")
                .aisle(
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "##2#########2##",
                        "#GNG#######GNG#",
                        "##2#########2##",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "#######G#######",
                        "######YCY######",
                        "#######G#######",
                        "#######G#######",
                        "###Y#######Y###",
                        "##GCGG###GGCG##",
                        "###Y#######Y###",
                        "#######G#######",
                        "#######G#######",
                        "######YCY######",
                        "#######G#######",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "######2N2######",
                        "####2223222####",
                        "###GNN3U3NNG###",
                        "####2223222####",
                        "######2N2######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "#######G#######",
                        "#######G#######",
                        "######UNU######",
                        "####GGNNNGG####",
                        "######UNU######",
                        "#######G#######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "#######3#######",
                        "######3U3######",
                        "#######3#######",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############")
                .where('U', stateIndex(0))
                .where('G', stateIndex(1))
                .where('C', stateIndex(4))
                .where('3', stateIndex(3))
                .where('2', stateIndex(2))
                .where('Y', stateIndex(2)
                        .or(metaTileEntities(Arrays.stream(MetaTileEntities.ENERGY_INPUT_HATCH)
                                .filter((mte) -> mte != null && mte.getTier() >= 8)
                                .toArray(MetaTileEntity[]::new)).setMinGlobalLimited(1).setMaxGlobalLimited(16)))
                .where('E', stateIndex(2)
                        .or(autoAbilities(false, false, true, true, true, true, false)))
                .where('X', selfPredicate())
                .where('N', air())
                .where('#', any())
                .build();
    }

    @Nonnull
    protected static TraceabilityPredicate stateIndex(int id) {
        return states(switch (id) {
            default -> GTCEFuCMetaBlocks.ADVANCED_CASING
                    .getState(GTCEFuCBlockAdvancedCasing.AdvancedCasingType.NULL_FIELD_CASING);
            case 1 -> MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
            case 4 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_COIL);
            case 2 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK2);
            case 3 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK3);
        });
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        if (this.recipeMapWorkable.isActive()) {
            return Textures.ACTIVE_FUSION_TEXTURE;
        } else {
            return Textures.FUSION_TEXTURE;
        }
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.FUSION_REACTOR_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityStarSiphon(this.metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        long energyStored = this.energyContainer.getEnergyStored();
        super.formStructure(context);
        this.initializeAbilities();
        ((EnergyContainerHandler) this.energyContainer).setEnergyStored(energyStored);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.energyContainer = new EnergyContainerHandler(this, 0, 0, 0, 0, 0) {

            @NotNull
            @Override
            public String getName() {
                return GregtechDataCodes.FUSION_REACTOR_ENERGY_CONTAINER_TRAIT;
            }
        };
        this.inputEnergyContainers = new EnergyContainerList(Lists.newArrayList());
        this.heat = 0;
        this.setFusionRingColor(NO_COLOR);
    }

    @Override
    protected void initializeAbilities() {
        this.inputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.inputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
        this.outputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        List<IEnergyContainer> energyInputs = getAbilities(MultiblockAbility.INPUT_ENERGY);
        this.inputEnergyContainers = new EnergyContainerList(energyInputs);
        long euCapacity = calculateEnergyStorageFactor(energyInputs.size());
        // Allow for adaptive max voltage
        this.energyContainer = new EnergyContainerHandler(this, euCapacity, inputEnergyContainers.getInputVoltage(), 0,
                0, 0) {

            @Nonnull
            @Override
            public String getName() {
                return GregtechDataCodes.FUSION_REACTOR_ENERGY_CONTAINER_TRAIT;
            }
        };
        this.setFusionRingColor(NO_COLOR);
    }

    private long calculateEnergyStorageFactor(int energyInputAmount) {
        return energyInputAmount * 5000000000L;
    }

    @Override
    protected void updateFormedValid() {
        if (this.inputEnergyContainers.getEnergyStored() > 0) {
            long energyAdded = this.energyContainer.addEnergy(this.inputEnergyContainers.getEnergyStored());
            if (energyAdded > 0) this.inputEnergyContainers.removeEnergy(energyAdded);
        }
        super.updateFormedValid();
        if (recipeMapWorkable.isWorking() && fusionRingColor == NO_COLOR) {
            if (recipeMapWorkable.getPreviousRecipe() != null &&
                    !recipeMapWorkable.getPreviousRecipe().getFluidOutputs().isEmpty()) {
                setFusionRingColor(0xFF000000 |
                        recipeMapWorkable.getPreviousRecipe().getFluidOutputs().get(0).getFluid().getColor());
            }
        } else if (!recipeMapWorkable.isWorking() && isStructureFormed()) {
            setFusionRingColor(NO_COLOR);
        }
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarInt(this.fusionRingColor);
        buf.writeVarInt(this.starTier);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.fusionRingColor = buf.readVarInt();
        this.starTier = buf.readVarInt();
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        if (dataId == GregtechDataCodes.UPDATE_COLOR) {
            this.fusionRingColor = buf.readVarInt();
        } else if (dataId == GregtechDataCodes.UPDATE_PARTICLE) {
            this.starTier = buf.readVarInt();
        } else {
            super.receiveCustomData(dataId, buf);
        }
    }

    protected int getFusionRingColor() {
        return this.fusionRingColor;
    }

    protected boolean hasFusionRingColor() {
        return this.fusionRingColor != NO_COLOR;
    }

    protected void setFusionRingColor(int fusionRingColor) {
        if (this.fusionRingColor != fusionRingColor) {
            this.fusionRingColor = fusionRingColor;
            writeCustomData(GregtechDataCodes.UPDATE_COLOR, buf -> buf.writeVarInt(fusionRingColor));
        }
    }

    protected void setStarTier(int starTier) {
        if (this.starTier != starTier) {
            this.starTier = starTier;
            writeCustomData(GregtechDataCodes.UPDATE_PARTICLE, buf -> buf.writeVarInt(starTier));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @Nonnull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtcefucontent.machine.antimatter_compressor.capacity",
                calculateEnergyStorageFactor(16) / 1000000000L));
        tooltip.add(I18n.format("gregtech.machine.fusion_reactor.overclocking"));
    }

    @Override
    public int getNumProgressBars() {
        return 2;
    }

    @Override
    public double getFillPercentage(int index) {
        if (index == 0) {
            return (double) this.energyContainer.getEnergyStored() / this.energyContainer.getEnergyCapacity();
        } else {
            return (double) this.heat / this.energyContainer.getEnergyCapacity();
        }
    }

    @Override
    public TextureArea getProgressBarTexture(int index) {
        if (index == 0) {
            return GuiTextures.PROGRESS_BAR_FUSION_ENERGY;
        } else {
            return GuiTextures.PROGRESS_BAR_FUSION_HEAT;
        }
    }

    @Override
    public void addBarHoverText(List<ITextComponent> hoverList, int index) {
        if (index == 0) {
            ITextComponent energyInfo = TextComponentUtil.stringWithColor(
                    TextFormatting.AQUA,
                    TextFormattingUtil.formatNumbers(energyContainer.getEnergyStored()) + " / " +
                            TextFormattingUtil.formatNumbers(energyContainer.getEnergyCapacity()) + " EU");
            hoverList.add(TextComponentUtil.translationWithColor(
                    TextFormatting.GRAY,
                    "gregtech.multiblock.energy_stored",
                    energyInfo));
        } else {
            ITextComponent heatInfo = TextComponentUtil.stringWithColor(
                    TextFormatting.RED,
                    TextFormattingUtil.formatNumbers(heat) + " / " +
                            TextFormattingUtil.formatNumbers(energyContainer.getEnergyCapacity()));
            hoverList.add(TextComponentUtil.translationWithColor(
                    TextFormatting.GRAY,
                    "gregtech.multiblock.fusion_reactor.heat",
                    heatInfo));
        }
    }

    protected class StarSiphonRecipeLogic extends MultiblockRecipeLogic {

        private boolean stopping;

        public StarSiphonRecipeLogic(MetaTileEntityStarSiphon tileEntity) {
            super(tileEntity);
        }

        @Override
        protected double getOverclockingDurationDivisor() {
            return 2.0D;
        }

        @Override
        protected double getOverclockingVoltageMultiplier() {
            return 2.0D;
        }

        @Override
        public void updateWorkable() {
            super.updateWorkable();
            // Drain heat when the reactor is not active, is paused via soft mallet, or does not have enough energy and
            // has fully wiped recipe progress
            // Don't drain heat when there is not enough energy and there is still some recipe progress, as that makes
            // it doubly hard to complete the recipe
            if (heat > 0 && (!isActive || !workingEnabled || (hasNotEnoughEnergy && progressTime == 0))) {
                heat = heat <= 30000 ? 0 : heat - 30000;
            }
        }

        @Override
        protected void completeRecipe() {
            super.completeRecipe();
            this.stopping = true;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            setStarTier(-1);
        }

        @Override
        protected void trySearchNewRecipe() {
            super.trySearchNewRecipe();
            if (this.stopping) setStarTier(-1);
        }

        @Override
        public boolean checkRecipe(@Nonnull Recipe recipe) {
            if (!super.checkRecipe(recipe))
                return false;

            // if the reactor is not able to hold enough energy for it, do not run the recipe
            if (recipe.getProperty(FusionEUToStartProperty.getInstance(), 0L) > energyContainer.getEnergyCapacity())
                return false;

            long heatDiff = recipe.getProperty(FusionEUToStartProperty.getInstance(), 0L) - heat;
            // if the stored heat is >= required energy, recipe is okay to run
            if (heatDiff <= 0) {
                setStarTier((GTCEFuCStarSiphonRecipes.starsRaw.indexOf(recipe.getAllItemOutputs().get(0))) / 6);
                this.stopping = false;
                return true;
            }

            // if the remaining energy needed is more than stored, do not run
            if (energyContainer.getEnergyStored() < heatDiff)
                return false;

            // remove the energy needed
            energyContainer.removeEnergy(heatDiff);
            // increase the stored heat
            heat += heatDiff;

            // calculate the tier of the star
            setStarTier((GTCEFuCStarSiphonRecipes.starsRaw.indexOf(recipe.getAllItemOutputs().get(0))) / 6);
            this.stopping = false;
            return true;
        }

        @Nonnull
        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound tag = super.serializeNBT();
            tag.setLong("Heat", heat);
            return tag;
        }

        @Override
        public void deserializeNBT(@Nonnull NBTTagCompound compound) {
            super.deserializeNBT(compound);
            heat = compound.getLong("Heat");
        }
    }

    @Override
    public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        if (this.hasFusionRingColor() && !this.registeredBloomRenderTicket) {
            this.registeredBloomRenderTicket = true;
            BloomEffectUtil.registerBloomRender(FusionBloomSetup.INSTANCE, getBloomType(), this, this);
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderBloomEffect(@NotNull BufferBuilder buffer, @NotNull EffectRenderContext context) {
        if (!this.hasFusionRingColor()) return;
        int color = RenderUtil.interpolateColor(this.getFusionRingColor(), -1, Eases.QUAD_IN
                .getInterpolation(
                        Math.abs((Math.abs(getOffsetTimer() % 50L) + context.partialTicks()) - 25F) / 25F));
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        EnumFacing relativeBack = RelativeDirection.BACK.getRelativeFacing(this.getFrontFacing(),
                this.getUpwardsFacing(), this.isFlipped());
        double x = (double) this.getPos().getX() - context.cameraX();
        double y = (double) this.getPos().getY() - context.cameraY();
        double z = (double) this.getPos().getZ() - context.cameraZ();
        renderFixedRing(buffer, x, y, z, EnumFacing.Axis.Y, r, g, b, a, relativeBack);
        renderFixedRing(buffer, x, y, z, EnumFacing.Axis.X, r, g, b, a, relativeBack);
        renderFixedRing(buffer, x, y, z, EnumFacing.Axis.Z, r, g, b, a, relativeBack);
        float dif = context.partialTicks() - this.partialTicksPrev;
        // if we've gone negative, add the remaining partial tick for the prev tick and the partial tick for this tick
        if (dif < 0) dif = context.partialTicks() + 1 - this.partialTicksPrev;
        this.partialTicksPrev = context.partialTicks();
        handlePoints(buffer, dif,
                x + relativeBack.getXOffset() * 7 + 0.5,
                y + relativeBack.getYOffset() * 7 + 0.5,
                z + relativeBack.getZOffset() * 7 + 0.5,
                r, g, b, a);

        cubeRenderHelper1.setDisplacement(
                x + relativeBack.getXOffset() * 7 + 0.5,
                y + relativeBack.getYOffset() * 7 + 0.5,
                z + relativeBack.getZOffset() * 7 + 0.5)
                .rotateY(dif / 10).renderCubeFrame(buffer,
                        253 / 255f, 243 / 255f, 158 / 255f, 1, 100); // 253 243 158

        if (this.getStarTier() > 0) {
            cubeRenderHelper2.setDisplacement(
                    x + relativeBack.getXOffset() * 7 + 0.5,
                    y + relativeBack.getYOffset() * 7 + 0.5,
                    z + relativeBack.getZOffset() * 7 + 0.5)
                    .rotateZ(-dif / 50).rotateX(dif / 20).renderCubeFrame(buffer,
                            142 / 255f, 62 / 255f, 236 / 255f, 1, 60); // 142 62 236
        }
        if (this.getStarTier() > 1) {
            cubeRenderHelper3.setDisplacement(
                    x + relativeBack.getXOffset() * 7 + 0.5,
                    y + relativeBack.getYOffset() * 7 + 0.5,
                    z + relativeBack.getZOffset() * 7 + 0.5)
                    .rotateY(-dif / 10).rotateZ(dif).renderCubeFrame(buffer,
                            225 / 255f, 57 / 255f, 43 / 255f, 1, 30); // 225 57 43
        }
    }

    private void handlePoints(BufferBuilder buffer, float dif, double x, double y, double z,
                              float r, float g, float b, float a) {
        for (InterpolatingPoint point : pointList) {
            if (point.isFinished()) {
                Vec3i dest = EnumFacing.random(random).getDirectionVec();
                point.setup(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1,
                        dest.getX() * 5, dest.getY() * 5, dest.getZ() * 5);
            } else {
                point.progress(dif);
            }
        }
        buffer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
        for (InterpolatingPoint point : pointList) {
            GL11.glPointSize(
                    (float) (40 / GTCEFuCUtil.pythagoreanAverage(point.x() + x, point.y() + y, point.z() + z)));
            buffer.pos(point.x() + x, point.y() + y, point.z() + z).color(r, g, b, a).endVertex();
        }
        Tessellator.getInstance().draw();
        GL11.glPointSize(1);
    }

    private void renderFixedRing(BufferBuilder buffer, double x, double y, double z, EnumFacing.Axis axis, float r,
                                 float g, float b, float a, EnumFacing relativeBack) {
        buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
        RenderBufferHelper.renderRing(buffer,
                x + relativeBack.getXOffset() * 7 + 0.5,
                y + relativeBack.getYOffset() * 7 + 0.5,
                z + relativeBack.getZOffset() * 7 + 0.5,
                6, 0.2, 10, 20,
                r, g, b, a, axis);
        Tessellator.getInstance().draw();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldRenderBloomEffect(@NotNull EffectRenderContext context) {
        return this.hasFusionRingColor();
    }

    public int getStarTier() {
        return starTier;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(GTCEFuCUtil.bbHelper(this, 6, 6, -1),
                GTCEFuCUtil.bbHelper(this, -6, -6, -13));
    }

    public boolean isGlobalRenderer() {
        return true;
    }

    private static BloomType getBloomType() {
        ConfigHolder.FusionBloom fusionBloom = ConfigHolder.client.shader.fusionBloom;
        return BloomType.fromValue(fusionBloom.useShader ? fusionBloom.bloomStyle : -1);
    }

    @SideOnly(Side.CLIENT)
    private static final class FusionBloomSetup implements IRenderSetup {

        private static final FusionBloomSetup INSTANCE = new FusionBloomSetup();

        float lastBrightnessX;
        float lastBrightnessY;

        private FusionBloomSetup() {}

        public void preDraw(@NotNull BufferBuilder buffer) {
            BloomEffect.strength = (float) ConfigHolder.client.shader.fusionBloom.strength;
            BloomEffect.baseBrightness = (float) ConfigHolder.client.shader.fusionBloom.baseBrightness;
            BloomEffect.highBrightnessThreshold = (float) ConfigHolder.client.shader.fusionBloom.highBrightnessThreshold;
            BloomEffect.lowBrightnessThreshold = (float) ConfigHolder.client.shader.fusionBloom.lowBrightnessThreshold;
            BloomEffect.step = 1;

            lastBrightnessX = OpenGlHelper.lastBrightnessX;
            lastBrightnessY = OpenGlHelper.lastBrightnessY;
            GlStateManager.color(1, 1, 1, 1);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            GlStateManager.disableTexture2D();
        }

        public void postDraw(@NotNull BufferBuilder buffer) {
            GlStateManager.enableTexture2D();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }
    }
}
