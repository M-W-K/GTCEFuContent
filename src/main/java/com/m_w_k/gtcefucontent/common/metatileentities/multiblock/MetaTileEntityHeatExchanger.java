package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
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

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.capability.IHEUComponent;
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
import gregtech.api.fluids.MaterialFluid;
import gregtech.api.fluids.fluidType.FluidTypes;
import gregtech.api.metatileentity.IDataInfoProvider;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.FluidPipeProperties;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.util.RelativeDirection;
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

public class MetaTileEntityHeatExchanger extends MultiblockWithDisplayBase implements IDataInfoProvider, IControllable {

    protected IMultipleTankHandler inputFluidInventory;
    protected IMultipleTankHandler outputFluidInventory;
    private final int tier;
    private final int hEUCount;
    private final HEUGridHandler heuHandler;

    private boolean isWorkingEnabled = true;

    public MetaTileEntityHeatExchanger(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId);
        this.tier = tier;
        this.hEUCount = (tier - 3) * (tier - 3);
        this.heuHandler = new HEUGridHandler(this);
    }

    @Override
    protected void updateFormedValid() {
        if (isWorkingEnabled()) {
            heuHandler.tick();
        } else {
            // clear our stored thermal energy when stopped
            heuHandler.thermalEnergy = 0;
        }
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.inputFluidInventory = new FluidTankList(false, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        this.heuHandler.onStructureForm(getAbilities(GTCEFuCMultiBlockAbility.HEU_COMPONENT));
    }

    @Override
    public void invalidateStructure() {
        // super sets this to false but doesn't send a sync packet
        this.setActive(false);
        super.invalidateStructure();
        this.heuHandler.onStructureInvalidate();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHeatExchanger(this.metaTileEntityId, this.tier);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return (switch (tier) {
            default -> FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT)
                    .aisle("CIXIC", "#III#", "#III#", "#III#", "CIIIC")
                    .aisle("CCCCC", "GEEEG", "GEEEG", "GEEEG", "CCCCC")
                    .aisle("CCCCC", "GPPPG", "GPPPG", "GPPPG", "CCCCC").setRepeatable(4, 16)
                    .aisle("CCCCC", "GEEEG", "GEEEG", "GEEEG", "CCCCC")
                    .aisle("CIIIC", "#III#", "#III#", "#III#", "CIIIC")
                    .where('I', stateIndex(0).setMinGlobalLimited(12).or(autoAbilities(true, false))
                            .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setPreviewCount(2))
                            .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(2)))
                    .where('C', stateIndex(1))
                    .where('G', stateIndex(2))
                    .where('E', metaTileEntities(GTCEFuCMetaTileEntities.HEU_ENDPOINTS))
                    .where('P', metaTileEntities(GTCEFuCMetaTileEntities.HEU_HOLDERS))
                    .where('X', selfPredicate())
                    .where('#', any());
            case 7 -> FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT)
                    .aisle("#IIXI#", "#IIII#", "#IIII#", "#IIII#", "#IIII#", "#IIII#")
                    .aisle("CCCCCC", "GEEEEG", "GEEEEG", "GEEEEG", "GEEEEG", "CCCCCC")
                    .aisle("CCCCCC", "GPPPPG", "GPPPPG", "GPPPPG", "GPPPPG", "CCCCCC").setRepeatable(4, 16)
                    .aisle("CCCCCC", "GEEEEG", "GEEEEG", "GEEEEG", "GEEEEG", "CCCCCC")
                    .aisle("#IIII#", "#IIII#", "#IIII#", "#IIII#", "#IIII#", "#IIII#")
                    .where('I', stateIndex(0).setMinGlobalLimited(12).or(autoAbilities(true, false))
                            .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setPreviewCount(2))
                            .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(2)))
                    .where('C', stateIndex(1))
                    .where('G', stateIndex(2))
                    .where('E', metaTileEntities(GTCEFuCMetaTileEntities.HEU_ENDPOINTS))
                    .where('P', metaTileEntities(GTCEFuCMetaTileEntities.HEU_HOLDERS))
                    .where('X', selfPredicate())
                    .where('#', any());
            case 8 -> FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT)
                    .aisle("#IIXII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#")
                    .aisle("CCCCCCC", "GEEEEEG", "GEEEEEG", "GEEEEEG", "GEEEEEG", "GEEEEEG", "CCCCCCC")
                    .aisle("CCCCCCC", "GPPPPPG", "GPPPPPG", "GPPPPPG", "GPPPPPG", "GPPPPPG", "CCCCCCC")
                    .setRepeatable(4, 16)
                    .aisle("CCCCCCC", "GEEEEEG", "GEEEEEG", "GEEEEEG", "GEEEEEG", "GEEEEEG", "CCCCCCC")
                    .aisle("#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#", "#IIIII#")
                    .where('I', stateIndex(0).setMinGlobalLimited(12).or(autoAbilities(true, false))
                            .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setPreviewCount(2))
                            .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(2)))
                    .where('C', stateIndex(1))
                    .where('G', stateIndex(2))
                    .where('E', metaTileEntities(GTCEFuCMetaTileEntities.HEU_ENDPOINTS))
                    .where('P', metaTileEntities(GTCEFuCMetaTileEntities.HEU_HOLDERS))
                    .where('X', selfPredicate())
                    .where('#', any());
        }).build();
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
                this.isActive() || !this.isWorkingEnabled(), this.isWorkingEnabled());
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

    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public boolean isActive() {
        return lastActive && isWorkingEnabled;
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(this.lastActive);
        buf.writeBoolean(this.isWorkingEnabled);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.lastActive = buf.readBoolean();
        this.isWorkingEnabled = buf.readBoolean();
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
        if (this.lastActive != active) {
            this.lastActive = active;
            markDirty();
            if (getWorld() != null && !getWorld().isRemote) {
                writeCustomData(GregtechDataCodes.IS_WORKING, buf -> buf.writeBoolean(lastActive));
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

    public static class HEUGridHandler {

        private final MetaTileEntityHeatExchanger controller;

        // private final Set<IHEUComponent> components = new ObjectOpenHashSet<>();
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
        private int cachedLength;
        private int recipeTime;
        private int recipeProgress;
        private FluidStack fluidAInitial;
        private FluidStack fluidAFinal;
        private long fluidAThermalEnergy;
        private FluidStack fluidBInitial;
        private FluidStack fluidBFinal;
        private long fluidBThermalEnergy;
        private int requiredPipeLength;

        public HEUGridHandler(MetaTileEntityHeatExchanger controller) {
            this.controller = controller;
        }

        public void onStructureForm(Collection<IHEUComponent> components) {
            resetStructure();
            resetRecipe();
            this.validGrid = true;
            for (IHEUComponent component : components) {
                // this.components.add(component);
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
                            invalidateStructure("gtcefucontent.multiblock.heat_exchanger.display.error.type");
                }
                // check for internal piping
                if (!component.hasValidPiping()) {
                    invalidateStructure("gtcefucontent.multiblock.heat_exchanger.display.error.fill");
                } else {
                    Material material = component.getPipeMaterial();
                    if (material != null) {
                        FluidPipeProperties properties = material.getProperty(PropertyKey.FLUID_PIPE);
                        if (properties != null) {
                            if (this.pipeProperty == null) {
                                this.pipeProperty = properties;
                                this.pipeVolModifier = (int) Math.sqrt(properties.getThroughput());
                            } else if (properties != pipeProperty)
                                invalidateStructure("gtcefucontent.multiblock.heat_exchanger.display.error.conflict");
                        } else
                            invalidateStructure("gtcefucontent.multiblock.heat_exchanger.display.error.material");
                    } else
                        invalidateStructure("gtcefucontent.multiblock.heat_exchanger.display.error.material");
                }
            }
            if (this.validGrid) {
                // run endpoint check
                advancedEndpointValidityCheck();
                if (!this.validGrid) return;
                // fix pipe length
                this.pipeLength /= this.controller.hEUCount;
                // 2/3 processing time if the exchanger uses conductive piping
                this.durationModifier = (this.pipeHolderVariant == IHEUComponent.HEUComponentType.H_CONDUCTIVE ? 2 :
                        3) / 3D;
                // increase recipe time based on actual pipe length and reflection count
                this.durationModifier *= (this.reflectionCount + 1) * Math.sqrt(this.pipeLength);
            }
        }

        private void advancedEndpointValidityCheck() {
            // no need to proceed if we have no returning endpoints
            if (this.reflectingEndpoints.size() == 0) return;

            double reflectraw = this.reflectingEndpoints.size() / (double) this.controller.hEUCount;
            this.reflectionCount = (int) reflectraw;
            if (reflectionCount != reflectraw) {
                // bad reflective endpoint count, no need for complex examination
                invalidateStructure("gtcefucontent.multiblock.heat_exchanger.display.error.endpoints");
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
                    invalidateStructure("gtcefucontent.multiblock.heat_exchanger.display.error.endpoints");
            }
        }

        private boolean checkPosAndNarrowAxis(BlockPos pos1, BlockPos pos2) {
            if (pos1.getX() != pos2.getX()) validAxi[0] = false;
            if (pos1.getY() != pos2.getY()) validAxi[1] = false;
            if (pos1.getZ() != pos2.getZ()) validAxi[2] = false;
            // if we've run out of valid axi, then the check is failed
            return validAxi[0] || validAxi[1] || validAxi[2];
        }

        private void invalidateStructure(String reason) {
            this.validGrid = false;
            resetStructure();
            this.invalidReason = reason;
        }

        private void invalidateRecipe(String reason) {
            this.validRecipe = false;
            resetRecipe();
            this.invalidReason = reason;
        }

        private void onStructureInvalidate() {
            resetStructure();
            resetRecipe();
            clearCache();
        }

        private void resetStructure() {
            // this.components.clear();
            this.validGrid = false;
            this.pipeHolderVariant = null;
            this.reflectionCount = 0;
            this.pipeLength = 0;
            this.pipeProperty = null;
            this.pipeVolModifier = 0;
            this.durationModifier = 0;
            this.thermalEnergy = 0;
            this.recipeTime = 0;
            this.invalidReason = "";
        }

        private void resetRecipe() {
            this.validRecipe = false;
            this.fluidAInitial = null;
            this.fluidAFinal = null;
            this.fluidAThermalEnergy = 0;
            this.fluidBInitial = null;
            this.fluidBFinal = null;
            this.fluidBThermalEnergy = 0;
            this.requiredPipeLength = 0;
            this.invalidReason = "";
        }

        public void tick() {
            if (checkRecipeValidity()) {
                this.controller.setActive(true);
                if (this.recipeProgress >= this.recipeTime) {
                    this.recipeProgress = 0;
                    // try to process fluid A
                    FluidStack fluidAInitialAdj = new FluidStack(this.fluidAInitial,
                            this.fluidAInitial.amount * this.pipeVolModifier * this.controller.hEUCount);
                    FluidStack fluidAFinalAdj = new FluidStack(this.fluidAFinal,
                            this.fluidAFinal.amount * this.pipeVolModifier * this.controller.hEUCount);

                    FluidStack fill = this.controller.inputFluidInventory.drain(fluidAInitialAdj, false);
                    if (fill != null && fill.amount == fluidAInitialAdj.amount) {
                        this.controller.inputFluidInventory.drain(fluidAInitialAdj, true);
                        this.controller.outputFluidInventory.fill(fluidAFinalAdj, true);
                        this.thermalEnergy += fluidAThermalEnergy;
                    } else this.resetRecipe();
                }
                this.recipeProgress++;

                // try to process fluid B
                int i = (int) (this.thermalEnergy / (3 * this.fluidBThermalEnergy)) + 1;
                if (this.thermalEnergy >= this.fluidBThermalEnergy) {
                    this.thermalEnergy -= this.fluidBThermalEnergy;
                    FluidStack fluidBInitialAdj = new FluidStack(this.fluidBInitial,
                            this.fluidBInitial.amount * i * this.pipeVolModifier * this.controller.hEUCount);
                    FluidStack fluidBFinalAdj = new FluidStack(this.fluidBFinal,
                            this.fluidBFinal.amount * i * this.pipeVolModifier * this.controller.hEUCount);
                    FluidStack fill = this.controller.inputFluidInventory.drain(fluidBInitialAdj, false);
                    if (fill != null && fill.amount == fluidBInitialAdj.amount) {
                        this.controller.inputFluidInventory.drain(fluidBInitialAdj, true);
                        this.controller.outputFluidInventory.fill(fluidBFinalAdj, true);
                    } else this.resetRecipe();
                }
            } else {
                this.controller.setActive(false);
                clearCache();
                // clear our stored thermal energy when stopped
                thermalEnergy = 0;
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
                // check to make sure we have enough fluid to do another iteration
                if (!(hasFluidAmount(this.fluidAInitial.getFluid(),
                        this.fluidAInitial.amount * this.pipeVolModifier * this.controller.hEUCount) &&
                        hasFluidAmount(this.fluidBInitial.getFluid(),
                                this.fluidBInitial.amount * this.pipeVolModifier * this.controller.hEUCount))) {
                    // kill the recipe if we don't have enough stuff
                    this.resetRecipe();
                }
            } else {
                // figure out if there is an exchange we can do
                Map<Fluid, Tuple<FluidStack, long[]>> cooling_map = HeatExchangerRecipeHandler.getCoolingMapCopy();
                Map<Fluid, Tuple<FluidStack, long[]>> heating_map = HeatExchangerRecipeHandler.getHeatingMapCopy();
                Set<Map.Entry<Fluid, Integer>> coolables = tankFluids.entrySet().stream()
                        .filter(fluidIntegerEntry -> cooling_map.containsKey(fluidIntegerEntry.getKey()))
                        .collect(Collectors.toSet());
                Set<Map.Entry<Fluid, Integer>> heatables = tankFluids.entrySet().stream()
                        .filter(fluidIntegerEntry -> heating_map.containsKey(fluidIntegerEntry.getKey()))
                        .collect(Collectors.toSet());
                // iterate through coolables and heatables to see if we have a valid recipe
                Tuple<int[], FluidStack[]> exchangeData = null;
                for (Map.Entry<Fluid, Integer> coolable : coolables) {
                    for (Map.Entry<Fluid, Integer> heatable : heatables) {
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
            }
            return this.validRecipe;
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
        }

        // modified code from TileEntityFluidPipeTickable
        private boolean badFlowCheck(FluidStack stack) {
            Fluid fluid = stack.getFluid();

            boolean burning = this.pipeProperty.getMaxFluidTemperature() < fluid.getTemperature(stack);
            boolean leaking = !this.pipeProperty.isGasProof() && fluid.isGaseous(stack);
            boolean shattering = !this.pipeProperty.isCryoProof() &&
                    fluid.getTemperature() < IPropertyFluidFilter.CRYOGENIC_TEMPERATURE_THRESHOLD;
            boolean corroding = false;
            boolean melting = false;

            if (fluid instanceof MaterialFluid materialFluid) {
                corroding = !this.pipeProperty.isAcidProof() && materialFluid.getFluidType().equals(FluidTypes.ACID);
                melting = !this.pipeProperty.isPlasmaProof() && materialFluid.getFluidType().equals(FluidTypes.PLASMA);

                // carrying plasmas which are too hot when plasma proof does not burn pipes
                if (burning && this.pipeProperty.isPlasmaProof() &&
                        materialFluid.getFluidType().equals(FluidTypes.PLASMA))
                    burning = false;
            }

            return burning || leaking || corroding || shattering || melting;
        }

        private boolean hasFluidAmount(Fluid fluid, int amount) {
            Map<Fluid, Integer> tankFluids = getTankFluids();
            return tankFluids.containsKey(fluid) && tankFluids.get(fluid) >= amount;
        }

        private void recalcuateRecipeDuration() {
            // correction so that the analogous operation would process the same amount of thermal energy per second
            double floatingRecipeTime = (double) this.fluidAThermalEnergy / this.fluidBThermalEnergy;
            floatingRecipeTime = (1 + floatingRecipeTime) / 2;
            this.recipeTime = (int) (3 * floatingRecipeTime * durationModifier);
        }

        private Map<Fluid, Integer> getTankFluids() {
            Map<Fluid, Integer> fluidMap = new HashMap<>();
            FluidStack stack;
            for (IFluidTankProperties tank : this.controller.inputFluidInventory.getTankProperties()) {
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

        private void cacheValues() {
            this.cachedLength = this.requiredPipeLength;
        }

        private void clearCache() {
            this.cachedLength = 0;
        }

        public void addInfo(List<ITextComponent> textList) {
            if (validGrid) {
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_exchanger.display.info",
                        this.pipeLength, this.pipeVolModifier,
                        Math.floor(this.durationModifier * 100) / 100));
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_exchanger.display.info.energy",
                        this.thermalEnergy / 1000));
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
        }

        public void addErrors(List<ITextComponent> textList) {
            if (!validGrid) {
                textList.add(new TextComponentTranslation("gtcefucontent.multiblock.heat_exchanger.display.error"));
                textList.add(new TextComponentTranslation(invalidReason));
            }
        }
    }
}
