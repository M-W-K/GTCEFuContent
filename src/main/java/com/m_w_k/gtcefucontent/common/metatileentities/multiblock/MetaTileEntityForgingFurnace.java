package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;
import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;
import gregtech.api.GTValues;
import gregtech.api.block.IHeatingCoilBlockStats;
import gregtech.api.capability.IHeatingCoil;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.api.recipes.recipeproperties.TemperatureProperty;
import gregtech.api.util.GTUtility;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.*;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityEnergyHatch;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityItemBus;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import static gregtech.api.recipes.logic.OverclockingLogic.heatingCoilOverclockingLogic;

public class MetaTileEntityForgingFurnace extends GCYMRecipeMapMultiblockController implements IHeatingCoil {

    private int blastFurnaceTemperature;
    public MetaTileEntityForgingFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, determineRecipeMaps());
        this.recipeMapWorkable = new ForgingFurnaceRecipeLogic(this);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object type = context.get("CoilType");
        if (type instanceof IHeatingCoilBlockStats) {
            this.blastFurnaceTemperature = ((IHeatingCoilBlockStats) type).getCoilTemperature();
        } else {
            this.blastFurnaceTemperature = BlockWireCoil.CoilType.CUPRONICKEL.getCoilTemperature();
        }

        this.blastFurnaceTemperature += 100 *
                Math.max(0, GTUtility.getTierByVoltage(getEnergyContainer().getInputVoltage()) - GTValues.MV);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.blastFurnaceTemperature = 0;
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe, boolean consumeIfSuccess) {
        return this.blastFurnaceTemperature >= recipe.getProperty(TemperatureProperty.getInstance(), 0);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.FRONT, RelativeDirection.DOWN, RelativeDirection.RIGHT)
                .aisle("TTTT", "TTTT", "TTTT", "TTTT")
                .aisle("TEET", "EPPE", "EPPE", "TEET").setRepeatable(1, 6)
                .aisle("TTTT", "TPPT", "TPPT", "TTTT")
                .aisle("####", "#PP#", "#PP#", "####")
                .aisle("HHHH", "HPPH", "HPPH", "HHHH")
                .aisle("VEEV", "ENNE", "ENNE", "VEEV")
                .aisle("CCCC", "CNNC", "CNNC", "CCCC")
                .aisle("VEEV", "ENNE", "ENNE", "VEEV")
                .aisle("CCCC", "CNNC", "CNNC", "CCCC")
                .aisle("VEEV", "ENNE", "ENNE", "VEEV")
                .aisle("HHHH", "HPPH", "HPPH", "HHHH")
                .aisle("####", "#PP#", "#PP#", "####")
                .aisle("RRRR", "RPPR", "RPPR", "RRRR")
                .aisle("VCCV", "RNNR", "RNNR", "VCCV")
                .aisle("VMMV", "RNNR", "XNNR", "VEEV")
                .aisle("VCCV", "RNNR", "RNNR", "VCCV")
                .aisle("RRRR", "RPPR", "RPPR", "RRRR")
                .aisle("####", "#PP#", "#PP#", "####")
                .aisle("FFFF", "FPPF", "FPPF", "FFFF")
                .aisle("FVVF", "VPPV", "VPPV", "FVVF")
                .aisle("FFFF", "FVVF", "FVVF", "FFFF")
                .where('R', getCasingState(0).setMinGlobalLimited(10)
                        .or(autoAbilities(true, true, false, true, true, true, false)))
                .where('T', getCasingState(1).setMinGlobalLimited(20)
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setPreviewCount(2)))
                .where('E', getCasingState(2))
                .where('P', getCasingState(3))
                .where('H', getCasingState(4))
                .where('V', getCasingState(5))
                .where('C', heatingCoils())
                .where('F', getCasingState(6))
                .where('M', getCasingState(2).or(abilities(MultiblockAbility.MUFFLER_HATCH).setExactLimit(1)))
                .where('X', selfPredicate())
                .where('N', air())
                .where('#', any())
                .build();
    }
    @Nonnull
    protected static TraceabilityPredicate getCasingState(int id) {
        return states(switch (id) {
            default -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.INVAR_HEATPROOF);
            case 1 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TUNGSTENSTEEL_ROBUST);
            case 2 -> MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.EXTREME_ENGINE_INTAKE_CASING);
            case 3 -> MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE);
            case 4 -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.HIGH_TEMPERATURE_CASING);
            case 5 -> GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT);
            case 6 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF);
        });

    }
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return iMultiblockPart instanceof MetaTileEntityItemBus itemBus
                && itemBus.getAbility().equals(MultiblockAbility.IMPORT_ITEMS)
                ? Textures.ROBUST_TUNGSTENSTEEL_CASING : Textures.HEAT_PROOF_CASING;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.ALLOY_SMELTER_OVERLAY;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return true;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityForgingFurnace(this.metaTileEntityId);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("gregtech.multiblock.blast_furnace.max_temperature",
                    blastFurnaceTemperature)
                    .setStyle(new Style().setColor(TextFormatting.RED)));
        }
        super.addDisplayText(textList);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.1"));
        tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.2"));
        tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.3"));
    }

    private static @NotNull RecipeMap<?> @NotNull [] determineRecipeMaps() {
        return new RecipeMap<?>[] { RecipeMaps.BLAST_RECIPES, GTCEFuCRecipeMaps.FORGING_FURNACE_RECIPES};
    }

    // I don't know why this override isn't part of base greg or gcym. Maybe they forgot?
    @Override
    public SoundEvent getSound() {
        return getCurrentRecipeMap().getSound();
    }

    @Override
    public int getCurrentTemperature() {
        return this.blastFurnaceTemperature;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class ForgingFurnaceRecipeLogic extends GCYMMultiblockRecipeLogic {

        public ForgingFurnaceRecipeLogic(RecipeMapMultiblockController metaTileEntity) {
            super(metaTileEntity);
        }

        @Override
        protected int @NotNull [] runOverclockingLogic(@NotNull IRecipePropertyStorage propertyStorage, int recipeEUt,
                                                       long maxVoltage, int duration, int maxOverclocks) {
            return heatingCoilOverclockingLogic(Math.abs(recipeEUt),
                    maxVoltage,
                    duration,
                    maxOverclocks,
                    ((IHeatingCoil) metaTileEntity).getCurrentTemperature(),
                    propertyStorage.getRecipePropertyValue(TemperatureProperty.getInstance(), 0));
        }
    }
}
