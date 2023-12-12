package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;
import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockAdvancedCasing;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;

import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;
import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.*;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.logic.OverclockingLogic;
import gregtech.api.recipes.recipeproperties.FusionEUToStartProperty;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.BlockInfo;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.blocks.*;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityEnergyHatch;

// cursed child of a power substation and a fusion reactor
public class MetaTileEntityAntimatterCompressor extends RecipeMapMultiblockController
                                                implements IProgressBarMultiblock {

    protected EnergyContainerList inputEnergyContainers;
    protected long heat = 0;
    protected int batteryCount;

    protected long energyLoaded = 0;

    protected static final String PMC_BATTERY_HEADER = "PSSBattery_";

    public MetaTileEntityAntimatterCompressor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTCEFuCRecipeMaps.ANTIMATTER_COMPRESSOR_RECIPES);
        this.recipeMapWorkable = new AntimatterCompressorRecipeLogic(this);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.DOWN)
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "######SIS######",
                        "######III######",
                        "######SIS######",
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
                        "###############",
                        "######VVV######",
                        "#####VPPPV#####",
                        "#####VPPPV#####",
                        "#####VPPPV#####",
                        "######VVV######",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###N#######N###",
                        "###############",
                        "######ZZZ######",
                        "#####ZAAAZ#####",
                        "#####ZAPAZ#####",
                        "#####ZAAAZ#####",
                        "######ZXZ######",
                        "###############",
                        "###N#######N###",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###N#######N###",
                        "###############",
                        "######ZZZ######",
                        "#####ZAAAZ#####",
                        "#####ZAPAZ#####",
                        "#####ZAAAZ#####",
                        "######ZZZ######",
                        "###############",
                        "###N#######N###",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "##FFFTTTTTFFF##",
                        "##FNFTTTTTFNF##",
                        "##FFF#####FFF##",
                        "##TT##VVV##TT##",
                        "##TT#VPPPV#TT##",
                        "##TT#VPPPV#TT##",
                        "##TT#VPPPV#TT##",
                        "##TT##VVV##TT##",
                        "##FFF#####FFF##",
                        "##FNFTTTTTFNF##",
                        "##FFFTTTTTFFF##",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "##I#########I##",
                        "#IUU#######UUI#",
                        "##UN#######NU##",
                        "###############",
                        "###############",
                        "######SIS######",
                        "######IPI######",
                        "######SIS######",
                        "###############",
                        "###############",
                        "##UN#######NU##",
                        "#IUU#######UUI#",
                        "##I#########I##",
                        "###############")
                .aisle(
                        "###############",
                        "#IU#########UI#",
                        "#U###########U#",
                        "###N#######N###",
                        "###############",
                        "###############",
                        "###############",
                        "#######P#######",
                        "###############",
                        "###############",
                        "###############",
                        "###N#######N###",
                        "#U###########U#",
                        "#IU#########UI#",
                        "###############")
                .aisle(
                        "###############",
                        "#UU#########UU#",
                        "#U###########U#",
                        "###############",
                        "#####INNNI#####",
                        "####INGGGNI####",
                        "####NGGIGGN####",
                        "####NGIPIGN####",
                        "####NGGIGGN####",
                        "####INGGGNI####",
                        "#####INNNI#####",
                        "###############",
                        "#U###########U#",
                        "#UU#########UU#",
                        "###############")
                .aisle(
                        "###############",
                        "#UU#########UU#",
                        "#UCC#######CCU#",
                        "##C#########C##",
                        "####INGGGNI####",
                        "####NAAAAAN####",
                        "####GAAAAAG####",
                        "####GAAPAAG####",
                        "####GAAAAAG####",
                        "####NAAAAAN####",
                        "####INGGGNI####",
                        "##C#########C##",
                        "#UCC#######CCU#",
                        "#UU#########UU#",
                        "###############")
                .aisle(
                        "#II#########II#",
                        "IIII#######IIII",
                        "IICCC#####CCCII",
                        "#IC#########CI#",
                        "##C#NGGGGGN#C##",
                        "####GAAAAAG####",
                        "####GANNNAG####",
                        "####GANPNAG####",
                        "####GANNNAG####",
                        "####GAAAAAG####",
                        "##C#NGGGGGN#C##",
                        "#IC#########CI#",
                        "IICCC#####CCCII",
                        "IIII#######IIII",
                        "#II#########II#")
                .aisle(
                        "###############",
                        "#VV#########VV#",
                        "#VCC#######CCV#",
                        "##C#########C##",
                        "####NGGGGGN####",
                        "####GAAAAAG####",
                        "####GANNNAG####",
                        "####GANANAG####",
                        "####GANNNAG####",
                        "####GAAAAAG####",
                        "####NGGGGGN####",
                        "##C#########C##",
                        "#VCC#######CCV#",
                        "#VV#########VV#",
                        "###############")
                .aisle(
                        "#II#########II#",
                        "IIII#######IIII",
                        "IICCC#####CCCII",
                        "#IC#########CI#",
                        "##C#NGGGGGN#C##",
                        "####GAAAAAG####",
                        "####GANNNAG####",
                        "####GANPNAG####",
                        "####GANNNAG####",
                        "####GAAAAAG####",
                        "##C#NGGGGGN#C##",
                        "#IC#########CI#",
                        "IICCC#####CCCII",
                        "IIII#######IIII",
                        "#II#########II#")
                .aisle(
                        "###############",
                        "#UU#########UU#",
                        "#UCC#######CCU#",
                        "##C#########C##",
                        "####INGGGNI####",
                        "####NAAAAAN####",
                        "####GAAAAAG####",
                        "####GAAPAAG####",
                        "####GAAAAAG####",
                        "####NAAAAAN####",
                        "####INGGGNI####",
                        "##C#########C##",
                        "#UCC#######CCU#",
                        "#UU#########UU#",
                        "###############")
                .aisle(
                        "###############",
                        "#UU#########UU#",
                        "#U###########U#",
                        "###############",
                        "#####INNNI#####",
                        "####INGGGNI####",
                        "####NGGIGGN####",
                        "####NGIPIGN####",
                        "####NGGIGGN####",
                        "####INGGGNI####",
                        "#####INNNI#####",
                        "###############",
                        "#U###########U#",
                        "#UU#########UU#",
                        "###############")
                .aisle(
                        "###############",
                        "#IU#########UI#",
                        "#U###########U#",
                        "###N#######N###",
                        "###############",
                        "###############",
                        "###############",
                        "#######P#######",
                        "###############",
                        "###############",
                        "###############",
                        "###N#######N###",
                        "#U###########U#",
                        "#IU#########UI#",
                        "###############")
                .aisle(
                        "###############",
                        "##I#########I##",
                        "#IUU#######UUI#",
                        "##UN#######NU##",
                        "###############",
                        "###############",
                        "######SIS######",
                        "######IPI######",
                        "######SIS######",
                        "###############",
                        "###############",
                        "##UN#######NU##",
                        "#IUU#######UUI#",
                        "##I#########I##",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "##FFFTTTTTFFF##",
                        "##FNFTTTTTFNF##",
                        "##FFF#####FFF##",
                        "##TT##YYY##TT##",
                        "##TT#YDDDY#TT##",
                        "##TT#YDPDY#TT##",
                        "##TT#YDDDY#TT##",
                        "##TT##YYY##TT##",
                        "##FFF#####FFF##",
                        "##FNFTTTTTFNF##",
                        "##FFFTTTTTFFF##",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###N#######N###",
                        "###############",
                        "######LLL######",
                        "#####LBBBL#####",
                        "#####LBPBL#####",
                        "#####LBBBL#####",
                        "######LLL######",
                        "###############",
                        "###N#######N###",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###N#######N###",
                        "###############",
                        "######LLL######",
                        "#####LBBBL#####",
                        "#####LBPBL#####",
                        "#####LBBBL#####",
                        "######LLL######",
                        "###############",
                        "###N#######N###",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "######YYY######",
                        "#####YDDDY#####",
                        "#####YDPDY#####",
                        "#####YDDDY#####",
                        "######YYY######",
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
                        "###############",
                        "###############",
                        "######SIS######",
                        "######III######",
                        "######SIS######",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############")
                .where('S', stateIndex(0))
                .where('I', stateIndex(1))
                .where('P', stateIndex(2))
                .where('N', stateIndex(3))
                .where('V', stateIndex(4))
                .where('F', stateIndex(5))
                .where('G', stateIndex(6))
                .where('C', stateIndex(7))
                .where('D', stateIndex(8))
                .where('L', stateIndex(9))
                .where('B', stateIndex(2).or(BATTERY_PREDICATE.get().setPreviewCount(8)))
                .where('T', frames(Materials.Steel))
                .where('U', frames(Materials.Neutronium))
                .where('Z',
                        stateIndex(0).setMinGlobalLimited(12)
                                .or(autoAbilities(false, true, true, true, true, false, false)))
                .where('Y', stateIndex(8).setMinGlobalLimited(8)
                        .or(metaTileEntities(MetaTileEntities.ENERGY_INPUT_HATCH))
                        .or(metaTileEntities(MetaTileEntities.ENERGY_INPUT_HATCH_4A))
                        .or(metaTileEntities(MetaTileEntities.ENERGY_INPUT_HATCH_16A).setPreviewCount(16)))
                .where('X', selfPredicate())
                .where('A', air())
                .where('#', any())
                .build();
    }

    protected TraceabilityPredicate stateIndex(int id) {
        return states(switch (id) {
            default -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
            case 1 -> GTCEFuCMetaBlocks.HARDENED_CASING
                    .getState(GTCEFuCBlockHardenedCasing.CasingType.INDESTRUCTIBLE_CASING);
            case 2 -> GTCEFuCMetaBlocks.HARDENED_CASING
                    .getState(GTCEFuCBlockHardenedCasing.CasingType.INDESTRUCTIBLE_PIPE_CASING);
            case 3 -> GTCEFuCMetaBlocks.ADVANCED_CASING
                    .getState(GTCEFuCBlockAdvancedCasing.AdvancedCasingType.NULL_FIELD_CASING);
            case 4 -> GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT);
            case 5 -> MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
            case 6 -> MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
            case 7 -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                    .getState(BlockLargeMultiblockCasing.CasingType.ATOMIC_CASING);
            case 8 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.PALLADIUM_SUBSTATION);
            case 9 -> MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.LAMINATED_GLASS);
            case 10 -> MetaBlocks.BATTERY_BLOCK.getState(BlockBatteryPart.BatteryPartType.LAPOTRONIC_UV);
        });
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        if (iMultiblockPart instanceof MetaTileEntityEnergyHatch) return Textures.PALLADIUM_SUBSTATION_CASING;
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.COMPRESSOR_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityAntimatterCompressor(this.metaTileEntityId);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("gtcefucontent.machine.antimatter_compressor.info"));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @Nonnull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtcefucontent.machine.antimatter_compressor.capacity",
                calculateEnergyStorageFactor(16) / 1000000000L));
        tooltip.add(TooltipHelper.RAINBOW_SLOW + I18n.format("gregtech.machine.perfect_oc"));
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        long energyStored = this.energyContainer.getEnergyStored();
        super.formStructure(context);

        // we're just faking the battery storage, we don't need to interact with the actual battery data ever.
        int count = 0;
        for (Map.Entry<String, Object> battery : context.entrySet()) {
            if (battery.getKey().startsWith("PSSBattery_") &&
                    battery.getValue() instanceof BatteryMatchWrapper wrapper) {
                count += wrapper.amount;
            }
        }
        this.batteryCount = count;

        this.initializeAbilities();
        // Make sure we also fill up with energy from the NBT on world load
        ((EnergyContainerHandler) this.energyContainer).setEnergyStored(energyStored + energyLoaded);
        energyLoaded = 0;
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
        this.batteryCount = 0;
        this.inputEnergyContainers = new EnergyContainerList(Lists.newArrayList());
        this.heat = 0;
    }

    @Override
    protected void initializeAbilities() {
        this.inputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.inputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
        this.outputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        List<IEnergyContainer> energyInputs = getAbilities(MultiblockAbility.INPUT_ENERGY);
        this.inputEnergyContainers = new EnergyContainerList(energyInputs);
        long euCapacity = calculateEnergyStorageFactor(this.batteryCount);
        // allow for adaptive max voltage
        this.energyContainer = new EnergyContainerHandler(this, euCapacity, inputEnergyContainers.getInputVoltage(), 0,
                0, 0) {

            @Nonnull
            @Override
            public String getName() {
                return GregtechDataCodes.FUSION_REACTOR_ENERGY_CONTAINER_TRAIT;
            }
        };
    }

    private long calculateEnergyStorageFactor(int energyInputAmount) {
        return energyInputAmount * 16 * 640000000L;
    }

    @Override
    protected void updateFormedValid() {
        if (this.inputEnergyContainers.getEnergyStored() > 0 && getTrueEnergyContainerSpace() > 0) {
            long energyAdded = Math.min(this.inputEnergyContainers.getEnergyStored(), getTrueEnergyContainerSpace());
            energyAdded = this.energyContainer.addEnergy(energyAdded);
            if (energyAdded > 0) this.inputEnergyContainers.removeEnergy(energyAdded);
        }
        super.updateFormedValid();
    }

    private long getTrueEnergyContainerSpace() {
        // We want to try and always keep space for the energy refund from shutting the compressor down
        return (energyContainer.getEnergyCapacity() - heat * 6 / 10) - energyContainer.getEnergyStored();
    }

    // copied and modified a little from substation code, it was protected and I don't like reflection.
    protected static final Supplier<TraceabilityPredicate> BATTERY_PREDICATE = () -> new TraceabilityPredicate(
            blockWorldState -> {
                IBlockState state = blockWorldState.getBlockState();
                if (GregTechAPI.PSS_BATTERIES.containsKey(state)) {
                    IBatteryData battery = GregTechAPI.PSS_BATTERIES.get(state);
                    // Allow only UV batteries
                    if (battery.getTier() == GTValues.UV) {
                        String key = PMC_BATTERY_HEADER + battery.getBatteryName();
                        BatteryMatchWrapper wrapper = blockWorldState.getMatchContext().get(key);
                        if (wrapper == null) wrapper = new BatteryMatchWrapper();
                        blockWorldState.getMatchContext().set(key, wrapper.increment());
                        return true;
                    }
                }
                return false;
            }, () -> GregTechAPI.PSS_BATTERIES.entrySet().stream()
                    .filter((entry) -> entry.getValue().getTier() == GTValues.UV)
                    .map(entry -> new BlockInfo(entry.getKey(), null))
                    .toArray(BlockInfo[]::new));

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

    protected class AntimatterCompressorRecipeLogic extends MultiblockRecipeLogic {

        public AntimatterCompressorRecipeLogic(MetaTileEntityAntimatterCompressor tileEntity) {
            super(tileEntity);
        }

        @Override
        protected double getOverclockingDurationDivisor() {
            return OverclockingLogic.PERFECT_OVERCLOCK_DURATION_DIVISOR;
        }

        @Override
        public void updateWorkable() {
            super.updateWorkable();
            // Empty heat when the compressor is stopped. The recipe will have to be reignited.
            // However, heat will be refunded at 50% effect power to the energy container, if it has space.
            if ((!isActive || hasNotEnoughEnergy) && heat > 0) {
                energyContainer.addEnergy(Math.min(energyContainer.getEnergyCanBeInserted(), heat / 2));
                heat = 0;
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
            tag.setLong("Energy", energyContainer.getEnergyStored());
            return tag;
        }

        @Override
        public void deserializeNBT(@Nonnull NBTTagCompound compound) {
            super.deserializeNBT(compound);
            heat = compound.getLong("Heat");
            energyLoaded = compound.getLong("Energy");
        }
    }

    // copied from substation code, it was private and I don't like reflection.
    private static class BatteryMatchWrapper {

        private int amount;

        public BatteryMatchWrapper() {}

        public MetaTileEntityAntimatterCompressor.BatteryMatchWrapper increment() {
            amount++;
            return this;
        }
    }

    public static void init() {
        // oh boy, very large numbers eh?
        // I really should've created my own stuff instead of just using fusion logic, this is bad for compatibility,
        // but whatever.
        FusionEUToStartProperty.registerFusionTier(12, "Antimatter");
        FusionEUToStartProperty.registerFusionTier(13, "Antimatter");
        FusionEUToStartProperty.registerFusionTier(14, "Antimatter");
        FusionEUToStartProperty.registerFusionTier(15, "Antimatter");
        FusionEUToStartProperty.registerFusionTier(16, "Antimatter");
    }
}
