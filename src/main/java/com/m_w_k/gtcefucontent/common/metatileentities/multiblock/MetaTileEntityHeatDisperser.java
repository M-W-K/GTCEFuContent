package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.m_w_k.gtcefucontent.api.gui.GTCEFuCGuiTextures;
import com.m_w_k.gtcefucontent.api.recipes.HalfExchangeData;
import com.m_w_k.gtcefucontent.api.recipes.HeatExchangerRecipeHandler;
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
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.pipenet.tile.IPipeTile;
import gregtech.api.unification.material.Material;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockCleanroomCasing;
import gregtech.common.blocks.BlockFrame;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MetaTileEntityHeatDisperser extends MultiblockWithDisplayBase
                                         implements IDataInfoProvider, IControllable, IProgressBarMultiblock {

    private final static int THERMAL_CAPACITY_CONSTANT = 500000;

    protected IMultipleTankHandler inputFluidInventory;
    protected IMultipleTankHandler outputFluidInventory;

    protected HalfExchangeData lastExchange = null;
    private long lastOperationTick = -1;

    protected final int targetTemperature;
    protected final double dispersalRate;
    protected final int maxTemperatureDifference;

    protected long thermalMass = 1;
    protected double chassisTemperature;

    private boolean isPaused;
    private boolean isSwitching;

    private boolean isWorkingEnabled = true;
    private boolean isActive = false;

    public MetaTileEntityHeatDisperser(ResourceLocation metaTileEntityId, int targetTemperature, double dispersalRate,
                                       int maxTemperatureDifference) {
        super(metaTileEntityId);
        this.targetTemperature = targetTemperature;
        this.chassisTemperature = targetTemperature;
        this.dispersalRate = dispersalRate;
        this.maxTemperatureDifference = maxTemperatureDifference;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityHeatDisperser(this.metaTileEntityId, this.targetTemperature, this.dispersalRate,
                this.maxTemperatureDifference);
    }

    @Override
    protected void updateFormedValid() {
        if (isWorkingEnabled) {
            // overticking? think again
            if (this.getWorld() != null && !this.getWorld().isRemote) {
                long tick = FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter();
                if (this.lastOperationTick == tick && Math.abs(this.chassisTemperature - this.targetTemperature) > 10) {
                    this.overtickExplosion();
                    return;
                } else this.lastOperationTick = tick;
            }

            setActive(tryHeatCollection());
            this.chassisTemperature = (this.chassisTemperature - this.targetTemperature) *
                    this.dispersalRate + this.targetTemperature;
        }
    }

    protected boolean tryHeatCollection() {
        // if the disperser is beyond its temperature limit, do nothing.
        if (Math.abs(this.chassisTemperature - this.targetTemperature) > this.maxTemperatureDifference) {
            this.setPaused(true);
            return false;
        } else this.setPaused(false);
        // if the disperser is already hot, only allow cooling. If it is already cold, only allow heating.
        if (this.chassisTemperature > this.targetTemperature + 10) {
            boolean active = tryFluidCooling();
            this.setSwitching(!active);
            return active;
        }
        else if (this.chassisTemperature < this.targetTemperature - 10) {
            boolean active = tryFluidHeating();
            this.setSwitching(!active);
            return active;
        }
        else {
            this.setSwitching(false);
            return tryFluidHeating() || tryFluidCooling();
        }
    }

    protected boolean tryFluidHeating() {
        for (IMultipleTankHandler.MultiFluidTankEntry multiFluidTankEntry : this.inputFluidInventory) {
            FluidStack fluid = multiFluidTankEntry.getFluid();
            if (fluid == null) break;
            if (lastExchange == null || fluid != lastExchange.in) {
                var exchange = HeatExchangerRecipeHandler.getHeatExchange(fluid,
                        HeatExchangerRecipeHandler.ExchangeType.HEATING, this.targetTemperature);
                if (exchange == null) continue;
                lastExchange = exchange;
            }
            if (doLastExchange()) return true;
        }
        return false;
    }

    protected boolean tryFluidCooling() {
        for (IMultipleTankHandler.MultiFluidTankEntry multiFluidTankEntry : this.inputFluidInventory) {
            FluidStack fluid = multiFluidTankEntry.getFluid();
            if (fluid == null) break;
            if (lastExchange == null || fluid != lastExchange.in) {
                var exchange = HeatExchangerRecipeHandler.getHeatExchange(fluid,
                        HeatExchangerRecipeHandler.ExchangeType.COOLING, this.targetTemperature);
                if (exchange == null) continue;
                lastExchange = exchange;
            }
            if (doLastExchange()) return true;
        }
        return false;
    }

    protected boolean doLastExchange() {
        if (this.lastExchange == null) return false;
        boolean once = false;
        while (Math.abs(this.chassisTemperature - this.targetTemperature) < this.maxTemperatureDifference) {
            int fillable = this.outputFluidInventory.fill(lastExchange.out, false);
            if (fillable != lastExchange.out.amount) {
                lastExchange = null;
                return false;
            }
            FluidStack drainable = this.inputFluidInventory.drain(lastExchange.in, false);
            if (drainable == null || drainable.amount != lastExchange.in.amount) {
                lastExchange = null;
                return false;
            }
            this.chassisTemperature -= (double) lastExchange.thermalEnergy / this.thermalMass;
            this.inputFluidInventory.drain(drainable, true);
            this.outputFluidInventory.fill(lastExchange.out, true);
            once = true;
        }
        return once;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
        assert this.structurePattern != null;
        this.thermalMass = 1;
        this.structurePattern.cache.values().forEach(blockInfo -> {
            if (blockInfo.getBlockState().getBlock() instanceof BlockFrame frame) {
                // sometimes the JEI world dies because of an NPE inside the material class
                try {
                    this.thermalMass += frame.getGtMaterial(blockInfo.getBlockState()).getMass();
                } catch (Exception ignored) {}
            }
        });
        this.thermalMass *= THERMAL_CAPACITY_CONSTANT;
    }

    @Override
    public void invalidateStructure() {
        // super sets this to false but doesn't send a sync packet
        this.setActive(false);
        super.invalidateStructure();
        resetTileAbilities();
        this.thermalMass = 1;
    }

    protected void initializeAbilities() {
        this.inputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
    }

    private void resetTileAbilities() {
        this.inputFluidInventory = new FluidTankList(true);
        this.outputFluidInventory = new FluidTankList(true);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT)
                .aisle("CXC", "CCC", "CCC", "CCC")
                .aisle("FGF", "FGF", "FGF", "FGF").setRepeatable(4, 16)
                .aisle("CCC", "CCC", "CCC", "CCC")
                .where('C', stateIndex(0).setMinGlobalLimited(12)
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(1))
                        .or(autoAbilities(true, false)))
                .where('G', stateIndex(1))
                .where('F', frames())
                .where('X', selfPredicate())
                .build();
    }

    protected TraceabilityPredicate frames() {
        return states(MetaBlocks.FRAMES.entrySet().stream().map(e -> e.getValue().getBlock(e.getKey()))
                .toArray(IBlockState[]::new));
    }

    @Nonnull
    protected static TraceabilityPredicate stateIndex(int id) {
        return states(id == 0 ?
                (MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.PLASCRETE)) :
                (MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.GRATE_CASING)));
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.PLASCRETE;
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.DISTILLERY_OVERLAY;
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
        tooltip.add(I18n.format("gtcefucontent.machine.heat_disperser.info", this.targetTemperature));
    }

    @Override
    public SoundEvent getSound() {
        return GTSoundEvents.ELECTROLYZER;
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
    public double getFillPercentage(int index) {
        return Math.min(1d,
                0.5d + (this.chassisTemperature - this.targetTemperature) / (this.maxTemperatureDifference * 3));
    }

    @Override
    public TextureArea getProgressBarTexture(int index) {
        return GTCEFuCGuiTextures.PROGRESS_BAR_HEU_HEAT;
    }

    @Override
    public void addBarHoverText(List<ITextComponent> hoverList, int index) {
        ITextComponent heatInfo = TextComponentUtil.translationWithColor(
                TextFormatting.DARK_RED,
                "gtcefucontent.machine.heat_disperser.heat1",
                TextFormattingUtil.formatLongToCompactString((long) this.chassisTemperature));
        hoverList.add(TextComponentUtil.translationWithColor(
                TextFormatting.GRAY,
                "gtcefucontent.machine.heat_disperser.heat2",
                heatInfo));
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_disperser.display.info", TextFormattingUtil.formatLongToCompactString(this.thermalMass)));
            if (this.isPaused) {
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_disperser.display.error.temperature"));
            } else if (this.isSwitching) {
                String type = this.chassisTemperature > this.targetTemperature ? "hot" : "cold";
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_disperser.display.error." + type));
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
        data.setDouble("Temperature", this.chassisTemperature);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.isWorkingEnabled = data.getBoolean("isWorkingEnabled");
        this.chassisTemperature = data.getDouble("Temperature");
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

    protected void setPaused(boolean paused) {
        isPaused = paused;
    }

    protected void setSwitching(boolean switching) {
        isSwitching = switching;
    }

    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;
            markDirty();
            if (getWorld() != null && !getWorld().isRemote) {
                writeCustomData(GregtechDataCodes.WORKABLE_ACTIVE, buf -> buf.writeBoolean(isActive));
            }
        }
    }

    @Override
    public boolean isActive() {
        return isActive && isWorkingEnabled;
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
    public boolean isWorkingEnabled() {
        return this.isWorkingEnabled;
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.WORKING_ENABLED) {
            this.isWorkingEnabled = buf.readBoolean();
            this.scheduleRenderUpdate();
        }
        if (dataId == GregtechDataCodes.WORKABLE_ACTIVE) {
            this.isActive = buf.readBoolean();
            this.scheduleRenderUpdate();
        }
    }

    protected void overtickExplosion() {
        if (com.m_w_k.gtcefucontent.common.ConfigHolder.heatDisperserExplodesOnOvertick) {
            setExploded();
            getWorld().setBlockToAir(getPos());
            getWorld().createExplosion(null, getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5,
                    10, true);
        }
    }
}
