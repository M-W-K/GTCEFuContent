package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import java.util.ArrayList;
import java.util.List;

import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import gregtech.api.GregTechAPI;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.resources.TextureArea;
import gregtech.common.ConfigHolder;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.m_w_k.gtcefucontent.api.longhelp.ILongMultipleTankHandler;
import com.m_w_k.gtcefucontent.api.longhelp.LongFluidStack;
import com.m_w_k.gtcefucontent.api.longhelp.LongFluidTankList;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;

import gregicality.multiblocks.api.metatileentity.GCYMMultiblockAbility;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;
import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.IRotorHolder;
import gregtech.api.capability.impl.MultiblockFuelRecipeLogic;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.*;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityEnergyHatch;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMufflerHatch;

public class MetaTileEntityMegaSteamEngine extends FuelMultiblockController implements IProgressBarMultiblock {

    protected boolean validVCU;
    protected long kineticEnergy;
    // the larger the shaft mass, the slower the engine will ramp up
    protected final int shaftMass;
    // the amount of kinetic energy to lose to friction every tick, half before and half after energy extraction
    protected final int friction;
    // the amount of power to generate per RPM
    protected final int generatorStrength;

    public MetaTileEntityMegaSteamEngine(ResourceLocation metaTileEntityId, int shaftMass, int friction, int generatorStrength) {
        super(metaTileEntityId, RecipeMaps.STEAM_TURBINE_FUELS, GTValues.OpV);
        this.recipeMapWorkable = new MSEMultiblockWorkableHandler(this);
        this.recipeMapWorkable.setMaximumOverclockVoltage(GTValues.V[GTValues.OpV]);
        this.validVCU = false;
        this.shaftMass = shaftMass;
        this.friction = friction;
        this.generatorStrength = generatorStrength;
    }

    public MetaTileEntityMegaSteamEngine(ResourceLocation metaTileEntityID) {
        this(metaTileEntityID, 100000, (int) GTValues.V[GTValues.ZPM], (int) GTValues.V[GTValues.ZPM]);
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        this.inputFluidInventory = new LongFluidTankList(allowSameFluidFillForOutputs(),
                getAbilities(MultiblockAbility.IMPORT_FLUIDS));
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMegaSteamEngine(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.FRONT, RelativeDirection.UP, RelativeDirection.RIGHT)
                .aisle("#####VV#####", "####VVVV####", "####VVVV####", "#####VV#####")
                .aisle("###HHVVHH###", "###YVTTVY###", "###YVTTVY###", "###HHVVHH###")
                .aisle("####CVVC####", "####CAAC####", "####CAAC####", "####CVVC####")
                .aisle("###HHVVHH###", "###YVTTVY###", "###YVTTVY###", "###HHVVHH###")
                .aisle("####CVVC####", "####CAAC####", "####CAAC####", "####CVVC####")
                .aisle("###HHVVHH###", "###YVTTVY###", "###YVTTVY###", "###HHVVHH###")
                .aisle("####CVVC####", "####CAAC####", "####CAAC####", "####CVVC####")
                .aisle("###HHVVHH###", "###YVTTVY###", "###YVTTVY###", "###HHVVHH###")
                .aisle("#####VV#####", "####VTTV####", "####VTTV####", "#####VV#####")
                .aisle("SS###VV###SS", "SS##VTTV##SS", "####VTTV####", "#####VV#####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####VVVV####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####MVVM####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####VVVV####")
                .aisle("SS###VV###SS", "SS##VTTV##SS", "####VTTV####", "#####VV#####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####VVVV####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####MVVM####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####VVVV####")
                .aisle("SS###VV###SS", "XS##VTTV##SU", "####VTTV####", "#####VV#####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####VVVV####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####MVVM####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####VVVV####")
                .aisle("SS###VV###SS", "SS##VTTV##SS", "####VTTV####", "#####VV#####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####VVVV####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####MVVM####")
                .aisle("SS##VVVV##SS", "SPPPPTTPPPPS", "#PPPPTTPPPP#", "####VVVV####")
                .aisle("SS###VV###SS", "SS##VTTV##SS", "####VTTV####", "#####VV#####")
                .aisle("#####VV#####", "####VVVV####", "####VVVV####", "#####VV#####")
                .where('V', stateIndex(1))
                .where('T', stateIndex(6))
                .where('H', stateIndex(3))
                .where('Y', stateIndex(3).or(abilities(MultiblockAbility.OUTPUT_ENERGY).setPreviewCount(16)))
                .where('A', stateIndex(5))
                .where('C', stateIndex(4))
                .where('S', stateIndex(0).setMinGlobalLimited(100)
                        .or(autoAbilities(false, true, false, false, true, true, false)))
                .where('P', stateIndex(2))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('U', abilities(GCYMMultiblockAbility.TIERED_HATCH))
                .where('X', selfPredicate())
                .where('#', any())
                .build();
    }

    protected TraceabilityPredicate stateIndex(int id) {
        return states(switch (id) {
            default -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                    .getState(BlockLargeMultiblockCasing.CasingType.STEAM_CASING);
            case 1 -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                    .getState(BlockLargeMultiblockCasing.CasingType.VIBRATION_SAFE_CASING);
            case 2 -> GTCEFuCMetaBlocks.HARDENED_CASING.getState(GTCEFuCBlockHardenedCasing.CasingType.PRESSURE_CASING);
            case 3 -> MetaBlocks.COMPUTER_CASING.getState(BlockComputerCasing.CasingType.HIGH_POWER_CASING);
            case 4 -> GCYMMetaBlocks.UNIQUE_CASING
                    .getState(BlockUniqueCasing.UniqueCasingType.MOLYBDENUM_DISILICIDE_COIL);
            case 5 -> MetaBlocks.COMPRESSED.get(Materials.SamariumMagnetic).getBlock(Materials.SamariumMagnetic);
            case 6 -> MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.TUNGSTENSTEEL_GEARBOX);
        });
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        List<IEnergyContainer> dynamoHatches = getAbilities(MultiblockAbility.OUTPUT_ENERGY);
        List<ITieredMetaTileEntity> vcus = getAbilities(GCYMMultiblockAbility.TIERED_HATCH);
        if (dynamoHatches.isEmpty() || vcus.isEmpty()) {
            this.validVCU = true;
            return;
        }
        long dynamoVoltage = dynamoHatches.stream().map(IEnergyContainer::getOutputVoltage).reduce(Math::max).get();
        ITieredMetaTileEntity vcu = vcus.get(0);
        this.validVCU = GTValues.V[vcu.getTier()] >= dynamoVoltage;
    }

    @Override
    protected void updateFormedValid() {
        if (!hasMufflerMechanics() || isMufflerFaceFree()) {
            this.recipeMapWorkable.updateWorkable();
            // remove half of our friction value from the maximum output
            long generated = this.energyContainer.addEnergy(getEnergyOut() - this.friction / 2);
            this.kineticEnergy -= generated + friction;
            if (this.kineticEnergy < 0) this.kineticEnergy = 0;
        }
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.validVCU = false;
    }

    public int getNumProgressBars() {
        return 3;
    }

    @Override
    public double getFillPercentage(int index) {
        if (index == 0) {
            long[] fuelAmount = new long[2];
            if (getInputFluidInventory() != null) {
                MSEMultiblockWorkableHandler recipeLogic = (MSEMultiblockWorkableHandler) recipeMapWorkable;
                if (recipeLogic.getInputFluidStack() != null) {
                    LongFluidStack testStack = recipeLogic.getInputFluidStack().copy();
                    testStack.setAmount(Long.MAX_VALUE);
                    fuelAmount = getTotalLongFluidAmount(testStack, getInputFluidInventory());
                }
            }
            return fuelAmount[1] != 0 ? 1.0 * fuelAmount[0] / fuelAmount[1] : 0;
        } else if (index == 1) {
            return this.getRPM() / this.maxRPM();
        } else {
            // I think it's reasonable to assume we do not have more than the integer limit of lubricant
            int[] lubricantAmount = new int[2];
            if (getInputFluidInventory() != null) {
                lubricantAmount = getTotalFluidAmount(GTCEFuCMaterials.VaporSeedRaw.getFluid(Integer.MAX_VALUE),
                        getInputFluidInventory());
            }
            return lubricantAmount[1] != 0 ? 1.0 * lubricantAmount[0] / lubricantAmount[1] : 0;
        }
    }

    @Override
    public TextureArea getProgressBarTexture(int index) {
        if (index == 0) {
            return GuiTextures.PROGRESS_BAR_LCE_FUEL;
        } else if (index == 1) {
            return GuiTextures.PROGRESS_BAR_TURBINE_ROTOR_SPEED;
        } else {
            return GuiTextures.PROGRESS_BAR_LCE_LUBRICANT;
        }
    }

    public void addBarHoverText(List<ITextComponent> hoverList, int index) {
        if (index == 0) {
            addFuelText(hoverList);
        } else if (index == 1) {
            ITextComponent rpmTranslated = TextComponentUtil.translationWithColor(
                    TextFormatting.WHITE,
                    "gregtech.multiblock.turbine.rotor_rpm_unit_name");
            ITextComponent rotorInfo = TextComponentUtil.translationWithColor(
                    TextFormatting.WHITE,
                    "%s / %s %s",
                    TextFormattingUtil.formatNumbers(getRPM()),
                    TextFormattingUtil.formatNumbers(maxRPM()),
                    rpmTranslated);
            hoverList.add(TextComponentUtil.translationWithColor(
                    TextFormatting.GRAY,
                    "gtcefucontent.machine.mega_steam_engine.display.shaft_speed",
                    rotorInfo));
        } else {
            // Lubricant
            int lubricantStored = 0;
            int lubricantCapacity = 0;
            if (isStructureFormed() && getInputFluidInventory() != null) {
                // Hunt for tanks with lubricant in them
                int[] lubricantAmount = getTotalFluidAmount(GTCEFuCMaterials.VaporSeedRaw.getFluid(Integer.MAX_VALUE),
                        getInputFluidInventory());
                lubricantStored = lubricantAmount[0];
                lubricantCapacity = lubricantAmount[1];
            }

            ITextComponent lubricantInfo = TextComponentUtil.stringWithColor(
                    TextFormatting.GOLD,
                    TextFormattingUtil.formatNumbers(lubricantStored) + " / " +
                            TextFormattingUtil.formatNumbers(lubricantCapacity) + " L");
            hoverList.add(TextComponentUtil.translationWithColor(
                    TextFormatting.GRAY,
                    "gtcefucontent.machine.mega_steam_engine.display.lubricant_amount",
                    lubricantInfo));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gcym.machine.steam_engine.tooltip.1", GTValues.VNF[GTValues.OpV]));
        tooltip.add(I18n.format("gtcefucontent.machine.mega_steam_engine.tooltip.1"));
        tooltip.add(I18n.format("gtcefucontent.machine.mega_steam_engine.tooltip.2", this.generatorStrength));
        tooltip.add(I18n.format("gtcefucontent.machine.mega_steam_engine.tooltip.3", this.friction));
        tooltip.add(I18n.format("gtcefucontent.universal.tooltip.uses_per_hour_vapor_seed", 20000));
    }



    @Override
    protected void addErrorText(List<ITextComponent> textList) {
        if (!validVCU)
            textList.add(new TextComponentTranslation("gtcefucontent.machine.mega_steam_engine.error.vcu"));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        if (iMultiblockPart instanceof MetaTileEntityMufflerHatch)
            return GCYMTextures.VIBRATION_SAFE_CASING;
        else if (iMultiblockPart instanceof MetaTileEntityEnergyHatch)
            return Textures.HIGH_POWER_CASING;
        return GCYMTextures.STEAM_CASING;
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return GCYMTextures.STEAM_ENGINE_OVERLAY;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return true;
    }

    @Override
    public void runMufflerEffect(float xPos, float yPos, float zPos, float xSpd, float ySpd, float zSpd) {
        this.getWorld().spawnParticle(EnumParticleTypes.CLOUD, xPos, yPos, zPos, xSpd, ySpd, zSpd);
    }

    public boolean hasValidVCU() {
        return validVCU;
    }

    @Override
    public ILongMultipleTankHandler getInputFluidInventory() {
        return (ILongMultipleTankHandler) this.inputFluidInventory;
    }

    @Override
    protected void addFuelText(List<ITextComponent> textList) {
        // Fuel
        long fuelStored = 0;
        long fuelCapacity = 0;
        FluidStack fuelStack = null;
        MSEMultiblockWorkableHandler recipeLogic = (MSEMultiblockWorkableHandler) recipeMapWorkable;
        if (isStructureFormed() && recipeLogic.getInputFluidStack() != null && getInputFluidInventory() != null) {
            fuelStack = recipeLogic.getInputFluidStack().copy();
            fuelStack.amount = Integer.MAX_VALUE;
            long[] fuelAmount = getTotalLongFluidAmount(fuelStack, getInputFluidInventory());
            fuelStored = fuelAmount[0];
            fuelCapacity = fuelAmount[1];
        }

        if (fuelStack != null) {
            ITextComponent fuelName = TextComponentUtil.setColor(GTUtility.getFluidTranslation(fuelStack),
                    TextFormatting.GOLD);
            ITextComponent fuelInfo = new TextComponentTranslation("%s / %s L (%s)",
                    TextFormattingUtil.formatNumbers(fuelStored),
                    TextFormattingUtil.formatNumbers(fuelCapacity),
                    fuelName);
            textList.add(TextComponentUtil.translationWithColor(
                    TextFormatting.GRAY,
                    "gregtech.multiblock.large_combustion_engine.fuel_amount",
                    TextComponentUtil.setColor(fuelInfo, TextFormatting.GOLD)));
        } else {
            textList.add(TextComponentUtil.translationWithColor(
                    TextFormatting.GRAY,
                    "gregtech.multiblock.large_combustion_engine.fuel_amount",
                    "0 / 0 L"));
        }
    }

    protected long[] getTotalLongFluidAmount(FluidStack testStack, IMultipleTankHandler multiTank) {
        long fluidAmount = 0;
        long fluidCapacity = 0;
        for (var tank : multiTank) {
            if (tank != null) {
                FluidStack drainStack = tank.drain(testStack, false);
                if (drainStack != null && drainStack.amount > 0) {
                    fluidAmount += drainStack.amount;
                    fluidCapacity += tank.getCapacity();
                }
            }
        }
        return new long[] { fluidAmount, fluidCapacity };
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setLong("KE", this.kineticEnergy);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.kineticEnergy = data.getLong("KE");
    }

    protected static class MSEMultiblockWorkableHandler extends MultiblockFuelRecipeLogic {

        private static final FluidStack LUBRICANT_STACK = GTCEFuCMaterials.VaporSeedRaw.getFluid(5);

        protected MetaTileEntityMegaSteamEngine engine;

        public MSEMultiblockWorkableHandler(MetaTileEntityMegaSteamEngine tileEntity) {
            super(tileEntity);
            engine = tileEntity;
        }

        @Override
        public String getRecipeFluidInputInfo() {
            IRotorHolder rotorHolder = null;

            if (metaTileEntity instanceof MultiblockWithDisplayBase multiblockWithDisplayBase) {
                List<IRotorHolder> abilities = multiblockWithDisplayBase.getAbilities(MultiblockAbility.ROTOR_HOLDER);
                rotorHolder = abilities.size() > 0 ? abilities.get(0) : null;
            }

            // Previous Recipe is always null on first world load, so try to acquire a new recipe
            Recipe recipe;
            if (previousRecipe == null) {
                recipe = findRecipe(Integer.MAX_VALUE, getInputInventory(), getInputTank());
                if (recipe == null) return null;
            } else {
                recipe = previousRecipe;
            }
            FluidStack requiredFluidInput = recipe.getFluidInputs().get(0).getInputFluidStack();

            int ocAmount = (int) (getMaxVoltage() / recipe.getEUt());
            // longs
            long neededAmount = (long) ocAmount * requiredFluidInput.amount;
            if (rotorHolder != null && rotorHolder.hasRotor()) {
                neededAmount /= (rotorHolder.getTotalEfficiency() / 100f);
            } else if (rotorHolder != null && !rotorHolder.hasRotor()) {
                return null;
            }
            return TextFormatting.RED + TextFormattingUtil.formatNumbers(neededAmount) + "L";
        }

        @Override
        public LongFluidStack getInputFluidStack() {
            // Previous Recipe is always null on first world load, so try to acquire a new recipe
            if (previousRecipe == null) {
                Recipe recipe = findRecipe(Integer.MAX_VALUE, getInputInventory(), getInputTank());

                return recipe == null ? null : getInputTank().drain(
                        new LongFluidStack(recipe.getFluidInputs().get(0).getInputFluidStack().getFluid(),
                                Long.MAX_VALUE),
                        false);
            }
            FluidStack fuelStack = previousRecipe.getFluidInputs().get(0).getInputFluidStack();
            return getInputTank().drain(new LongFluidStack(fuelStack.getFluid(), Long.MAX_VALUE), false);
        }

        @Override
        protected ILongMultipleTankHandler getInputTank() {
            return this.engine.getInputFluidInventory();
        }

        @Override
        protected void updateRecipeProgress() {
            drainLubricant();
            super.updateRecipeProgress();
        }

        @Override
        protected boolean shouldSearchForRecipes() {
            return super.shouldSearchForRecipes() && checkLubricant();
        }

        public boolean checkLubricant() {
            // check lubricant and invalidate if it fails
            IMultipleTankHandler inputTank = engine.getInputFluidInventory();
            if (LUBRICANT_STACK.isFluidStackIdentical(inputTank.drain(LUBRICANT_STACK, false))) {
                return true;
            } else {
                invalidate();
                return false;
            }
        }

        public void drainLubricant() {
            if (totalContinuousRunningTime == 1 || totalContinuousRunningTime % 18 == 0) {
                IMultipleTankHandler inputTank = engine.getInputFluidInventory();
                inputTank.drain(LUBRICANT_STACK, true);
            }
        }

        @Override
        protected boolean canProgressRecipe() {
            return this.engine.hasValidVCU() && checkLubricant() && super.canProgressRecipe();
        }

        @Override
        protected boolean drawEnergy(int recipeEUt, boolean simulate) {
            long euToDraw = boostProduction(recipeEUt);
            long resultEnergy = this.engine.kineticEnergy - euToDraw;
            if (resultEnergy >= 0L && resultEnergy <= getEnergyCapacity()) {
                if (!simulate) this.engine.kineticEnergy -= euToDraw;
                return true;
            }
            return false;
        }

        @Override
        protected long getEnergyStored() {
            return this.engine.kineticEnergy;
        }

        @Override
        protected long getEnergyCapacity() {
            return this.engine.maxKE();
        }

        @Override
        public int getInfoProviderEUt() {
            return (int) this.engine.getEnergyOut();
        }
    }

    protected double getRPM() {
        return Math.pow(2D * this.kineticEnergy / this.shaftMass, 0.5);
    }

    protected long getEnergyOut() {
        return Math.min((long) (this.getRPM() * this.generatorStrength), this.kineticEnergy);
    }

    protected double maxRPM() {
        return this.recipeMapWorkable.getMaxVoltage() / (double) this.generatorStrength;
    }

    protected long maxKE() {
        return (long) (0.5 * this.shaftMass * maxRPM() * maxRPM());
    }
}
