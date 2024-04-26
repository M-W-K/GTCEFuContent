package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.m_w_k.gtcefucontent.api.gui.GTCEFuCGuiTextures;
import com.m_w_k.gtcefucontent.api.recipes.HeatExchangerRecipeHandler;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCHeatExchangerLoader;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.IDataInfoProvider;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockCleanroomCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMufflerHatch;
import gregtech.core.sound.GTSoundEvents;

public class MetaTileEntityHeatReclaimer extends MultiblockWithDisplayBase
                                         implements IDataInfoProvider, IControllable, IProgressBarMultiblock {

    private final boolean isAdvanced;
    private MetaTileEntityMufflerHatch muffler;
    private RecipeMapMultiblockController watchedController;

    protected IMultipleTankHandler inputFluidInventory;
    protected IMultipleTankHandler outputFluidInventory;

    protected Exchange lastExchange = null;

    private long thermalEnergy = 0;
    private final long maxHeat;
    private final int maxTemp;

    private boolean isWorkingEnabled = true;
    private boolean isActive = false;

    public MetaTileEntityHeatReclaimer(ResourceLocation metaTileEntityId, boolean advanced) {
        super(metaTileEntityId);
        this.isAdvanced = advanced;
        this.maxHeat = GTCEFuCHeatExchangerLoader.WATER_TO_STEAM_ENERGY * (advanced ? 80 : 20);
        this.maxTemp = advanced ? 1300 : 400;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityHeatReclaimer(this.metaTileEntityId, this.isAdvanced);
    }

    @Override
    protected void updateFormedValid() {
        if (isWorkingEnabled) {
            recoverEnergy();
            setActive(tryFluidHeating());
            // leak thermal energy
        } else this.thermalEnergy *= 0.99;
    }

    protected boolean tryFluidHeating() {
        boolean atLeastOneExchange = false;
        for (IMultipleTankHandler.MultiFluidTankEntry multiFluidTankEntry : this.inputFluidInventory) {
            FluidStack fluid = multiFluidTankEntry.getFluid();
            if (fluid == null) break;
            if (this.isAdvanced && !HeatExchangerRecipeHandler.isEutectic(fluid.getFluid())) break;
            if (lastExchange == null || fluid.getFluid() != lastExchange.inputFluid) {
                var exchange = HeatExchangerRecipeHandler.getHeatExchange(fluid.getFluid(),
                        HeatExchangerRecipeHandler.ExchangeType.HEATING, this.maxTemp);
                if (exchange == null) continue;
                lastExchange = new Exchange(fluid.getFluid(), exchange);
            }
            while (this.thermalEnergy > lastExchange.triple.getMiddle()) {
                int fillable = this.outputFluidInventory.fill(lastExchange.triple.getRight(), false);
                if (fillable != lastExchange.triple.getRight().amount) {
                    lastExchange = null;
                    return atLeastOneExchange;
                }
                FluidStack drainable = this.inputFluidInventory.drain(
                        new FluidStack(fluid.getFluid(), lastExchange.triple.getLeft()), false);
                if (drainable == null || drainable.amount != lastExchange.triple.getLeft()) {
                    lastExchange = null;
                    return atLeastOneExchange;
                }
                atLeastOneExchange = true;
                this.thermalEnergy -= lastExchange.triple.getMiddle();
                this.inputFluidInventory.drain(drainable, true);
                this.outputFluidInventory.fill(lastExchange.triple.getRight(), true);
            }
            if (atLeastOneExchange) break;
        }
        return atLeastOneExchange;
    }

    protected void recoverEnergy() {
        if (this.thermalEnergy == this.maxHeat) return;
        if (getOffsetTimer() % 10 == 0) {
            this.muffler = getMufflerHatch();
            if (this.muffler == null) {
                this.watchedController = null;
                return;
            }
            if (muffler.getController() instanceof RecipeMapMultiblockController controller &&
                    isValidController(controller))
                this.watchedController = controller;
            else this.watchedController = null;
        }
        if (this.watchedController == null || !this.watchedController.isActive()) return;
        double recovery = Math.abs(this.watchedController.getRecipeMapWorkable().getRecipeEUt()) *
                GTCEFuCHeatExchangerLoader.SINGLE_EU_ENERGY;
        this.thermalEnergy += recovery * recoveryEfficiency(this.muffler);
        if (this.thermalEnergy > this.maxHeat) this.thermalEnergy = this.maxHeat;
    }

    private double recoveryEfficiency(@NotNull MetaTileEntityMufflerHatch muffler) {
        double recovery = Math.max(0.1, muffler.getTier() * 0.1);
        recovery /= this.getMaintenanceDurationMultiplier();
        return recovery * (this.isAdvanced ? 0.4 : 0.3);
    }

    protected static boolean isValidController(RecipeMapMultiblockController controller) {
        return controller.getRecipeMap() != RecipeMaps.STEAM_TURBINE_FUELS;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
    }

    @Override
    public void invalidateStructure() {
        // super sets this to false but doesn't send a sync packet
        this.setActive(false);
        super.invalidateStructure();
        resetTileAbilities();
    }

    protected void initializeAbilities() {
        this.inputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
    }

    private void resetTileAbilities() {
        this.inputFluidInventory = new FluidTankList(true);
        this.outputFluidInventory = new FluidTankList(true);
    }

    @Nullable
    protected MetaTileEntityMufflerHatch getMufflerHatch() {
        TileEntity te = getWorld().getTileEntity(this.getPos().offset(
                RelativeDirection.BACK.getRelativeFacing(frontFacing, upwardsFacing, isFlipped), 2));
        if (te instanceof MetaTileEntityHolder holder) {
            MetaTileEntity mte = holder.getMetaTileEntity();
            if (mte instanceof MetaTileEntityMufflerHatch hatch) return hatch;
        }
        return null;
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT)
                .aisle("CVC", "CXC", "CVC")
                .aisle("CVC", "V#V", "CVC")
                .where('C', stateIndex(0, this.isAdvanced)
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2, 1))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2, 1))
                        .or(autoAbilities(true, false)))
                .where('V', stateIndex(1, this.isAdvanced))
                .where('X', selfPredicate())
                .where('#', air())
                .build();
    }

    @Nonnull
    protected static TraceabilityPredicate stateIndex(int id, boolean advanced) {
        return states(id == 0 ?
                (advanced ?
                        (MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.PLASCRETE)) :
                        (GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                                .getState(BlockLargeMultiblockCasing.CasingType.STEAM_CASING))) :
                (advanced ?
                        (GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT)) :
                        (MetaBlocks.MULTIBLOCK_CASING
                                .getState(BlockMultiblockCasing.MultiblockCasingType.GRATE_CASING))));
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return isAdvanced ? Textures.PLASCRETE : GCYMTextures.STEAM_CASING;
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.BENDER_OVERLAY;
    }

    @Override
    public double getFillPercentage(int index) {
        return (double) this.thermalEnergy / this.maxHeat;
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
                TextFormattingUtil.formatLongToCompactString(this.thermalEnergy),
                TextFormattingUtil.formatLongToCompactString(this.maxHeat));
        hoverList.add(TextComponentUtil.translationWithColor(
                TextFormatting.GRAY,
                "gtcefucontent.machine.heat_exchanger.heat2",
                heatInfo));
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                this.isActive() || !this.isWorkingEnabled(), this.isWorkingEnabled());
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @javax.annotation.Nullable World player, @Nonnull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtcefucontent.machine.heat_reclaimer.info", this.maxTemp));
        if (this.isAdvanced) tooltip.add(I18n.format("gtcefucontent.machine.heat_reclaimer.info.eutectic"));
    }

    @Override
    public SoundEvent getSound() {
        return GTSoundEvents.FIRE;
    }

    @Override
    public @NotNull List<ITextComponent> getDataInfo() {
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
            if (this.muffler != null)
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_reclaimer.display.info",
                        TextFormattingUtil.formatNumbers(recoveryEfficiency(this.muffler) * 100)));
        }
    }

    @Override
    protected void addErrorText(List<ITextComponent> textList) {
        super.addErrorText(textList);
        if (isStructureFormed()) {
            if (this.muffler == null) {
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_reclaimer.display.error"));
                textList.add(
                        new TextComponentTranslation("gtcefucontent.multiblock.heat_reclaimer.display.error.muffler"));
            } else if (this.watchedController == null) {
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_reclaimer.display.error"));
                textList.add(new TextComponentTranslation(
                        "gtcefucontent.multiblock.heat_reclaimer.display.error.controller"));
            }
        }
    }

    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("isWorkingEnabled", this.isWorkingEnabled);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.isWorkingEnabled = data.getBoolean("isWorkingEnabled");
    }

    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;
            markDirty();
            if (getWorld() != null && !getWorld().isRemote) {
                writeCustomData(GregtechDataCodes.IS_WORKING, buf -> buf.writeBoolean(isActive));
            }
        }
    }

    @Override
    public boolean isActive() {
        return isActive && isWorkingEnabled;
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(this.isActive);
        buf.writeBoolean(this.isWorkingEnabled);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.isActive = buf.readBoolean();
        this.isWorkingEnabled = buf.readBoolean();
    }

    @Override
    public boolean isWorkingEnabled() {
        return this.isWorkingEnabled;
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        if (this.isWorkingEnabled != isWorkingAllowed) {
            this.isWorkingEnabled = isWorkingAllowed;
            markDirty();
            if (getWorld() != null && !getWorld().isRemote) {
                writeCustomData(GregtechDataCodes.WORKING_ENABLED, buf -> buf.writeBoolean(isWorkingEnabled));
            }
        }
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.WORKING_ENABLED) {
            this.isWorkingEnabled = buf.readBoolean();
            this.scheduleRenderUpdate();
        }
        if (dataId == GregtechDataCodes.IS_WORKING) {
            this.scheduleRenderUpdate();
        }
    }

    protected static class Exchange {

        public final Fluid inputFluid;
        public final Triple<Integer, Long, FluidStack> triple;

        public Exchange(Fluid inputFluid, Triple<Integer, Long, FluidStack> triple) {
            this.inputFluid = inputFluid;
            this.triple = triple;
        }
    }
}
