package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.capability.IHEUComponent;
import com.m_w_k.gtcefucontent.api.gui.GTCEFuCGuiTextures;
import com.m_w_k.gtcefucontent.api.metatileentity.multiblock.GTCEFuCMultiBlockAbility;
import com.m_w_k.gtcefucontent.api.recipes.HeatExchangerRecipeHandler;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregtech.api.capability.*;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.IDataInfoProvider;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.IProgressBarMultiblock;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.FluidPipeProperties;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockCleanroomCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class MetaTileEntityHeatExchanger extends MultiblockWithDisplayBase
                                         implements IDataInfoProvider, IProgressBarMultiblock {

    protected IItemHandlerModifiable inputInventory;
    protected IItemHandlerModifiable outputInventory;
    protected IMultipleTankHandler inputFluidInventory;
    protected IMultipleTankHandler outputFluidInventory;

    protected final int tier;
    protected final int hEUCount;
    protected final List<IHEUComponent> notifiedHEUComponentList = new ArrayList<>();
    protected final HEUGridHandler heuHandler;

    public MetaTileEntityHeatExchanger(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId);
        this.tier = tier;
        this.hEUCount = (tier - 3) * (tier - 3);
        this.heuHandler = new HEUGridHandler(this);
    }

    @Override
    protected void updateFormedValid() {
        if (this.heuHandler.isWorkingEnabled()) {
            heuHandler.tick();
        } else {
            // clear our stored thermal energy when stopped
            heuHandler.thermalEnergy = 0;
        }
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.initializeAbilities();
        this.heuHandler.onStructureForm(getAbilities(GTCEFuCMultiBlockAbility.HEU_COMPONENT));
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.resetTileAbilities();
        this.heuHandler.onStructureInvalidate();
    }

    protected void initializeAbilities() {
        this.inputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.inputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
        this.outputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
    }

    private void resetTileAbilities() {
        this.inputInventory = new ItemStackHandler(0);
        this.inputFluidInventory = new FluidTankList(true);
        this.outputInventory = new ItemStackHandler(0);
        this.outputFluidInventory = new FluidTankList(true);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHeatExchanger(this.metaTileEntityId, this.tier);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return wherify(switch (this.tier) {
            default -> FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT)
                    .aisle("CIXIC", "#III#", "#III#", "#III#", "CIIIC")
                    .aisle("CCCCC", "GEEEG", "GEEEG", "GEEEG", "CCCCC")
                    .aisle("CCCCC", "GPPPG", "GPPPG", "GPPPG", "CCCCC").setRepeatable(4, 16)
                    .aisle("CCCCC", "GEEEG", "GEEEG", "GEEEG", "CCCCC")
                    .aisle("CIIIC", "#III#", "#III#", "#III#", "CIIIC");
            case 7 -> FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT)
                    .aisle("#IIXI#", "#IIII#", "#IIII#", "#IIII#", "#IIII#", "#IIII#")
                    .aisle("CCCCCC", "GEEEEG", "GEEEEG", "GEEEEG", "GEEEEG", "CCCCCC")
                    .aisle("CCCCCC", "GPPPPG", "GPPPPG", "GPPPPG", "GPPPPG", "CCCCCC").setRepeatable(4, 16)
                    .aisle("CCCCCC", "GEEEEG", "GEEEEG", "GEEEEG", "GEEEEG", "CCCCCC")
                    .aisle("#IIII#", "#IIII#", "#IIII#", "#IIII#", "#IIII#", "#IIII#");
            case 8 -> FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT)
                    .aisle("#IIXII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#")
                    .aisle("CCCCCCC", "GEEEEEG", "GEEEEEG", "GEEEEEG", "GEEEEEG", "GEEEEEG", "CCCCCCC")
                    .aisle("CCCCCCC", "GPPPPPG", "GPPPPPG", "GPPPPPG", "GPPPPPG", "GPPPPPG", "CCCCCCC")
                    .setRepeatable(4, 16)
                    .aisle("CCCCCCC", "GEEEEEG", "GEEEEEG", "GEEEEEG", "GEEEEEG", "GEEEEEG", "CCCCCCC")
                    .aisle("#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#");
        }, this.tier).build();
    }

    protected FactoryBlockPattern wherify(FactoryBlockPattern pattern, int tier) {
        return pattern
                .where('I', stateIndex(0).setMinGlobalLimited((tier - 3) * tier).or(autoAbilities(true, false))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setPreviewCount(2))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(2))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(1, 1))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(1, 1)))
                .where('C', stateIndex(1))
                .where('G', stateIndex(2))
                .where('E', metaTileEntities(GTCEFuCMetaTileEntities.HEU_ENDPOINTS))
                .where('P', metaTileEntities(GTCEFuCMetaTileEntities.HEU_HOLDERS))
                .where('X', selfPredicate())
                .where('#', any());
    }

    @Nonnull
    protected static TraceabilityPredicate stateIndex(int id) {
        return states(switch (id) {
            default -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.INVAR_HEATPROOF);
            case 1 -> MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.PLASCRETE);
            case 2 -> MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS);
        });
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.HEAT_PROOF_CASING;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return GCYMTextures.LARGE_AUTOCLAVE_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                this.isActive() || !this.heuHandler.isWorkingEnabled(), this.heuHandler.isWorkingEnabled());
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, @Nonnull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtcefucontent.machine.heat_exchanger.info",
                this.hEUCount));
    }

    @Override
    public SoundEvent getSound() {
        return GTSoundEvents.COOLING;
    }

    @Nonnull
    @Override
    public List<ITextComponent> getDataInfo() {
        List<ITextComponent> list = new ArrayList<>();
        if (ConfigHolder.machines.enableMaintenance && hasMaintenanceMechanics()) {
            list.add(new TextComponentTranslation("behavior.tricorder.multiblock_maintenance",
                    new TextComponentTranslation(TextFormattingUtil.formatNumbers(getNumMaintenanceProblems()))
                            .setStyle(new Style().setColor(TextFormatting.RED))));
        }
        return list;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            heuHandler.addInfo(textList);
        }
    }

    @Override
    protected void addWarningText(List<ITextComponent> textList) {
        super.addErrorText(textList);
        if (isStructureFormed()) {
            heuHandler.addWarnings(textList);
        }
    }

    @Override
    protected void addErrorText(List<ITextComponent> textList) {
        super.addErrorText(textList);
        if (isStructureFormed()) {
            heuHandler.addErrors(textList);
        }
    }

    @Override
    public boolean isActive() {
        return this.heuHandler.isActive() && this.heuHandler.isWorkingEnabled();
    }

    public void addNotifiedHeuComponent(IHEUComponent component) {
        if (!notifiedHEUComponentList.contains(component)) {
            this.notifiedHEUComponentList.add(component);
        }
    }

    public List<IHEUComponent> getNotifiedHEUComponentList() {
        return notifiedHEUComponentList;
    }

    @Override
    public double getFillPercentage(int index) {
        return (double) this.heuHandler.thermalEnergy / this.heuHandler.getMaxHeat();
    }

    @Override
    public TextureArea getProgressBarTexture(int index) {
        return GTCEFuCGuiTextures.PROGRESS_BAR_HEU_HEAT;
    }

    @Override
    public void addBarHoverText(List<ITextComponent> hoverList, int index) {
        ITextComponent heatInfo = TextComponentUtil.translationWithColor(
                TextFormatting.DARK_RED,
                "gtcefucontent.machine.heat_exchanger.heat1",
                TextFormattingUtil.formatLongToCompactString(this.heuHandler.thermalEnergy),
                TextFormattingUtil.formatLongToCompactString(this.heuHandler.getMaxHeat()));
        hoverList.add(TextComponentUtil.translationWithColor(
                TextFormatting.GRAY,
                "gtcefucontent.machine.heat_exchanger.heat2",
                heatInfo));
    }

    public static class HEUGridHandler extends MTETrait implements IControllable {

        private IItemHandlerModifiable componentsInv = new ItemStackHandler(0);
        private boolean badPiping = false;
        private boolean needsNotification = false;
        private final Map<IHEUComponent, Boolean> componentsPiped = new HashMap<>();
        private final Set<IHEUComponent> reflectingEndpoints = new ObjectOpenHashSet<>();
        private boolean[] validAxi = new boolean[3];

        private IHEUComponent.HEUComponentType pipeHolderVariant;
        private int reflectionCount;
        private int pipeLength;
        private FluidPipeProperties pipeProperty;
        private int pipeVolModifier;
        private double durationModifier;
        private long thermalEnergy;
        private boolean validGrid;
        private String invalidReason;

        private boolean validRecipe;
        private FluidStack[] prevRecipeInfo;
        private boolean slowedRecipe;
        private int cachedLength;
        private int recipeTime;
        private int recipeProgress;
        private FluidStack fluidAInitial;
        private final int[] targetInterpolationAmount = new int[2];
        private int[] interpolationAmount = new int[3];
        private long thermalInterpolation;
        private FluidStack fluidAFinal;
        private long fluidAThermalEnergy;
        private FluidStack fluidBInitial;
        private FluidStack fluidBFinal;
        private long fluidBThermalEnergy;
        private long cachedMaxEnergy = 0;
        private boolean validMaxHeatCache = true;
        private int requiredPipeLength;

        private boolean isActive;
        private boolean workingEnabled = true;

        public HEUGridHandler(MetaTileEntityHeatExchanger controller) {
            super(controller);
        }

        public void onStructureForm(Collection<IHEUComponent> components) {
            resetStructure();
            resetRecipe();
            this.validGrid = true;
            for (IHEUComponent component : components) {
                if (component.getComponentType().isEndpoint()) {
                    if (component.getComponentType() == IHEUComponent.HEUComponentType.E_RETURNING)
                        this.reflectingEndpoints.add(component);
                } else {
                    this.pipeLength += component.getComponentType() == IHEUComponent.HEUComponentType.H_EXPANDED ? 3 :
                            2;
                    if (this.pipeHolderVariant == null) {
                        this.pipeHolderVariant = component.getComponentType();
                    } else
                        if (this.pipeHolderVariant != component.getComponentType())
                            invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.type");
                }
                // check for internal piping
                if (!component.hasValidPiping()) {
                    this.componentsPiped.put(component, false);
                    invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.fill");
                    this.badPiping = true;
                } else {
                    this.componentsPiped.put(component, true);
                    pipePropertyCheck(component.getPipeMaterial());
                }
            }
            componentsInv = new ItemHandlerList(new ArrayList<>(components));

            if (this.validGrid) finalStructureCheck();
        }

        private boolean pipePropertyCheck(Material material) {
            if (material != null) {
                FluidPipeProperties properties = material.getProperty(PropertyKey.FLUID_PIPE);
                if (properties != null) {
                    if (this.pipeProperty == null) {
                        this.pipeProperty = properties;
                        this.pipeVolModifier = (int) Math.sqrt(properties.getThroughput());
                        return true;
                    } else if (properties != pipeProperty)
                        invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.conflict");
                    else
                        return true;
                } else
                    invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.material");
            } else
                invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.material");
            return false;
        }

        private void finalStructureCheck() {
            // run endpoint check
            advancedEndpointValidityCheck();
            if (!this.validGrid) return;
            // fix pipe length
            this.pipeLength /= this.getMetaTileEntity().hEUCount;
            // 2/3 processing time if the exchanger uses conductive piping
            this.durationModifier = (this.pipeHolderVariant == IHEUComponent.HEUComponentType.H_CONDUCTIVE ? 4 :
                    6) / 3D;
            // increase recipe time based on actual pipe length and reflection count
            this.durationModifier *= (this.reflectionCount + 1) * Math.sqrt(this.pipeLength);
        }

        private void advancedEndpointValidityCheck() {
            // no need to proceed if we have no returning endpoints
            if (this.reflectingEndpoints.size() == 0) return;

            double reflectraw = this.reflectingEndpoints.size() / (double) this.getMetaTileEntity().hEUCount;
            this.reflectionCount = (int) reflectraw;
            if (reflectionCount != reflectraw) {
                // bad reflective endpoint count, no need for complex examination
                invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.endpoints");
                return;
            }
            if (reflectionCount == 2) return;

            Iterator<IHEUComponent> iterator = reflectingEndpoints.iterator();
            BlockPos pos = iterator.next().getPos();
            // reset valid axi
            validAxi = new boolean[] { true, true, true };

            // iterate through reflective endpoints. If we find one out of the axis then the exchanger is invalid.
            while (iterator.hasNext()) {
                IHEUComponent endpoint = iterator.next();
                if (!checkPosAndNarrowAxis(pos, endpoint.getPos()))
                    invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.endpoints");
            }
        }

        private boolean checkPosAndNarrowAxis(BlockPos pos1, BlockPos pos2) {
            if (pos1.getX() != pos2.getX()) validAxi[0] = false;
            if (pos1.getY() != pos2.getY()) validAxi[1] = false;
            if (pos1.getZ() != pos2.getZ()) validAxi[2] = false;
            // if we've run out of valid axi, then the check is failed
            return validAxi[0] || validAxi[1] || validAxi[2];
        }

        private void invalidateGrid(String reason) {
            this.validGrid = false;
            this.badPiping = false;
            this.invalidReason = reason;
        }

        private void invalidateRecipe(String reason) {
            this.validRecipe = false;
            resetRecipe();
            this.invalidReason = reason;
        }

        private void onStructureInvalidate() {
            setActive(false);
            resetStructure();
            resetRecipe();
            clearCache();
        }

        private void resetStructure() {
            this.validGrid = false;
            this.pipeHolderVariant = null;
            this.reflectionCount = 0;
            this.pipeLength = 0;
            this.pipeProperty = null;
            this.pipeVolModifier = 0;
            this.durationModifier = 0;
            this.thermalEnergy = 0;
            this.recipeTime = 0;
            this.recipeProgress = 0;
            this.invalidReason = "";
            this.badPiping = false;
            this.componentsInv = new ItemStackHandler(0);
            this.componentsPiped.clear();
        }

        private void resetRecipe() {
            this.validRecipe = false;
            this.fluidAInitial = null;
            this.fluidAFinal = null;
            this.fluidBInitial = null;
            this.fluidBFinal = null;
            this.invalidReason = "";
            this.validMaxHeatCache = false;
        }

        public void tick() {
            // recipe processing
            if (checkRecipeValidity()) {
                this.setActive(true);
                if (this.recipeProgress >= this.recipeTime) {
                    // recipe completion
                    this.runInterpolationLogic(1D);
                    this.recipeProgress = 0;

                } else if (this.recipeProgress == 1) {
                    // recipe init
                    this.interpolationAmount = new int[2];
                    this.thermalInterpolation = 0;
                    this.targetInterpolationAmount[0] = fluidAdjusted(fluidAInitial).amount;
                    this.targetInterpolationAmount[1] = fluidAdjusted(fluidAFinal).amount;
                }
                if (runInterpolationLogic(getInterpolation())) {
                    // try to process fluid B
                    int mult = (int) (this.thermalEnergy / this.fluidBThermalEnergy);
                    while (mult > 0) {
                        FluidStack fluidBInitialAdj = fluidAdjusted(fluidBInitial, mult);
                        FluidStack fluidBFinalAdj = fluidAdjusted(fluidBFinal, mult);
                        FluidStack fill = this.getMetaTileEntity().inputFluidInventory.drain(fluidBInitialAdj, false);
                        int drain = this.getMetaTileEntity().outputFluidInventory.fill(fluidBFinalAdj, false);
                        if (fill != null && fill.amount == fluidBInitialAdj.amount && drain == fluidBFinalAdj.amount) {
                            this.thermalEnergy -= this.fluidBThermalEnergy * mult;
                            this.getMetaTileEntity().inputFluidInventory.drain(fluidBInitialAdj, true);
                            this.getMetaTileEntity().outputFluidInventory.fill(fluidBFinalAdj, true);
                            break;
                        } else mult--;
                        // If we have no mult left, we either have no fluid B or no space for fluid B
                        if (mult == 0) this.resetRecipe();
                    }
                    if (this.validRecipe) {
                        // move recipe forward a step if we haven't stocked too much thermal energy.
                        this.slowedRecipe = this.thermalEnergy > getMaxHeat() * 0.9;
                        if (!slowedRecipe) this.recipeProgress++;
                        else this.invalidReason = "gtcefucontent.multiblock.heat_exchanger.display.error.amount2";
                    }
                }
            } else {
                this.setActive(false);
                // decay stocked thermal energy
                this.thermalEnergy *= 0.99;
            }

            // piping I/O
            if (this.componentsInv.getSlots() != 0) {
                // piping fill processing
                if (this.getMetaTileEntity().inputInventory.getSlots() != 0) {
                    GTTransferUtils.moveInventoryItems(this.getMetaTileEntity().inputInventory, this.componentsInv);
                }

                // piping empty processing
                if (this.getMetaTileEntity().outputInventory.getSlots() != 0) {
                    GTTransferUtils.moveInventoryItems(this.componentsInv, this.getMetaTileEntity().outputInventory);
                }

                // recheck piping validity
                List<IHEUComponent> notifiedComponentList = this.getMetaTileEntity().getNotifiedHEUComponentList();
                if (!notifiedComponentList.isEmpty()) {
                    updateComponentPipingStatus(notifiedComponentList);
                    this.getMetaTileEntity().notifiedHEUComponentList.clear();
                }
            }
        }

        protected double getInterpolation() {
            return (double) this.recipeProgress / this.recipeTime;
        }

        protected long getMaxHeat() {
            if (!this.validMaxHeatCache) {
                // it seems counterintuitive, and it is.
                long newMax = Math.min(this.fluidAThermalEnergy, this.fluidBThermalEnergy) * 10;
                // prevent shrinking the bar smaller than our current thermal energy
                if (newMax >= this.thermalEnergy) {
                    this.cachedMaxEnergy = newMax;
                    this.validMaxHeatCache = true;
                }
            }
            return this.cachedMaxEnergy;
        }

        private boolean runInterpolationLogic(double interpolation) {
            // TODO prevent fluid loss on recipe interruption
            if (this.recipeTime != 0 && this.recipeProgress != 0) {
                int targetAmountI = (int) Math.ceil(interpolation * this.targetInterpolationAmount[0]);
                // casting to int acts as a Math.floor() call
                int targetAmountF = (int) (interpolation * this.targetInterpolationAmount[1]);
                long targetAmountT = (long) (interpolation * this.fluidAThermalEnergy * this.pipeVolModifier *
                        this.getMetaTileEntity().hEUCount);

                int missingAmountI = targetAmountI - this.interpolationAmount[0];
                int missingAmountF = targetAmountF - this.interpolationAmount[1];
                long missingAmountT = targetAmountT - this.thermalInterpolation;

                // kill the recipe if we run out of input
                int actualDrain = tankIOHelper(new FluidStack(this.fluidAInitial, missingAmountI), true, true);
                if (actualDrain != missingAmountI) {
                    this.resetRecipe();
                    return false;
                }

                tankIOHelper(new FluidStack(this.fluidAInitial, missingAmountI), true, false);
                tankIOHelper(new FluidStack(this.fluidAFinal, missingAmountF), false, false);
                this.thermalEnergy += missingAmountT;

                this.interpolationAmount[0] = targetAmountI;
                this.interpolationAmount[1] = targetAmountF;
                this.thermalInterpolation = targetAmountT;
            }
            return true;
        }

        private int tankIOHelper(FluidStack stack, boolean in, boolean simulate) {
            if (in) {
                FluidStack drain = this.getMetaTileEntity().inputFluidInventory.drain(stack, !simulate);
                if (drain == null) return 0;
                return drain.amount;
            } else {
                return this.getMetaTileEntity().outputFluidInventory.fill(stack, !simulate);
            }
        }

        private void updateComponentPipingStatus(List<IHEUComponent> components) {
            // update our knowledge of component validity
            for (IHEUComponent component : components) {
                boolean prevValidity = this.componentsPiped.get(component);
                if (prevValidity != component.hasValidPiping()) {
                    this.componentsPiped.put(component, !prevValidity);
                }
            }
            // update grid validity
            // check component piping validity
            Optional<Boolean> validPipingO = componentsPiped.keySet().stream()
                    .map(IHEUComponent::hasValidPiping)
                    .reduce((a, b) -> a && b);
            boolean validPiping = validPipingO.isPresent() && validPipingO.get();
            if (!validPiping) {
                invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.fill");
            } else {
                // check component pipe properties
                this.pipeProperty = null;
                validPipingO = componentsPiped.keySet().stream()
                        .map((a) -> pipePropertyCheck(a.getPipeMaterial()))
                        .reduce((a, b) -> a && b);
                validPiping = validPipingO.isPresent() && validPipingO.get();
            }
            // update status
            if (!validGrid && badPiping) {
                validGrid = validPiping;
                badPiping = !validPiping;
                if (validGrid) finalStructureCheck();
            }
        }

        public boolean checkRecipeValidity() {
            // Prevent surprise exceptions
            if (!this.validGrid) {
                this.validRecipe = false;
                return false;
            }
            Map<Fluid, Integer> tankFluids = getTankFluids();
            if (this.validRecipe) {
                if (this.recipeProgress <= 1 && !(hasFluid(this.fluidAInitial) && hasFluid(this.fluidBInitial) &&
                        canInsert(this.fluidAFinal) && canInsert(this.fluidBFinal))) {
                    this.invalidateRecipe("gtcefucontent.multiblock.heat_exchanger.display.error.amount");
                    // there is no need to perform the expensive recipe search if nothing changes in the inputs.
                    this.needsNotification = true;
                }
            } else {
                if (this.needsNotification) {
                    if (!this.getMetaTileEntity().notifiedFluidInputList.isEmpty() ||
                            !this.getMetaTileEntity().notifiedFluidOutputList.isEmpty()) {
                        this.getMetaTileEntity().notifiedFluidInputList.clear();
                        this.getMetaTileEntity().notifiedFluidOutputList.clear();
                        this.needsNotification = false;
                    } else return false;
                }

                if (prevRecipeCheck(tankFluids)) {
                    // previous valid recipe is now valid!
                    fluidAInitial = prevRecipeInfo[0];
                    fluidBInitial = prevRecipeInfo[2];
                    fluidAFinal = prevRecipeInfo[1];
                    fluidBFinal = prevRecipeInfo[3];
                    this.validRecipe = true;
                    this.validRecipe = checkRecipeValidity();
                    this.validMaxHeatCache = false;
                    return this.validRecipe;
                }

                // figure out if there is an exchange we can do

                Map<Fluid, Tuple<FluidStack, long[]>> cooling_map = HeatExchangerRecipeHandler.getCoolingMapCopy();
                Map<Fluid, Tuple<FluidStack, long[]>> heating_map = HeatExchangerRecipeHandler.getHeatingMapCopy();
                // iterate through coolables and heatables to see if we have a valid recipe
                Tuple<int[], FluidStack[]> exchangeData = null;
                for (Map.Entry<Fluid, Integer> coolable : tankFluids.entrySet()) {
                    // move on if this entry is not a coolable
                    if (!cooling_map.containsKey(coolable.getKey())) continue;

                    for (Map.Entry<Fluid, Integer> heatable : tankFluids.entrySet()) {
                        // move on if this entry is not a heatable
                        if (!heating_map.containsKey(heatable.getKey())) continue;

                        exchangeData = HeatExchangerRecipeHandler.getHeatExchange(coolable.getKey(), heatable.getKey());
                        if (exchangeData != null) {
                            // we found a recipe!
                            Tuple<FluidStack, long[]> A = cooling_map.get(coolable.getKey());
                            Tuple<FluidStack, long[]> B = heating_map.get(heatable.getKey());
                            fluidAInitial = new FluidStack(coolable.getKey(), (int) A.getSecond()[0]);
                            fluidBInitial = new FluidStack(heatable.getKey(), (int) B.getSecond()[0]);
                            fluidAFinal = A.getFirst();
                            fluidBFinal = B.getFirst();
                            fluidAThermalEnergy = A.getSecond()[1];
                            fluidBThermalEnergy = B.getSecond()[1];
                            this.requiredPipeLength = (int) Math.sqrt(GTCEFuCUtil.geometricMean(
                                    fluidAInitial.getFluid().getTemperature() - fluidAFinal.getFluid().getTemperature(),
                                    fluidBFinal.getFluid().getTemperature() -
                                            fluidBInitial.getFluid().getTemperature()));
                            recalcuateRecipeDuration();
                            this.validRecipe = true;
                            cacheValues();
                            finalRecipeCheck();
                            break;
                        }
                    }
                    if (exchangeData != null) break;
                }

                // there is no need to perform the expensive recipe search if nothing changes in the inputs.
                if (exchangeData == null) this.needsNotification = true;
            }
            return this.validRecipe;
        }

        private FluidStack fluidAdjusted(FluidStack stack) {
            return new FluidStack(stack, stack.amount * this.pipeVolModifier * this.getMetaTileEntity().hEUCount);
        }

        private FluidStack fluidAdjusted(FluidStack stack, int mult) {
            return new FluidStack(stack, stack.amount * mult);
        }

        private void finalRecipeCheck() {
            if (badFlowCheck(this.fluidAInitial) || badFlowCheck(this.fluidAFinal) ||
                    badFlowCheck(this.fluidBInitial) || badFlowCheck(this.fluidBFinal)) {
                invalidateRecipe("gtcefucontent.multiblock.heat_exchanger.display.error.fluid");
                return;
            }
            if (this.requiredPipeLength > this.pipeLength * (this.reflectionCount + 1)) {
                invalidateRecipe("gtcefucontent.multiblock.heat_exchanger.display.error.len");
                return;
            }
            if (!(HeatExchangerRecipeHandler.isEutectic(fluidAInitial.getFluid()) ||
                    HeatExchangerRecipeHandler.isEutectic(fluidBInitial.getFluid()))) {
                invalidateRecipe("gtcefucontent.multiblock.heat_exchanger.display.error.eutectic");
                return;
            }
            // do another validity check to ensure we have sufficient input amounts in our tanks
            this.validRecipe = checkRecipeValidity();
            this.validMaxHeatCache = false;
        }

        private boolean prevRecipeCheck(Map<Fluid, Integer> tankFluids) {
            // if we are no longer compatible with the previous recipe fluids,
            // we should enable searching for a new recipe.
            if (prevRecipeInfo == null) return false;
            if (badFlowCheck(prevRecipeInfo[0]) || badFlowCheck(prevRecipeInfo[1]) ||
                    badFlowCheck(prevRecipeInfo[2]) || badFlowCheck(prevRecipeInfo[3])) {
                invalidateRecipe("gtcefucontent.multiblock.heat_exchanger.display.error.fluid");
                return false;
            }
            if (this.requiredPipeLength > this.pipeLength * (this.reflectionCount + 1)) {
                invalidateRecipe("gtcefucontent.multiblock.heat_exchanger.display.error.len");
                return false;
            }
            return tankFluids.containsKey(prevRecipeInfo[0].getFluid()) &&
                    tankFluids.containsKey(prevRecipeInfo[1].getFluid()) &&
                    tankFluids.containsKey(prevRecipeInfo[2].getFluid()) &&
                    tankFluids.containsKey(prevRecipeInfo[3].getFluid());
        }

        private boolean badFlowCheck(FluidStack stack) {
            return !this.pipeProperty.test(stack);
        }

        private boolean hasFluid(FluidStack fluid) {
            Map<Fluid, Integer> tankFluids = getTankFluids();
            return tankFluids.containsKey(fluid.getFluid()) && tankFluids.get(fluid.getFluid()) >= fluid.amount;
        }

        private boolean canInsert(FluidStack fluid) {
            return this.getMetaTileEntity().outputFluidInventory.fill(fluid, false) == fluid.amount;
        }

        private void recalcuateRecipeDuration() {
            this.recipeProgress = 0;
            // correction so that the analogous operation would process the same amount of thermal energy per second
            double floatingRecipeTime = (double) this.fluidAThermalEnergy / this.fluidBThermalEnergy;
            floatingRecipeTime = (1 + floatingRecipeTime) / 2;
            this.recipeTime = (int) (3 * floatingRecipeTime * durationModifier *
                    this.getMetaTileEntity().getMaintenanceDurationMultiplier());
        }

        private Map<Fluid, Integer> getTankFluids() {
            Map<Fluid, Integer> fluidMap = new HashMap<>();
            FluidStack stack;
            for (IFluidTankProperties tank : this.getMetaTileEntity().inputFluidInventory.getTankProperties()) {
                stack = tank.getContents();
                if (stack != null) {
                    if (fluidMap.containsKey(stack.getFluid())) {
                        fluidMap.put(stack.getFluid(), stack.amount + fluidMap.get(stack.getFluid()));
                    } else {
                        fluidMap.put(stack.getFluid(), stack.amount);
                    }
                }
            }
            return fluidMap;
        }

        @Override
        public void writeInitialSyncData(@NotNull PacketBuffer buf) {
            super.writeInitialSyncData(buf);
            buf.writeBoolean(this.isActive);
            buf.writeBoolean(this.workingEnabled);
        }

        @Override
        public void receiveInitialSyncData(@NotNull PacketBuffer buf) {
            super.receiveInitialSyncData(buf);
            this.isActive = buf.readBoolean();
            this.workingEnabled = buf.readBoolean();
        }

        @Override
        public @NotNull NBTTagCompound serializeNBT() {
            NBTTagCompound tag = super.serializeNBT();
            tag.setBoolean("WorkEnabled", workingEnabled);
            tag.setLong("ThermalEnergy", thermalEnergy);
            return tag;
        }

        @Override
        public void deserializeNBT(@NotNull NBTTagCompound tag) {
            super.deserializeNBT(tag);
            this.workingEnabled = tag.getBoolean("WorkEnabled");
            this.thermalEnergy = tag.getLong("ThermalEnergy");
        }

        public void setActive(boolean active) {
            if (this.isActive != active) {
                this.isActive = active;
                this.getMetaTileEntity().markDirty();
                World world = this.getMetaTileEntity().getWorld();
                if (world != null && !world.isRemote) {
                    writeCustomData(GregtechDataCodes.IS_WORKING, buf -> buf.writeBoolean(isActive));
                }
            }
        }

        @Override
        public boolean isWorkingEnabled() {
            return this.workingEnabled;
        }

        @Override
        public void setWorkingEnabled(boolean workingEnabled) {
            this.workingEnabled = workingEnabled;
            metaTileEntity.markDirty();
            World world = metaTileEntity.getWorld();
            if (world != null && !world.isRemote) {
                writeCustomData(GregtechDataCodes.WORKING_ENABLED, buf -> buf.writeBoolean(workingEnabled));
            }
        }

        @Override
        public @NotNull MetaTileEntityHeatExchanger getMetaTileEntity() {
            return (MetaTileEntityHeatExchanger) super.getMetaTileEntity();
        }

        @Override
        public final @NotNull String getName() {
            return GregtechDataCodes.ABSTRACT_WORKABLE_TRAIT;
        }

        @Override
        public <T> T getCapability(Capability<T> capability) {
            if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
                return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
            }
            return null;
        }

        @Override
        public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
            if (dataId == GregtechDataCodes.WORKABLE_ACTIVE) {
                this.isActive = buf.readBoolean();
                this.getMetaTileEntity().scheduleRenderUpdate();
            } else if (dataId == GregtechDataCodes.WORKING_ENABLED) {
                this.workingEnabled = buf.readBoolean();
                this.getMetaTileEntity().scheduleRenderUpdate();
            }
        }

        public boolean isActive() {
            return isActive;
        }

        private void cacheValues() {
            this.cachedLength = this.requiredPipeLength;
            this.prevRecipeInfo = new FluidStack[] { this.fluidAInitial, this.fluidAFinal, this.fluidBInitial,
                    this.fluidBFinal };
        }

        private void clearCache() {
            this.cachedLength = 0;
        }

        public void addInfo(List<ITextComponent> textList) {
            if (validGrid) {
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_exchanger.display.info",
                        this.pipeLength * (this.reflectionCount + 1), this.pipeVolModifier,
                        Math.floor(this.durationModifier * 100) / 100));
                if (cachedLength != 0) {
                    textList.add(new TextComponentTranslation(
                            "gtcefucontent.multiblock.heat_exchanger.display.info.pipe", cachedLength));
                }
            }
        }

        public void addWarnings(List<ITextComponent> textList) {
            if (!validRecipe) {
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_exchanger.display.error"));
                if (invalidReason.equals(""))
                    invalidReason = "gtcefucontent.multiblock.heat_exchanger.display.error.recipe";
                textList.add(new TextComponentTranslation(invalidReason));
            }
            if (slowedRecipe) textList.add(new TextComponentTranslation(invalidReason));
        }

        public void addErrors(List<ITextComponent> textList) {
            if (!validGrid) {
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_exchanger.display.error"));
                textList.add(new TextComponentTranslation(invalidReason));
            }
        }
    }
}
