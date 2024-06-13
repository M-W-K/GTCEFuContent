package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import static com.m_w_k.gtcefucontent.api.recipes.logic.LimitedPerfectOverclockingLogic.limitedPerfectOverclockingLogic;
import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.*;

import java.util.*;
import java.util.function.DoubleSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Vector3d;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.m_w_k.gtcefucontent.api.gui.GTCEFuCGuiTextures;
import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.api.render.GTCEFuCTextures;
import com.m_w_k.gtcefucontent.api.util.MultiblockRenderRotHelper;

import gregicality.multiblocks.api.render.GCYMTextures;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IHPCAComponentHatch;
import gregtech.api.capability.impl.*;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.gui.widgets.ImageCycleButtonWidget;
import gregtech.api.gui.widgets.ImageWidget;
import gregtech.api.gui.widgets.IndicatorImageWidget;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.recipeproperties.FusionEUToStartProperty;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.api.util.interpolate.Eases;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.IRenderSetup;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.shader.postprocessing.BloomEffect;
import gregtech.client.shader.postprocessing.BloomType;
import gregtech.client.utils.*;
import gregtech.common.ConfigHolder;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityFluidHatch;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiFluidHatch;
import gregtech.common.metatileentities.multi.multiblockpart.hpca.MetaTileEntityHPCAComponent;

public class MetaTileEntityFusionStack extends RecipeMapMultiblockController implements IFastRenderMetaTileEntity,
                                       IBloomEffect, MultiblockRenderRotHelper.HelperUser {

    protected static final int NO_COLOR = 0;
    protected final Vector3d vecUpDown = new Vector3d();
    protected final Vector3d vecUpDownMirror = new Vector3d();
    protected final Vector3d vecLeftRightMirror = new Vector3d();
    protected final Vector3d vecForwardBack = new Vector3d();
    protected final Vector3d vecForwardBackMirror = new Vector3d();

    protected final MultiblockRenderRotHelper rotHelper;

    protected final List<Vector3d> activeVecs = new ArrayList<>(2);
    protected final List<Vector3d> activeMirrorVecs = new ArrayList<>(3);

    protected final int overclock_rating;
    protected EnergyContainerList inputEnergyContainers;
    protected long heat = 0;
    protected final FusionProgressSupplier progressBarSupplier;
    private int fusionRingColor = NO_COLOR;
    @SideOnly(Side.CLIENT)
    private boolean registeredBloomRenderTicket;

    // I had to copy word-for-word so many things from MetaTileEntityFusionReactor because EVERYTHING IS PRIVATE
    // Credit to the creators of GTCEu I guess...
    // And yes, I tried to use reflection, and I couldn't get it to stop throwing compile-time errors.
    // Plus, use of reflection isn't the most wise thing either.
    // Seriously, why so many private properties? Make them protected plz k thx
    public MetaTileEntityFusionStack(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, GTCEFuCRecipeMaps.FUSION_STACK_RECIPE_MAPS.get(overclock_rating(tier) - 1));
        this.recipeMapWorkable = new FusionStackRecipeLogic(this);
        this.overclock_rating = overclock_rating(tier);
        this.energyContainer = new EnergyContainerHandler(this, 0, 0, 0, 0, 0) {

            @Nonnull
            @Override
            public String getName() {
                return GregtechDataCodes.FUSION_REACTOR_ENERGY_CONTAINER_TRAIT;
            }
        };
        this.progressBarSupplier = new FusionProgressSupplier();
        this.rotHelper = new MultiblockRenderRotHelper(this,
                vecUpDown, vecUpDownMirror, vecForwardBack, vecForwardBackMirror, vecLeftRightMirror);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityFusionStack(metaTileEntityId, tier(1));
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return (switch (overclock_rating) {
            default -> FusionStackPatterns.FUSION_STACK;
            case 2 -> FusionStackPatterns.FUSION_ARRAY;
            case 3 -> FusionStackPatterns.FUSION_COMPLEX
                    .where('5',
                            metaTileEntities(MetaTileEntities.HPCA_ADVANCED_COMPUTATION_COMPONENT))
                    .where('6',
                            metaTileEntities(MetaTileEntities.HPCA_ACTIVE_COOLER_COMPONENT))
                    .where('7',
                            metaTileEntities(MetaTileEntities.HPCA_ADVANCED_COMPUTATION_COMPONENT))
                    .where('8',
                            metaTileEntities(MetaTileEntities.HPCA_ACTIVE_COOLER_COMPONENT));
        })
                .where('X', selfPredicate())
                .build();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart instanceof MetaTileEntityFluidHatch || sourcePart instanceof MetaTileEntityMultiFluidHatch)
            return getFluidHatchTexture();
        if (this.recipeMapWorkable.isActive()) {
            return Textures.ACTIVE_FUSION_TEXTURE;
        } else {
            return Textures.FUSION_TEXTURE;
        }
    }

    private ICubeRenderer getFluidHatchTexture() {
        switch (overclock_rating) {
            default -> {
                return Textures.STURDY_HSSE_CASING;
            }
            case 2 -> {
                return Textures.FROST_PROOF_CASING;
            }
            case 3 -> {
                return GCYMTextures.ATOMIC_CASING;
            }
        }
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        long energyStored = this.energyContainer.getEnergyStored();
        super.formStructure(context);
        this.initializeAbilities();
        ((EnergyContainerHandler) this.energyContainer).setEnergyStored(energyStored);
        // rotate HPCA components into alignment
        List<IHPCAComponentHatch> hpcaParts = getAbilities(MultiblockAbility.HPCA_COMPONENT);
        hpcaParts.stream().map(a -> {
            if (a instanceof MetaTileEntityHPCAComponent c) return c;
            return null;
        }).filter(Objects::nonNull).forEach(part -> {
            BlockPos relPos = part.getPos().subtract(this.getPos());
            EnumFacing left = RelativeDirection.LEFT.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
                    isFlipped());
            if (filteredPos(relPos, left) > 0) setFrontFacing(part, left);
            else setFrontFacing(part, left.getOpposite());
        });
    }

    private void setFrontFacing(MetaTileEntityHPCAComponent part, EnumFacing facing) {
        if (part.getFrontFacing() != facing)
            part.setFrontFacing(facing);
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
        this.energyContainer = new EnergyContainerHandler(this, euCapacity, GTValues.V[this.tier(2)], 0,
                0, 0) {

            @Nonnull
            @Override
            public String getName() {
                return GregtechDataCodes.FUSION_REACTOR_ENERGY_CONTAINER_TRAIT;
            }
        };
    }

    private long calculateEnergyStorageFactor(int energyInputAmount) {
        // each additional tier gets 2x as many inputs already
        return energyInputAmount * 80000000L;
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
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.fusionRingColor = buf.readVarInt();
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        if (dataId == GregtechDataCodes.UPDATE_COLOR) {
            this.fusionRingColor = buf.readVarInt();
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

    @Override
    protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
        // Background
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 198, 236);

        // Display
        builder.image(4, 4, 190, 138, GuiTextures.DISPLAY);

        // Energy Bar
        builder.widget(new ProgressWidget(
                () -> energyContainer.getEnergyCapacity() > 0 ?
                        1.0 * energyContainer.getEnergyStored() / energyContainer.getEnergyCapacity() : 0,
                4, 144, 94, 7,
                GuiTextures.PROGRESS_BAR_FUSION_ENERGY, ProgressWidget.MoveType.HORIZONTAL)
                        .setHoverTextConsumer(this::addEnergyBarHoverText));

        // Heat Bar
        builder.widget(new ProgressWidget(
                () -> energyContainer.getEnergyCapacity() > 0 ? 1.0 * heat / energyContainer.getEnergyCapacity() : 0,
                100, 144, 94, 7,
                GuiTextures.PROGRESS_BAR_FUSION_HEAT, ProgressWidget.MoveType.HORIZONTAL)
                        .setHoverTextConsumer(this::addHeatBarHoverText));

        // Indicator Widget
        builder.widget(new IndicatorImageWidget(174, 122, 17, 17, getLogo())
                .setWarningStatus(getWarningLogo(), this::addWarningText)
                .setErrorStatus(getErrorLogo(), this::addErrorText));

        // Title
        if (overclock_rating == 1) {
            // Stack
            builder.widget(
                    new ImageWidget(62, 9, 74, 12, GTCEFuCGuiTextures.FUSION_REACTOR_STACK_TITLE).setIgnoreColor(true));
        } else if (overclock_rating == 2) {
            // Array
            builder.widget(
                    new ImageWidget(63, 9, 73, 12, GTCEFuCGuiTextures.FUSION_REACTOR_ARRAY_TITLE).setIgnoreColor(true));
        } else {
            // Complex
            builder.widget(new ImageWidget(57, 9, 84, 12, GTCEFuCGuiTextures.FUSION_REACTOR_COMPLEX_TITLE)
                    .setIgnoreColor(true));
        }

        // Fusion Diagram + Progress Bar
        builder.widget(new ImageWidget(55, 24, 89, 101, GuiTextures.FUSION_REACTOR_DIAGRAM).setIgnoreColor(true));
        builder.widget(FusionProgressSupplier.Type.BOTTOM_LEFT.getWidget(this));
        builder.widget(FusionProgressSupplier.Type.TOP_LEFT.getWidget(this));
        builder.widget(FusionProgressSupplier.Type.TOP_RIGHT.getWidget(this));
        builder.widget(FusionProgressSupplier.Type.BOTTOM_RIGHT.getWidget(this));

        // Fusion Legend
        // builder.widget(new ImageWidget(7, 98, 108, 41, GuiTextures.FUSION_REACTOR_LEGEND).setIgnoreColor(true));

        // Power Button + Detail
        builder.widget(new ImageCycleButtonWidget(173, 211, 18, 18, GuiTextures.BUTTON_POWER,
                recipeMapWorkable::isWorkingEnabled, recipeMapWorkable::setWorkingEnabled));
        builder.widget(new ImageWidget(173, 229, 18, 6, GuiTextures.BUTTON_POWER_DETAIL));

        // Voiding Mode Button
        builder.widget(new ImageCycleButtonWidget(173, 189, 18, 18, GuiTextures.BUTTON_VOID_MULTIBLOCK,
                4, this::getVoidingMode, this::setVoidingMode)
                        .setTooltipHoverString(MultiblockWithDisplayBase::getVoidingModeTooltip));

        // Distinct Buses Unavailable Image
        builder.widget(new ImageWidget(173, 171, 18, 18, GuiTextures.BUTTON_NO_DISTINCT_BUSES)
                .setTooltip("gregtech.multiblock.universal.distinct_not_supported"));

        // Flex Unavailable Image
        builder.widget(getFlexButton(173, 153, 18, 18));

        // Player Inventory
        builder.bindPlayerInventory(entityPlayer.inventory, 153);
        return builder;
    }

    private static class FusionProgressSupplier {

        private final AtomicDouble tracker = new AtomicDouble(0.0);
        private final ProgressWidget.TimedProgressSupplier bottomLeft;
        private final DoubleSupplier topLeft;
        private final DoubleSupplier topRight;
        private final DoubleSupplier bottomRight;

        public FusionProgressSupplier() {
            // Bottom Left, fill on [0, 0.25)
            bottomLeft = new ProgressWidget.TimedProgressSupplier(200, 164, false) {

                @Override
                public double getAsDouble() {
                    double val = super.getAsDouble();
                    tracker.set(val);
                    if (val >= 0.25) {
                        return 1;
                    }
                    return 4 * val;
                }

                @Override
                public void resetCountdown() {
                    super.resetCountdown();
                    tracker.set(0);
                }
            };

            // Top Left, fill on [0.25, 0.5)
            topLeft = () -> {
                double val = tracker.get();
                if (val < 0.25) {
                    return 0;
                } else if (val >= 0.5) {
                    return 1;
                }
                return 4 * (val - 0.25);
            };

            // Top Right, fill on [0.5, 0.75)
            topRight = () -> {
                double val = tracker.get();
                if (val < 0.5) {
                    return 0;
                } else if (val >= 0.75) {
                    return 1;
                }
                return 4 * (val - 0.5);
            };

            // Bottom Right, fill on [0.75, 1.0]
            bottomRight = () -> {
                double val = tracker.get();
                if (val < 0.75) {
                    return 0;
                } else if (val >= 1) {
                    return 1;
                }
                return 4 * (val - 0.75);
            };
        }

        public void resetCountdown() {
            bottomLeft.resetCountdown();
        }

        public DoubleSupplier getSupplier(FusionProgressSupplier.Type type) {
            return switch (type) {
                case BOTTOM_LEFT -> bottomLeft;
                case TOP_LEFT -> topLeft;
                case TOP_RIGHT -> topRight;
                case BOTTOM_RIGHT -> bottomRight;
            };
        }

        private enum Type {

            BOTTOM_LEFT(
                    61, 66, 35, 41,
                    GuiTextures.PROGRESS_BAR_FUSION_REACTOR_DIAGRAM_BL, ProgressWidget.MoveType.VERTICAL),
            TOP_LEFT(
                    61, 30, 41, 35,
                    GuiTextures.PROGRESS_BAR_FUSION_REACTOR_DIAGRAM_TL, ProgressWidget.MoveType.HORIZONTAL),
            TOP_RIGHT(
                    103, 30, 35, 41,
                    GuiTextures.PROGRESS_BAR_FUSION_REACTOR_DIAGRAM_TR, ProgressWidget.MoveType.VERTICAL_DOWNWARDS),
            BOTTOM_RIGHT(
                    97, 72, 41, 35,
                    GuiTextures.PROGRESS_BAR_FUSION_REACTOR_DIAGRAM_BR, ProgressWidget.MoveType.HORIZONTAL_BACKWARDS);

            private final int x;
            private final int y;
            private final int width;
            private final int height;
            private final TextureArea texture;
            private final ProgressWidget.MoveType moveType;

            Type(int x, int y, int width, int height, TextureArea texture, ProgressWidget.MoveType moveType) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
                this.texture = texture;
                this.moveType = moveType;
            }

            public ProgressWidget getWidget(MetaTileEntityFusionStack instance) {
                return new ProgressWidget(
                        () -> instance.recipeMapWorkable.isActive() ?
                                instance.progressBarSupplier.getSupplier(this).getAsDouble() : 0,
                        x, y, width, height, texture, moveType)
                                .setIgnoreColor(true)
                                .setHoverTextConsumer(
                                        tl -> MultiblockDisplayText.builder(tl, instance.isStructureFormed())
                                                .setWorkingStatus(instance.recipeMapWorkable.isWorkingEnabled(),
                                                        instance.recipeMapWorkable.isActive())
                                                .addWorkingStatusLine());
            }
        }
    }

    private void addEnergyBarHoverText(List<ITextComponent> hoverList) {
        ITextComponent energyInfo = TextComponentUtil.stringWithColor(
                TextFormatting.AQUA,
                TextFormattingUtil.formatNumbers(energyContainer.getEnergyStored()) + " / " +
                        TextFormattingUtil.formatNumbers(energyContainer.getEnergyCapacity()) + " EU");
        hoverList.add(TextComponentUtil.translationWithColor(
                TextFormatting.GRAY,
                "gregtech.multiblock.energy_stored",
                energyInfo));
    }

    private void addHeatBarHoverText(List<ITextComponent> hoverList) {
        ITextComponent heatInfo = TextComponentUtil.stringWithColor(
                TextFormatting.RED,
                TextFormattingUtil.formatNumbers(heat) + " / " +
                        TextFormattingUtil.formatNumbers(energyContainer.getEnergyCapacity()));
        hoverList.add(TextComponentUtil.translationWithColor(
                TextFormatting.GRAY,
                "gregtech.multiblock.fusion_reactor.heat",
                heatInfo));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @Nonnull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.machine.fusion_reactor.capacity",
                calculateEnergyStorageFactor((int) (Math.pow(2, 3 + overclock_rating))) / 1000000L));
        tooltip.add(I18n.format("gregtech.machine.fusion_reactor.overclocking"));
        tooltip.add(TooltipHelper.RAINBOW_SLOW +
                I18n.format("gtcefucontent.machine.fusion_stack.perfect", overclock_rating));
        if (overclock_rating == 3) {
            tooltip.add(TooltipHelper.BLINKING_CYAN + I18n.format("gtcefucontent.machine.fusion_stack.coolant"));
        }
    }

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return GTCEFuCTextures.FUSION_STACK_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @SuppressWarnings("unused")
    public long getHeat() {
        return heat;
    }

    protected class FusionStackRecipeLogic extends MultiblockRecipeLogic {

        public FusionStackRecipeLogic(MetaTileEntityFusionStack tileEntity) {
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

        @Nonnull
        protected int[] runOverclockingLogic(@Nonnull IRecipePropertyStorage propertyStorage, int recipeEUt,
                                             long maxVoltage, int duration, int amountOC) {
            return limitedPerfectOverclockingLogic(
                    Math.abs(recipeEUt),
                    maxVoltage,
                    duration,
                    amountOC,
                    getOverclockingDurationDivisor(),
                    getOverclockingVoltageMultiplier(),
                    overclock_rating);
        }

        @Override
        protected void modifyOverclockPre(int @NotNull [] values, @NotNull IRecipePropertyStorage storage) {
            super.modifyOverclockPre(values, storage);

            // Limit the number of OCs to the difference in fusion reactor tier.
            // However, effective tier goes up by two per for fusion stacks.
            // This is compensated for by the euToStart doubling as well.
            long euToStart = storage.getRecipePropertyValue(FusionEUToStartProperty.getInstance(), 0L);
            int fusionTier = FusionEUToStartProperty.getFusionTier(euToStart);
            if (fusionTier != 0) fusionTier = MetaTileEntityFusionStack.this.tier(2) - fusionTier;
            values[2] = Math.min(fusionTier, values[2]);
        }

        @Override
        public long getMaxVoltage() {
            // Increase the tier-lock voltage twice as fast as normal fusion
            // UEV, UXV, MAX
            return Math.min(GTValues.V[MetaTileEntityFusionStack.this.tier(2)], this.getSumMaxVoltage());
        }

        private long getSumMaxVoltage() {
            // restore old logic of sum voltage across all hatches.
            // fusion stacks rely on excessive number of UHV hatches since UHV-exceeding hatches aren't available.
            return inputEnergyContainers.getInputVoltage();
        }

        @Override
        public void updateWorkable() {
            super.updateWorkable();
            // Drain heat when the reactor is not active, is paused via soft mallet, or does not have enough energy and
            // has fully wiped recipe progress
            // Don't drain heat when there is not enough energy and there is still some recipe progress, as that makes
            // it doubly hard to complete the recipe
            // (Will have to recover heat and recipe progress)
            if (heat > 0 && (!isActive || !workingEnabled || (hasNotEnoughEnergy && progressTime == 0))) {
                // heat loss should scale with the number of rings
                long lostHeat = (long) (10000 * Math.pow(2, overclock_rating));
                heat = heat <= lostHeat ? 0 : heat - lostHeat;
            }
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
            if (heatDiff <= 0)
                return true;

            // if the remaining energy needed is more than stored, do not run
            if (energyContainer.getEnergyStored() < heatDiff)
                return false;

            // remove the energy needed
            energyContainer.removeEnergy(heatDiff);
            // increase the stored heat
            heat += heatDiff;
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

        @Override
        protected void setActive(boolean active) {
            if (active != isActive) {
                MetaTileEntityFusionStack.this.progressBarSupplier.resetCountdown();
            }
            super.setActive(active);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        if (this.hasFusionRingColor() && !this.registeredBloomRenderTicket) {
            this.registeredBloomRenderTicket = true;
            BloomEffectUtil.registerBloomRender(FusionBloomSetup.INSTANCE, getBloomType(), this, this);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderBloomEffect(@NotNull BufferBuilder buffer, @NotNull EffectRenderContext context) {
        if (!this.hasFusionRingColor()) return;
        int color = RenderUtil.interpolateColor(this.getFusionRingColor(), -1, Eases.QUAD_IN
                .getInterpolation(Math.abs((Math.abs(getOffsetTimer() % 50) + context.partialTicks()) - 25) / 25));
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        double x = (double) this.getPos().getX() - context.cameraX();
        double y = (double) this.getPos().getY() - context.cameraY();
        double z = (double) this.getPos().getZ() - context.cameraZ();

        rotHelper.calculateRots(this.getFrontFacing(), this.getUpwardsFacing(), this.isFlipped());
        rotHelper.rotVecs();

        renderFusionRings(buffer, x, y, z, r, g, b, a);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldRenderBloomEffect(@NotNull EffectRenderContext context) {
        return this.hasFusionRingColor();
    }

    public void resetVecs() {
        this.activeVecs.clear();
        this.activeMirrorVecs.clear();
        if (this.overclock_rating == 1) {
            setVec(this.vecUpDownMirror, 2);
            setVec(this.vecForwardBack, -7);
        } else if (this.overclock_rating == 2) {
            setVec(this.vecUpDown, -4);
            setVec(this.vecUpDownMirror, 2);
            setVec(this.vecForwardBack, -4);
            setVec(this.vecLeftRightMirror, 10);
        } else {
            setVec(this.vecUpDown, -7);
            setVec(this.vecUpDownMirror, 2);
            setVec(this.vecForwardBack, -1);
            setVec(this.vecLeftRightMirror, 10);
            setVec(this.vecForwardBackMirror, 10);
        }
    }

    private void setVec(Vector3d vec, double value) {
        if (vec == this.vecUpDown) {
            this.activeVecs.add(vec);
            vec.set(0, value, 0);
        } else if (vec == this.vecUpDownMirror) {
            this.activeMirrorVecs.add(vec);
            vec.set(0, value, 0);
        } else if (vec == this.vecForwardBack) {
            this.activeVecs.add(vec);
            vec.set(value, 0, 0);
        } else if (vec == this.vecForwardBackMirror) {
            this.activeMirrorVecs.add(vec);
            vec.set(value, 0, 0);
        } else if (vec == this.vecLeftRightMirror) {
            this.activeMirrorVecs.add(vec);
            vec.set(0, 0, value);
        }
    }

    protected void renderFusionRings(BufferBuilder buffer, double x, double y, double z, float r, float g, float b,
                                     float a) {
        Vector3d vec = new Vector3d(x, y, z);
        for (Vector3d vector : activeVecs) {
            vec.add(vector);
        }
        // start a nesting process to go through mirror vecs
        renderFusionRingsNestHelp(buffer, vec, r, g, b, a, activeMirrorVecs);
    }

    private void renderFusionRingsNestHelp(BufferBuilder buffer, Vector3d vec, float r, float g, float b, float a,
                                           List<Vector3d> mirrorVecs) {
        // get the top vector
        Vector3d mirrorVector = mirrorVecs.get(0);
        Vector3d vecPos = new Vector3d(vec);
        vecPos.add(mirrorVector);
        Vector3d vecNeg = new Vector3d(vec);
        vecNeg.sub(mirrorVector);
        if (mirrorVecs.size() > 1) {
            // get a slice without the top vector
            mirrorVecs = mirrorVecs.subList(1, mirrorVecs.size());
            // keep nesting
            renderFusionRingsNestHelp(buffer, vecPos, r, g, b, a, mirrorVecs);
            renderFusionRingsNestHelp(buffer, vecNeg, r, g, b, a, mirrorVecs);
        } else {
            // start rendering
            renderFusionRing(buffer, vecPos, r, g, b, a);
            renderFusionRing(buffer, vecNeg, r, g, b, a);
        }
    }

    protected void renderFusionRing(BufferBuilder buffer, Vector3d vec, float r, float g, float b, float a) {
        buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
        RenderBufferHelper.renderRing(buffer,
                vec.getX() + 0.5, vec.getY() + 0.5, vec.getZ() + 0.5, 6, 0.2, 10, 20,
                r, g, b, a,
                RelativeDirection.UP.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), isFlipped()).getAxis());
        Tessellator.getInstance().draw();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (overclock_rating == 1) {
            return new AxisAlignedBB(bbHelper(this, 6, 2, -1),
                    bbHelper(this, -6, -2, -13));
        } else if (overclock_rating == 2) {
            return new AxisAlignedBB(bbHelper(this, 16, -2, 2),
                    bbHelper(this, -16, -6, -10));
        } else {
            return new AxisAlignedBB(bbHelper(this, 16, -4, 15),
                    bbHelper(this, -16, -8, -17));
        }
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

    protected int tier(int mult) {
        return this.overclock_rating * mult + GTValues.UV;
    }

    protected static int overclock_rating(int tier) {
        return tier - GTValues.UV;
    }

    public static void init() {
        FusionEUToStartProperty.registerFusionTier(9, "(Stack)");
        FusionEUToStartProperty.registerFusionTier(10, "(Array)");
        FusionEUToStartProperty.registerFusionTier(11, "(Complex)");
        FusionStackPatterns.init();
    }
}
