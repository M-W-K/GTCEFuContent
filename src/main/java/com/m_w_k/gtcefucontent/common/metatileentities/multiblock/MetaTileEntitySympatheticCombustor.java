package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import java.util.*;
import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.common.ConfigHolder;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.GTUtility;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.*;
import gregtech.common.metatileentities.MetaTileEntities;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class MetaTileEntitySympatheticCombustor extends MultiblockWithDisplayBase implements IProgressBarMultiblock {

    protected EnergyContainerList energyContainer;
    protected long highestHatchVoltage;

    private long maxEU;
    private long remainingEU;
    private double efficiencyMult;
    private double fireEnhancerMult;
    private int fireEnhancerConsumed;

    private int throttlePercentage = 100;

    private static final int[] EMPTY = new int[] { 0, 0 };

    public MetaTileEntitySympatheticCombustor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntitySympatheticCombustor(this.metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
        for (var hatch : getAbilities(MultiblockAbility.OUTPUT_ENERGY)) {
            if (hatch.getOutputVoltage() > highestHatchVoltage) {
                highestHatchVoltage = hatch.getOutputVoltage();
            }
        }
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetTileAbilities();
        this.highestHatchVoltage = 0;
        this.throttlePercentage = 100;
        this.efficiencyMult = 1;
        this.fireEnhancerMult = 1;
        this.fireEnhancerConsumed = 0;
    }

    private void initializeAbilities() {
        this.energyContainer = new EnergyContainerList(getAbilities(MultiblockAbility.OUTPUT_ENERGY));
    }

    private void resetTileAbilities() {
        this.energyContainer = new EnergyContainerList(new ArrayList<>());
    }

    @Override
    protected void updateFormedValid() {
        if (!hasMufflerMechanics() || isMufflerFaceFree()) {
            if (doEUOutput()) consumeInputs();
        }
    }

    // I know this is a jank abomination but idc
    protected boolean doEUOutput() {
        if (this.remainingEU <= 0) return true;
        long output = Math.min(energyContainer.getOutputVoltage(), this.remainingEU) * this.throttlePercentage / 100;
        long energyOutput = this.energyContainer.addEnergy(output);
        // always consume at least 1 amp of our output voltage, affected by throttle
        this.remainingEU -= Math.max(energyOutput,
                GTValues.V[GTUtility.getFloorTierByVoltage(this.energyContainer.getOutputVoltage())] *
                        this.throttlePercentage / 100);
        this.remainingEU = Math.max(this.remainingEU, 0);
        // if the next output tick would not provide the full output, consume inputs.
        return this.remainingEU - output <= 0;
    }

    protected void consumeInputs() {
        Map<Fluid, Long> uniqueFluids = new Object2LongOpenHashMap<>();
        Map<ItemData, Long> uniqueItems = new Object2LongOpenHashMap<>();
        List<Runnable> onConsumeActions = new ObjectArrayList<>();

        List<Function<Integer, Integer>> fireEnhancerConsumers = new ObjectArrayList<>();

        for (var tank : getAbilities(MultiblockAbility.IMPORT_FLUIDS)) {
            FluidStack fluid = tank.drain(Integer.MAX_VALUE, false);
            if (fluid == null) continue;
            if (GTCEFuCMaterials.FireEnhancer.getFluid() == fluid.getFluid()) {
                fireEnhancerConsumers.add(quant -> Objects.requireNonNull(tank.drain(quant, true)).amount);
            } else {
                int[] data = determineFluidBurnability(fluid, this.highestHatchVoltage);
                // ignore fuel if the fuel's min tier is higher than our tier
                if (data[0] == 0) continue;
                uniqueFluids.merge(fluid.getFluid(), (long) data[0], Long::sum);
                onConsumeActions.add(() -> tank.drain(data[1], true));
            }
        }

        for (var bus : getAbilities(MultiblockAbility.IMPORT_ITEMS)) {
            // one contributing item variant per bus
            ItemStack item = null;
            for (int slot = 0; slot < bus.getSlots(); slot++) {
                ItemStack stack = bus.extractItem(slot, Integer.MAX_VALUE, true);
                int[] data = determineItemBurnability(stack, this.highestHatchVoltage);
                // ignore fuel if the fuel's min tier is higher than our tier
                if (data[0] == 0) continue;
                if (item == null) {
                    item = stack;
                }
                if (item.isItemEqual(stack)) {
                    uniqueItems.merge(new ItemData(item), (long) data[0], Long::sum);
                    int finalSlot = slot;
                    onConsumeActions.add(() -> bus.extractItem(finalSlot, data[1], false));
                }
            }
        }

        long eu = 0;
        for (long fluidEU : uniqueFluids.values()) {
            eu += fluidEU;
        }
        for (long itemEU : uniqueItems.values()) {
            eu += itemEU;
        }
        if (eu == 0) {
            this.efficiencyMult = 1;
            this.fireEnhancerMult = 1;
            this.fireEnhancerConsumed = 0;
            return;
        }
        // Efficiency bonus: mult by 1.1 to the power of 1 over the sum of the squares of the weights.
        // Perfectly even EU contribution results in x% multiplicative bonus per unique fuel.
        // The less even the EU contribution, the lower the bonus.
        double sumOfSquares = 0;
        for (long fluidEU : uniqueFluids.values()) {
            double weight = (double) fluidEU / eu;
            sumOfSquares += weight * weight;
        }
        for (long itemEU : uniqueItems.values()) {
            double weight = (double) itemEU / eu;
            sumOfSquares += weight * weight;
        }
        this.efficiencyMult = Math.pow(1 + (ConfigHolder.sympatheticCombustorBonus) / 100d, 1 / sumOfSquares);
        eu *= this.efficiencyMult;
        // finally, try and consume as much fire enhancer as recipe EU divided by 5 thousand.
        // fire enhancer can double efficiency if fully satisfied
        if (!fireEnhancerConsumers.isEmpty()) {
            int desiredEnhancer = (int) (eu / 5000);
            int unsatisfiedEnhancer = desiredEnhancer;
            for (var consumer : fireEnhancerConsumers) {
                unsatisfiedEnhancer -= consumer.apply(unsatisfiedEnhancer);
                if (unsatisfiedEnhancer == 0) break;
            }
            this.fireEnhancerConsumed = desiredEnhancer - unsatisfiedEnhancer;
            this.fireEnhancerMult = 1 + (double) this.fireEnhancerConsumed / desiredEnhancer;
            eu *= this.fireEnhancerMult;
        }

        this.remainingEU += eu;
        this.maxEU = this.remainingEU;
        onConsumeActions.forEach(Runnable::run);
    }

    protected int[] determineFluidBurnability(FluidStack fluid, long maxVoltage) {
        List<FluidStack> listform = Collections.singletonList(fluid);
        List<ItemStack> dummyList = NonNullList.create();
        int eu = 0;
        int reps;
        int quant = 0;
        Recipe recipe = RecipeMaps.COMBUSTION_GENERATOR_FUELS.findRecipe(Integer.MAX_VALUE, dummyList, listform);
        if (recipe != null && recipe.getEUt() < maxVoltage) {
            // integer division for flooring behavior
            reps = fluid.amount / recipe.getFluidInputs().get(0).getAmount();
            eu = reps * recipe.getEUt() * recipe.getDuration();
            quant = recipe.getFluidInputs().get(0).getAmount() * reps;
        }
        recipe = RecipeMaps.GAS_TURBINE_FUELS.findRecipe(Integer.MAX_VALUE, dummyList, listform);
        if (recipe != null && recipe.getEUt() < maxVoltage) {
            // integer division for flooring behavior
            int reps_ = fluid.amount / recipe.getFluidInputs().get(0).getAmount();
            int eu_ = reps_ * recipe.getEUt() * recipe.getDuration();
            if (eu_ > eu) {
                reps = reps_;
                eu = eu_;
                quant = recipe.getFluidInputs().get(0).getAmount() * reps;
            }
        }
        recipe = RecipeMaps.SEMI_FLUID_GENERATOR_FUELS.findRecipe(Integer.MAX_VALUE, dummyList, listform);
        if (recipe != null && recipe.getEUt() < maxVoltage) {
            // integer division for flooring behavior
            int reps_ = fluid.amount / recipe.getFluidInputs().get(0).getAmount();
            int eu_ = reps_ * recipe.getEUt() * recipe.getDuration();
            if (eu_ > eu) {
                reps = reps_;
                eu = eu_;
                quant = recipe.getFluidInputs().get(0).getAmount() * reps;
            }
        }
        if (eu == 0) return EMPTY;
        else return new int[] { eu, quant };
    }

    protected int[] determineItemBurnability(ItemStack item, long maxVoltage) {
        if (maxVoltage < GTValues.V[GTValues.LV]) return EMPTY;
        // 10 EU per burntime, min tier of LV
        return new int[] { TileEntityFurnace.getItemBurnTime(item) * 10 * item.getCount(), item.getCount() };
    }

    private void incrementThrottle(Widget.ClickData clickData) {
        this.throttlePercentage = MathHelper.clamp(throttlePercentage + 5, 25, 100);
    }

    private void decrementThrottle(Widget.ClickData clickData) {
        this.throttlePercentage = MathHelper.clamp(throttlePercentage - 5, 25, 100);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.FRONT, RelativeDirection.DOWN, RelativeDirection.RIGHT)
                .aisle("F###F#", "FCCCF#", "FEEEF#", "FCCCF#", "F###F#")
                .aisle("######", "#GDG##", "#CNC##", "#GDG##", "######")
                .aisle("######", "#GDG##", "#INE##", "#GDG##", "######").setRepeatable(1, 8)
                .aisle("######", "#GDG##", "#ZNC##", "#GDG##", "######")
                .aisle("#VVVPP", "VNNNVY", "VNNNVY", "VNNNVY", "#VVVPP")
                .aisle("#CCC##", "CNNNC#", "XNNNM#", "CNNNC#", "#CCC##")
                .aisle("#VVVPP", "VNNNVY", "VNNNVY", "VNNNVY", "#VVVPP")
                .aisle("######", "#GDG##", "#ZNC##", "#GDG##", "######")
                .aisle("######", "#GDG##", "#HNE##", "#GDG##", "######").setRepeatable(1, 8)
                .aisle("######", "#GDG##", "#CNC##", "#GDG##", "######")
                .aisle("F###F#", "FCCCF#", "FEEEF#", "FCCCF#", "F###F#")
                .where('F', frames(GCYMMaterials.HSLASteel))
                .where('C', getCasingState(0))
                .where('E', getCasingState(2))
                .where('G', getCasingState(1))
                .where('D', getCasingState(6))
                .where('V', getCasingState(5))
                .where('P', getCasingState(4))
                .where('I', abilities(MultiblockAbility.IMPORT_ITEMS))
                .where('Z', getCasingState(0).or(autoAbilities(true, false)))
                .where('H', metaTileEntities(MetaTileEntities.FLUID_IMPORT_HATCH)) // Do not allow multi hatches
                .where('Y', getCasingState(4).or(abilities(MultiblockAbility.OUTPUT_ENERGY)
                        .setMinGlobalLimited(1).setMaxGlobalLimited(4, 2)))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('X', selfPredicate())
                .where('N', air())
                .where('#', any())
                .build();
    }

    @Nonnull
    protected static TraceabilityPredicate getCasingState(int id) {
        return states(switch (id) {
            default -> MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.TITANIUM_TURBINE_CASING);
            case 1 -> MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.TITANIUM_GEARBOX);
            case 2 -> MetaBlocks.MULTIBLOCK_CASING
                    .getState(BlockMultiblockCasing.MultiblockCasingType.ENGINE_INTAKE_CASING);
            case 4 -> MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.PLASCRETE);
            case 5 -> MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.TITANIUM_FIREBOX);
            case 6 -> GCYMMetaBlocks.UNIQUE_CASING
                    .getState(BlockUniqueCasing.UniqueCasingType.MOLYBDENUM_DISILICIDE_COIL);
        });
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return iMultiblockPart instanceof IMultiblockAbilityPart<?>abilityPart &&
                abilityPart.getAbility() == MultiblockAbility.OUTPUT_ENERGY ? Textures.PLASCRETE :
                        Textures.STABLE_TITANIUM_CASING;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(true, isActive())
                .addCustom(tl -> {
                    if (isStructureFormed()) {
                        long outVoltage = isActive() ? energyContainer.getOutputVoltage() : 0;
                        // EU line
                        String energyFormatted = TextFormattingUtil.formatNumbers(outVoltage);
                        ITextComponent voltageName = new TextComponentString(
                                GTValues.VNF[GTUtility.getFloorTierByVoltage(outVoltage)]);

                        textList.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gregtech.multiblock.turbine.energy_per_tick",
                                energyFormatted, voltageName));

                        // Efficiency line
                        ITextComponent efficiency = TextComponentUtil.stringWithColor(
                                TextFormatting.AQUA,
                                TextFormattingUtil.formatNumbers(this.efficiencyMult) + "x");

                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtcefucontent.multiblock.sympathetic_combustor.efficiency",
                                efficiency));
                        if (this.fireEnhancerMult != 1) {
                            // Fire Enhancement line
                            ITextComponent enhancement = TextComponentUtil.stringWithColor(
                                    TextFormatting.GOLD,
                                    TextFormattingUtil.formatNumbers(this.fireEnhancerMult) + "x");

                            tl.add(TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gtcefucontent.multiblock.sympathetic_combustor.enhancement",
                                    enhancement));

                            // Fire Enhancer line
                            ITextComponent enhancer = TextComponentUtil.stringWithColor(
                                    TextFormatting.RED,
                                    this.fireEnhancerConsumed + " L");

                            tl.add(TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gtcefucontent.multiblock.sympathetic_combustor.enhancer",
                                    enhancer));
                        }

                        // Throttle line
                        ITextComponent throttle = TextComponentUtil.stringWithColor(
                                getNumberColor(getThrottle()),
                                getThrottle() + "%");

                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtcefucontent.multiblock.sympathetic_combustor.throttle",
                                throttle));
                    }
                })
                .addWorkingStatusLine();
    }

    private TextFormatting getNumberColor(int number) {
        if (number == 0) {
            return TextFormatting.DARK_RED;
        } else if (number <= 40) {
            return TextFormatting.RED;
        } else if (number < 100) {
            return TextFormatting.YELLOW;
        } else {
            return TextFormatting.GREEN;
        }
    }

    @Override
    protected @NotNull Widget getFlexButton(int x, int y, int width, int height) {
        WidgetGroup group = new WidgetGroup(x, y, width, height);
        group.addWidget(new ClickButtonWidget(0, 0, 9, 18, "", this::decrementThrottle)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_MINUS)
                .setTooltipText("gtcefucontent.multiblock.sympathetic_combustor.throttle_decrement"));
        group.addWidget(new ClickButtonWidget(9, 0, 9, 18, "", this::incrementThrottle)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_PLUS)
                .setTooltipText("gtcefucontent.multiblock.sympathetic_combustor.throttle_increment"));
        return group;
    }

    @Override
    public double getFillPercentage(int index) {
        return (double) this.remainingEU / this.maxEU;
    }

    @Override
    public void addBarHoverText(List<ITextComponent> hoverList, int index) {
        if (!isStructureFormed()) {
            hoverList.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                    "gregtech.multiblock.invalid_structure"));
        } else {
            hoverList.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                    "gtcefucontent.multiblock.sympathetic_combustor.progress"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("ThrottlePercentage", throttlePercentage);
        data.setLong("RemainingEU", remainingEU);
        data.setLong("MaxEU", maxEU);
        data.setDouble("EfficiencyMult", efficiencyMult);
        data.setDouble("FireEnhancerMult", fireEnhancerMult);
        data.setInteger("FireEnhancerConsumed", fireEnhancerConsumed);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        throttlePercentage = data.getInteger("ThrottlePercentage");
        remainingEU = data.getLong("RemainingEU");
        maxEU = data.getLong("MaxEU");
        efficiencyMult = data.getDouble("EfficiencyMult");
        fireEnhancerMult = data.getDouble("FireEnhancerMult");
        fireEnhancerConsumed = data.getInteger("FireEnhancerConsumed");
        super.readFromNBT(data);
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarInt(throttlePercentage);
        buf.writeDouble(efficiencyMult);
        buf.writeDouble(fireEnhancerMult);
        buf.writeVarInt(fireEnhancerConsumed);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        throttlePercentage = buf.readVarInt();
        efficiencyMult = buf.readDouble();
        fireEnhancerMult = buf.readDouble();
        fireEnhancerConsumed = buf.readVarInt();
    }

    public int getThrottle() {
        return throttlePercentage;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.LARGE_GAS_TURBINE_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), isActive(),
                true);
    }

    @Override
    protected boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    public boolean isActive() {
        return this.remainingEU > 0;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return true;
    }

    protected static class ItemData {

        public final Item item;
        public final int meta;

        public ItemData(ItemStack stack) {
            this.item = stack.getItem();
            this.meta = stack.getMetadata();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemData metaItem = (ItemData) o;
            return meta == metaItem.meta && Objects.equals(item, metaItem.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, meta);
        }
    }
}
