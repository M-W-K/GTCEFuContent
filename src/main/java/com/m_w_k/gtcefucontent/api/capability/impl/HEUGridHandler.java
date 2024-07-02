package com.m_w_k.gtcefucontent.api.capability.impl;

import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.getTemp;

import java.util.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.capability.IHEUComponent;
import com.m_w_k.gtcefucontent.api.metatileentity.IHeatExchanger;
import com.m_w_k.gtcefucontent.api.recipes.FullExchangeData;
import com.m_w_k.gtcefucontent.api.recipes.HeatExchangerRecipeHandler;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCHeatExchangerLoader;

import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.FluidPipeProperties;
import gregtech.api.unification.material.properties.PropertyKey;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class HEUGridHandler extends MTETrait implements IControllable {

    protected static final float BASE_SPEED = 1000;

    private boolean badPiping = false;
    private boolean needsNotification = false;
    private final Set<IHEUComponent> componentsPiped = new ObjectOpenHashSet<>();
    private final Set<IHEUComponent> reflectingEndpoints = new ObjectOpenHashSet<>();
    private boolean[] validAxi = new boolean[3];

    private IHEUComponent.HEUComponentType pipeHolderVariant;
    private int reflectionCount;
    private int pipeLength;
    private FluidPipeProperties pipeProperty;
    private int basePipeVolModifier;
    private double durationModifier;
    private long thermalEnergy;
    private boolean validGrid;
    private String invalidReason;

    private boolean validRecipe;
    private FluidStack[] prevRecipeStacks;
    private long[] prevRecipeThermals;
    private boolean slowedRecipe;
    private int cachedLength;
    private int recipeTime;
    private int recipeProgress;
    private FluidStack fluidAInitial;
    private int interpolationCount;
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

    public HEUGridHandler(IHeatExchanger controller) {
        super((MetaTileEntity) controller);
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
                } else if (this.pipeHolderVariant != component.getComponentType())
                    invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.type", false);
            }
            this.componentsPiped.add(component);
            // check for internal piping
            if (!component.hasValidPiping()) {
                invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.fill", true);
            } else {
                pipePropertyCheck(component.getPipeMaterial());
            }
        }

        if (this.validGrid) finalStructureCheck();
    }

    private boolean pipePropertyCheck(Material material) {
        if (material != null) {
            FluidPipeProperties properties = material.getProperty(PropertyKey.FLUID_PIPE);
            if (properties != null) {
                if (this.pipeProperty == null) {
                    this.pipeProperty = properties;
                    this.basePipeVolModifier = (int) Math.sqrt(properties.getThroughput());
                    return true;
                } else if (properties != pipeProperty)
                    invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.conflict", true);
                else
                    return true;
            } else
                invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.material", true);
        } else
            invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.material", true);
        return false;
    }

    private void finalStructureCheck() {
        // run endpoint check
        advancedEndpointValidityCheck();
        if (!this.validGrid) return;
        this.invalidReason = "";
        // fix pipe length
        this.pipeLength /= this.getHeatExchanger().getHEUCount();
        // 2/3 processing time if the exchanger uses conductive piping
        this.durationModifier = (this.pipeHolderVariant == IHEUComponent.HEUComponentType.H_CONDUCTIVE ? 4 :
                6) / 3D;
        this.durationModifier *= (this.reflectionCount + 1) * 10 / this.getHeatExchanger().getSpeedBonus();
    }

    private void advancedEndpointValidityCheck() {
        // no need to proceed if we have no returning endpoints
        if (this.reflectingEndpoints.size() == 0) return;

        double reflectraw = this.reflectingEndpoints.size() / (double) this.getHeatExchanger().getHEUCount();
        this.reflectionCount = (int) reflectraw;
        if (reflectionCount != reflectraw) {
            // bad reflective endpoint count, no need for complex examination
            invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.endpoints", false);
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
                invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.endpoints", false);
        }
    }

    private boolean checkPosAndNarrowAxis(BlockPos pos1, BlockPos pos2) {
        if (pos1.getX() != pos2.getX()) validAxi[0] = false;
        if (pos1.getY() != pos2.getY()) validAxi[1] = false;
        if (pos1.getZ() != pos2.getZ()) validAxi[2] = false;
        // if we've run out of valid axi, then the check is failed
        return validAxi[0] || validAxi[1] || validAxi[2];
    }

    private void invalidateGrid(String reason, boolean badPiping) {
        this.validGrid = false;
        this.badPiping = badPiping;
        this.invalidReason = reason;
    }

    private void invalidateRecipe(String reason) {
        this.validRecipe = false;
        resetRecipe();
        this.invalidReason = reason;
    }

    public void onStructureInvalidate() {
        setActive(false);
        resetStructure();
        resetRecipe();
        clearCache();
    }

    private void resetStructure() {
        this.validGrid = false;
        this.pipeHolderVariant = null;
        this.reflectionCount = 0;
        this.reflectingEndpoints.clear();
        this.pipeLength = 0;
        this.pipeProperty = null;
        this.basePipeVolModifier = 0;
        this.durationModifier = 0;
        this.thermalEnergy = 0;
        this.recipeTime = 0;
        this.recipeProgress = 0;
        this.invalidReason = "";
        this.badPiping = false;
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
                this.interpolationCount = 0;
                this.recipeProgress++;
            } else if (!this.slowedRecipe) this.recipeProgress++;
            else {
                // extremely slow decay of stocked thermal energy
                this.thermalEnergy *= 0.999;
            }
            if (runInterpolationLogic(getInterpolation())) {
                // try to process fluid B
                int mult = (int) (this.thermalEnergy / this.fluidBThermalEnergy);
                while (mult > 0) {
                    FluidStack fluidBInitialAdj = fluidAdjusted(fluidBInitial, mult);
                    FluidStack fluidBFinalAdj = fluidAdjusted(fluidBFinal, mult);
                    FluidStack fill = this.getHeatExchanger().getInputFluidInventory().drain(fluidBInitialAdj, false);
                    int drain = this.getHeatExchanger().getOutputFluidInventory().fill(fluidBFinalAdj, false);
                    if (fill != null && fill.amount == fluidBInitialAdj.amount && drain == fluidBFinalAdj.amount) {
                        this.thermalEnergy -= this.fluidBThermalEnergy * mult;
                        this.getHeatExchanger().getInputFluidInventory().drain(fluidBInitialAdj, true);
                        this.getHeatExchanger().getOutputFluidInventory().fill(fluidBFinalAdj, true);
                        break;
                    } else mult /= 2;
                    // If we have no mult left, we either have no fluid B or no space for fluid B
                    if (mult == 0) this.resetRecipe();
                }
                if (this.validRecipe) {
                    // move recipe forward a step if we haven't stocked too much thermal energy.
                    this.slowedRecipe = this.thermalEnergy + predictedNextHeat() > getMaxHeat();
                }
            }
        } else {
            this.setActive(false);
            // extremely slow decay of stocked thermal energy
            this.thermalEnergy *= 0.999;
        }
    }

    protected long predictedNextHeat() {
        return this.fluidAThermalEnergy * predictedNextOperationCount();
    }

    protected int predictedNextOperationCount() {
        return (int) Math.ceil(this.operationMultiplier() * BASE_SPEED / Math.max(1, this.recipeTime));
    }

    protected int operationMultiplier() {
        return this.getCurrentPipeVolModifier() * this.getHeatExchanger().getHEUCount();
    }

    protected double getInterpolation() {
        return (double) this.recipeProgress / this.recipeTime;
    }

    public long getMaxHeat() {
        if (!this.validMaxHeatCache) {
            long newMax = 2 *
                    Math.max(this.fluidBThermalEnergy, predictedNextHeat());
            // prevent shrinking the bar smaller than our current thermal energy
            if (newMax >= this.thermalEnergy) {
                this.cachedMaxEnergy = newMax;
                this.validMaxHeatCache = true;
            }
        }
        return this.cachedMaxEnergy;
    }

    private boolean runInterpolationLogic(double interpolation) {
        if (this.recipeTime != 0 && this.recipeProgress != 0) {
            int targetInterpolationCount = (int) Math.ceil(interpolation * this.operationMultiplier() * BASE_SPEED);
            if (targetInterpolationCount > this.interpolationCount) {
                int missingCount = targetInterpolationCount - this.interpolationCount;
                if (this.thermalEnergy + this.fluidAThermalEnergy * missingCount > this.getMaxHeat())
                    missingCount = (int) ((this.getMaxHeat() - this.thermalEnergy) / this.fluidAThermalEnergy);

                FluidStack toDrain = fluidAdjusted(this.fluidAInitial, missingCount);
                FluidStack toFill = fluidAdjusted(this.fluidAFinal, missingCount);
                int drain = this.tankIOHelper(toDrain, true, true);
                int fill = this.tankIOHelper(toFill, false, true);
                if (drain != toDrain.amount || fill != toFill.amount) {
                    this.needsNotification = true;
                    this.invalidateRecipe("gtcefucontent.multiblock.heat_exchanger.display.error.amount");
                    return false;
                }
                this.tankIOHelper(toDrain, true, false);
                this.tankIOHelper(toFill, false, false);
                this.thermalEnergy += this.fluidAThermalEnergy * missingCount;
                this.interpolationCount = targetInterpolationCount;
            }
        }
        return true;
    }

    private int tankIOHelper(FluidStack stack, boolean in, boolean simulate) {
        if (in) {
            FluidStack drain = this.getHeatExchanger().getInputFluidInventory().drain(stack, !simulate);
            if (drain == null) return 0;
            return drain.amount;
        } else {
            return this.getHeatExchanger().getOutputFluidInventory().fill(stack, !simulate);
        }
    }

    public void updateComponentPipingStatus() {
        // update grid validity
        // check component piping validity
        Optional<Boolean> validPipingO = componentsPiped.stream()
                .map(IHEUComponent::hasValidPiping)
                .reduce((a, b) -> a && b);
        boolean validPiping = validPipingO.isPresent() && validPipingO.get();
        if (!validPiping) {
            invalidateGrid("gtcefucontent.multiblock.heat_exchanger.display.error.fill", true);
        } else {
            // check component pipe properties
            this.pipeProperty = null;
            validPipingO = componentsPiped.stream()
                    .map((a) -> pipePropertyCheck(a.getPipeMaterial()))
                    .reduce((a, b) -> a && b);
            validPiping = validPipingO.isPresent() && validPipingO.get();
        }
        // update status
        if (!validGrid && badPiping) {
            validGrid = validPiping;
            badPiping = !validPiping;
            if (validGrid) finalStructureCheck();
        } else {
            badPiping = !validPiping;
        }
    }

    public boolean checkRecipeValidity() {
        // Prevent surprise exceptions
        if (!this.validGrid) {
            this.validRecipe = false;
            return false;
        }
        if (this.validRecipe) {
            if (this.recipeProgress <= 1 &&
                    !(hasFluid(fluidAdjusted(this.fluidAInitial, this.predictedNextOperationCount())) &&
                            hasFluid(this.fluidBInitial) &&
                            canInsert(fluidAdjusted(this.fluidAFinal, this.predictedNextOperationCount())) &&
                            canInsert(this.fluidBFinal))) {
                this.invalidateRecipe("gtcefucontent.multiblock.heat_exchanger.display.error.amount");
                // there is no need to perform the expensive recipe search if nothing changes in the inputs.
                this.needsNotification = true;
                return false;
            }
        } else {
            if (this.needsNotification) {
                if (!this.getHeatExchanger().getNotifiedFluidInputList().isEmpty() ||
                        !this.getHeatExchanger().getNotifiedFluidOutputList().isEmpty()) {
                    this.getHeatExchanger().getNotifiedFluidInputList().clear();
                    this.getHeatExchanger().getNotifiedFluidOutputList().clear();
                    this.needsNotification = false;
                } else return false;
            }

            if (prevRecipeCheck()) {
                fluidAInitial = prevRecipeStacks[0];
                fluidAFinal = prevRecipeStacks[1];
                fluidBInitial = prevRecipeStacks[2];
                fluidBFinal = prevRecipeStacks[3];
                fluidAThermalEnergy = prevRecipeThermals[0];
                fluidBThermalEnergy = prevRecipeThermals[1];
                recalculateRecipeConstants();
                this.validRecipe = true;
                this.validRecipe = checkRecipeValidity();
                this.validMaxHeatCache = false;
                return this.validRecipe;
            }

            var tankFluids = getTankFluids();

            FullExchangeData exchangeData = null;
            for (FluidStack coolable : tankFluids.keySet()) {
                // move on if this entry is not a coolable
                if (!HeatExchangerRecipeHandler.isCoolable(coolable.getFluid())) continue;

                for (FluidStack heatable : tankFluids.keySet()) {
                    // move on if this entry is not a heatable
                    if (!HeatExchangerRecipeHandler.isHeatable(heatable.getFluid())) continue;

                    if (heatable == coolable) continue;
                    exchangeData = HeatExchangerRecipeHandler.getHeatExchange(coolable, heatable, this.getHeatExchanger().getTargetEutecticTemperature());
                    if (exchangeData != null) {
                        // fluid A for the recipe logic should always be the fluid being cooled
                        boolean heatA = exchangeData.thermalEnergyA > 0;
                        fluidAInitial = heatA ? exchangeData.initialB : exchangeData.initialA;
                        fluidBInitial = heatA ? exchangeData.initialA : exchangeData.initialB;
                        fluidAFinal = heatA ? exchangeData.finalB : exchangeData.finalA;
                        fluidBFinal = heatA ? exchangeData.finalA : exchangeData.finalB;
                        fluidAThermalEnergy = heatA ? -exchangeData.thermalEnergyB : -exchangeData.thermalEnergyA;
                        fluidBThermalEnergy = heatA ? exchangeData.thermalEnergyA : exchangeData.thermalEnergyB;
                        recalculateRecipeConstants();
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
        return sanityCheck();
    }

    protected void recalculateRecipeConstants() {
        this.requiredPipeLength = (int) Math.sqrt(GTCEFuCUtil.geometricMean(
                getTemp(fluidAInitial) - getTemp(fluidAFinal),
                getTemp(fluidBFinal) - getTemp(fluidBInitial)));
        this.recipeProgress = 0;
        // over the course of recipe time, 1 unit of 'fluid a thermal energy' will be produced.
        // thus, in order to achieve a constant thermal energy / time no matter the exchange,
        // multiply recipe time by fluid a thermal energy.
        this.recipeTime = (int) (this.fluidAThermalEnergy * this.durationModifier *
                this.getHeatExchanger().getMaintenanceDurationMultiplier() /
                GTCEFuCHeatExchangerLoader.WATER_TO_STEAM_ENERGY);
    }

    private boolean sanityCheck() {
        return this.recipeTime > 0 &&
                this.fluidAInitial != null &&
                this.fluidBInitial != null &&
                this.fluidAFinal != null &&
                this.fluidBFinal != null &&
                this.fluidAThermalEnergy > 0 &&
                this.fluidBThermalEnergy > 0;
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

    private boolean prevRecipeCheck() {
        // if we are no longer compatible with the previous recipe fluids,
        // we should enable searching for a new recipe.
        if (prevRecipeStacks == null) return false;
        if (badFlowCheck(prevRecipeStacks[0]) || badFlowCheck(prevRecipeStacks[1]) ||
                badFlowCheck(prevRecipeStacks[2]) || badFlowCheck(prevRecipeStacks[3])) {
            invalidateRecipe("gtcefucontent.multiblock.heat_exchanger.display.error.fluid");
            return false;
        }
        if (this.requiredPipeLength > this.pipeLength * (this.reflectionCount + 1)) {
            invalidateRecipe("gtcefucontent.multiblock.heat_exchanger.display.error.len");
            return false;
        }
        return this.hasFluid(prevRecipeStacks[0]) && this.canInsert(prevRecipeStacks[1]) &&
                this.hasFluid(prevRecipeStacks[2]) && this.canInsert(prevRecipeStacks[3]);
    }

    private boolean badFlowCheck(FluidStack stack) {
        return !this.pipeProperty.test(stack);
    }

    private boolean hasFluid(FluidStack fluid) {
        FluidStack existing = getTankFluids().get(fluid);
        return existing != null && existing.amount >= fluid.amount;
    }

    private boolean canInsert(FluidStack fluid) {
        return this.getHeatExchanger().getOutputFluidInventory().fill(fluid, false) == fluid.amount;
    }

    // returns a map of fluidStacks mapped to themselves. Yep.
    private Map<FluidStack, FluidStack> getTankFluids() {
        Map<FluidStack, FluidStack> fluids = new Object2ObjectOpenHashMap<>();
        FluidStack stack;
        for (IFluidTankProperties tank : this.getHeatExchanger().getInputFluidInventory().getTankProperties()) {
            stack = tank.getContents();
            if (stack != null) {
                // behold my witchcraft
                FluidStack existing = fluids.get(stack);
                if (existing != null) {
                    existing.amount += stack.amount;
                } else {
                    FluidStack copy = stack.copy();
                    fluids.put(copy, copy);
                }
            }
        }
        return fluids;
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
                writeCustomData(GregtechDataCodes.WORKABLE_ACTIVE, buf -> buf.writeBoolean(isActive));
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

    public @NotNull IHeatExchanger getHeatExchanger() {
        return (IHeatExchanger) super.getMetaTileEntity();
    }

    public void setThermalEnergy(long thermalEnergy) {
        this.thermalEnergy = thermalEnergy;
    }

    public long getThermalEnergy() {
        return thermalEnergy;
    }

    public int getCurrentPipeVolModifier() {
        return basePipeVolModifier;
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
        this.prevRecipeStacks = new FluidStack[] { this.fluidAInitial, this.fluidAFinal, this.fluidBInitial,
                this.fluidBFinal };
        this.prevRecipeThermals = new long[] { this.fluidAThermalEnergy, this.fluidBThermalEnergy };
    }

    private void clearCache() {
        this.cachedLength = 0;
    }

    public void addInfo(List<ITextComponent> textList) {
        if (validGrid) {
            textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_exchanger.display.info",
                    this.pipeLength * (this.reflectionCount + 1), this.getCurrentPipeVolModifier(),
                    Math.floor(this.durationModifier * 100) / 100));
            if (this.getHeatExchanger().getTargetEutecticTemperature() != null) {
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_exchanger.display.info.target",
                        this.getHeatExchanger().getTargetEutecticTemperature()));
            }
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
        } else if (slowedRecipe) textList
                .add(new TextComponentTranslation("gtcefucontent.multiblock.heat_exchanger.display.error.amount2"));
    }

    public void addErrors(List<ITextComponent> textList) {
        if (!validGrid) {
            textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_exchanger.display.error"));
            textList.add(new TextComponentTranslation(invalidReason));
        }
    }
}
