package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

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
import gregtech.api.capability.impl.FluidTankList;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetaTileEntityMegaSteamEngine extends FuelMultiblockController {

    protected boolean validVCU;

    public MetaTileEntityMegaSteamEngine(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.STEAM_TURBINE_FUELS, GTValues.OpV);
        this.recipeMapWorkable = new MSEMultiblockFuelRecipeLogic(this);
        this.recipeMapWorkable.setMaximumOverclockVoltage(GTValues.V[GTValues.OpV]);
        this.validVCU = false;
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
            default -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.STEAM_CASING);
            case 1 -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.VIBRATION_SAFE_CASING);
            case 2 -> GTCEFuCMetaBlocks.HARDENED_CASING.getState(GTCEFuCBlockHardenedCasing.CasingType.PRESSURE_CASING);
            case 3 -> MetaBlocks.COMPUTER_CASING.getState(BlockComputerCasing.CasingType.HIGH_POWER_CASING);
            case 4 -> GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.MOLYBDENUM_DISILICIDE_COIL);
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
    public void invalidateStructure() {
        super.invalidateStructure();
        this.validVCU = false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gcym.machine.steam_engine.tooltip.1", GTValues.VNF[GTValues.OpV]));
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
        MSEMultiblockFuelRecipeLogic recipeLogic = (MSEMultiblockFuelRecipeLogic) recipeMapWorkable;
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

    protected static class MSEMultiblockFuelRecipeLogic extends MultiblockFuelRecipeLogic {
        public MSEMultiblockFuelRecipeLogic(MetaTileEntityMegaSteamEngine tileEntity) {
            super(tileEntity);
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
                        new LongFluidStack(recipe.getFluidInputs().get(0).getInputFluidStack().getFluid(), Long.MAX_VALUE),
                        false);
            }
            FluidStack fuelStack = previousRecipe.getFluidInputs().get(0).getInputFluidStack();
            return getInputTank().drain(new LongFluidStack(fuelStack.getFluid(), Long.MAX_VALUE), false);
        }

        @Override
        protected ILongMultipleTankHandler getInputTank() {
            MetaTileEntityMegaSteamEngine controller = (MetaTileEntityMegaSteamEngine) metaTileEntity;
            return controller.getInputFluidInventory();
        }

        @Override
        protected boolean canProgressRecipe() {
            if (metaTileEntity instanceof MetaTileEntityMegaSteamEngine engine)
                return engine.hasValidVCU() && super.canProgressRecipe();
            else return false;
        }
    }
}
