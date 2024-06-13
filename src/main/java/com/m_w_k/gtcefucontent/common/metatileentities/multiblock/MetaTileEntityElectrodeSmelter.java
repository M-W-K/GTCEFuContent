package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import static gregtech.api.recipes.logic.OverclockingLogic.heatingCoilOverclockingLogic;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.render.GTCEFuCTextures;

import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;
import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;
import gregicality.multiblocks.api.recipes.GCYMRecipeMaps;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;
import gregtech.api.GTValues;
import gregtech.api.block.IHeatingCoilBlockStats;
import gregtech.api.capability.IHeatingCoil;
import gregtech.api.capability.IMufflerHatch;
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
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.*;

public class MetaTileEntityElectrodeSmelter extends GCYMRecipeMapMultiblockController implements IHeatingCoil {

    private int blastFurnaceTemperature;

    public MetaTileEntityElectrodeSmelter(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, determineRecipeMaps());
        this.recipeMapWorkable = new ElectrodeSmelterRecipeLogic(this);
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
        TraceabilityPredicate casing = stateIndex(0).setMinGlobalLimited(615);
        return PATTERN
                .where('C', casing
                        .or(autoAbilities(false, true, true, true, true, true, false))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1, 4)
                                .setMaxGlobalLimited(16)))
                .where('V', stateIndex(1))
                .where('Q', frames(Materials.NaquadahAlloy))
                .where('P', stateIndex(6))
                .where('F', stateIndex(7))
                .where('G', stateIndex(2))
                .where('E', stateIndex(3))
                .where('S', stateIndex(4))
                .where('T', stateIndex(5))
                .where('H', casing)
                .where('A', frames(GCYMMaterials.TantalumCarbide))
                .where('O', heatingCoils())
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('X', selfPredicate())
                .where('N', air())
                .where('#', any())
                .build();
    }

    @Nonnull
    protected static TraceabilityPredicate stateIndex(int id) {
        return states(switch (id) {
            default -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                    .getState(BlockLargeMultiblockCasing.CasingType.HIGH_TEMPERATURE_CASING);
            case 1 -> GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT);
            case 2 -> MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.TUNGSTENSTEEL_GEARBOX);
            case 3 -> MetaBlocks.MULTIBLOCK_CASING
                    .getState(BlockMultiblockCasing.MultiblockCasingType.EXTREME_ENGINE_INTAKE_CASING);
            case 4 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.SUPERCONDUCTOR_COIL);
            case 5 -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TUNGSTENSTEEL_ROBUST);
            case 6 -> MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE);
            case 7 -> MetaBlocks.BOILER_FIREBOX_CASING
                    .getState(BlockFireboxCasing.FireboxCasingType.TUNGSTENSTEEL_FIREBOX);
        });
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return iMultiblockPart instanceof IMufflerHatch ? Textures.ROBUST_TUNGSTENSTEEL_CASING :
                GCYMTextures.BLAST_CASING;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return GTCEFuCTextures.ELECTRODE_SMELTER_OVERLAY;
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
        return new MetaTileEntityElectrodeSmelter(this.metaTileEntityId);
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
        return new RecipeMap<?>[] { RecipeMaps.BLAST_RECIPES, GCYMRecipeMaps.ALLOY_BLAST_RECIPES };
    }

    @Override
    public int getCurrentTemperature() {
        return this.blastFurnaceTemperature;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class ElectrodeSmelterRecipeLogic extends GCYMMultiblockRecipeLogic {

        public ElectrodeSmelterRecipeLogic(RecipeMapMultiblockController metaTileEntity) {
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

    private static final FactoryBlockPattern PATTERN = FactoryBlockPattern
            .start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
            .aisle(
                    "###CCCCCCCCCCC###",
                    "##CCCCCCCCCCCCC##",
                    "#CCCCCCCCCCCCCCC#",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "#CCCCCCCCCCCCCCC#",
                    "##CCCCCCCCCCCCC##",
                    "###CCCCCCCCCCC###")
            .aisle(
                    "###CCCCCCCCCCC###",
                    "##CCCCCCCCCCCCC##",
                    "#CCCCCVVVVVCCCCC#",
                    "CCCCCVVVVVVVCCCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCVCCCCCCCCCVCCC",
                    "CCVVCCCCCCCCCVVCC",
                    "CCVVCCCCCCCCCVVCC",
                    "CCVVCCCCCCCCCVVCC",
                    "CCVVCCCCCCCCCVVCC",
                    "CCVVCCCCCCCCCVVCC",
                    "CCCVCCCCCCCCCVCCC",
                    "CCCCCCCCCCCCCCCCC",
                    "CCCCCVVVVVVVCCCCC",
                    "#CCCCCVVVVVCCCCC#",
                    "##CCCCCCCCCCCCC##",
                    "###CCCCCXCCCCC###")
            .aisle(
                    "#################",
                    "#################",
                    "####Q#######Q####",
                    "###Q#########Q###",
                    "##Q#####P#####Q##",
                    "######FFPFF######",
                    "#####FNNNNNF#####",
                    "#####FNANANF#####",
                    "####PPNNNNNPP####",
                    "#####FNANANF#####",
                    "#####FNNNNNF#####",
                    "######FFPFF######",
                    "##Q#####P#####Q##",
                    "###Q#########Q###",
                    "####Q#######Q####",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "####Q#######Q####",
                    "###Q#########Q###",
                    "##Q#####P#####Q##",
                    "######EEGEE######",
                    "#####ENNSNNE#####",
                    "#####ENASANE#####",
                    "####PGSSGSSGP####",
                    "#####ENASANE#####",
                    "#####ENNSNNE#####",
                    "######EEGEE######",
                    "##Q#####P#####Q##",
                    "###Q#########Q###",
                    "####Q#######Q####",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "####Q#######Q####",
                    "###Q#########Q###",
                    "##Q#####P#####Q##",
                    "######FFTFF######",
                    "#####FNNNNNF#####",
                    "#####FNANANF#####",
                    "####PTNNNNNTP####",
                    "#####FNANANF#####",
                    "#####FNNNNNF#####",
                    "######FFTFF######",
                    "##Q#####P#####Q##",
                    "###Q#########Q###",
                    "####Q#######Q####",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "####QQQQQQQQQ####",
                    "###QQQHHHHHQQQ###",
                    "##QQHHHHPHHHHQQ##",
                    "##QQHHHHHHHHHQQ##",
                    "##QHHHHHHHHHHHQ##",
                    "##QHHHHANAHHHHQ##",
                    "##QHPHHNNNHHPHQ##",
                    "##QHHHHANAHHHHQ##",
                    "##QHHHHHHHHHHHQ##",
                    "##QQHHHHHHHHHQQ##",
                    "##QQHHHHPHHHHQQ##",
                    "###QQQHHHHHQQQ###",
                    "####QQQQQQQQQ####",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "###Q#########Q###",
                    "########P########",
                    "######FEPEF######",
                    "#####FNNNNNF#####",
                    "#####ENANANE#####",
                    "####PPNNNNNPP####",
                    "#####ENANANE#####",
                    "#####FNNNNNF#####",
                    "######FEPEF######",
                    "########P########",
                    "###Q#########Q###",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "###Q#########Q###",
                    "########P########",
                    "######OOOOO######",
                    "#####ONNNNNO#####",
                    "#####ONANANO#####",
                    "####PONNNNNOP####",
                    "#####ONANANO#####",
                    "#####ONNNNNO#####",
                    "######OOOOO######",
                    "########P########",
                    "###Q#########Q###",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "###Q#########Q###",
                    "########P########",
                    "######VVGVV######",
                    "#####VNNSNNV#####",
                    "#####VNASANV#####",
                    "####PGSSGSSGP####",
                    "#####VNASANV#####",
                    "#####VNNSNNV#####",
                    "######VVGVV######",
                    "########P########",
                    "###Q#########Q###",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "###Q#########Q###",
                    "########P########",
                    "######OOOOO######",
                    "#####ONNNNNO#####",
                    "#####ONANANO#####",
                    "####PONNNNNOP####",
                    "#####ONANANO#####",
                    "#####ONNNNNO#####",
                    "######OOOOO######",
                    "########P########",
                    "###Q#########Q###",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "###Q#########Q###",
                    "########P########",
                    "######VVGVV######",
                    "#####VNNSNNV#####",
                    "#####VNASANV#####",
                    "####PGSSGSSGP####",
                    "#####VNASANV#####",
                    "#####VNNSNNV#####",
                    "######VVGVV######",
                    "########P########",
                    "###Q#########Q###",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "###Q#########Q###",
                    "########P########",
                    "######OOOOO######",
                    "#####ONNNNNO#####",
                    "#####ONANANO#####",
                    "####PONNNNNOP####",
                    "#####ONANANO#####",
                    "#####ONNNNNO#####",
                    "######OOOOO######",
                    "########P########",
                    "###Q#########Q###",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "###Q#########Q###",
                    "########P########",
                    "######FETEF######",
                    "#####FNNNNNF#####",
                    "#####ENANANE#####",
                    "####PTNNNNNTP####",
                    "#####ENANANE#####",
                    "#####FNNNNNF#####",
                    "######FETEF######",
                    "########P########",
                    "###Q#########Q###",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#####QQQQQQQ#####",
                    "###QQQHHHHHQQQ###",
                    "###QHHHHPHHHHQ###",
                    "##QQHHHHHHHHHQQ##",
                    "##QHHHHHHHHHHHQ##",
                    "##QHHHHANAHHHHQ##",
                    "##QHPHHNNNHHPHQ##",
                    "##QHHHHANAHHHHQ##",
                    "##QHHHHHHHHHHHQ##",
                    "##QQHHHHHHHHHQQ##",
                    "###QHHHHPHHHHQ###",
                    "###QQQHHHHHQQQ###",
                    "#####QQQQQQQ#####",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "#################",
                    "########P########",
                    "######FEPEF######",
                    "#####FNNNNNF#####",
                    "#####ENANANE#####",
                    "####PPNNNNNPP####",
                    "#####ENANANE#####",
                    "#####FNNNNNF#####",
                    "######FEPEF######",
                    "########P########",
                    "#################",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "#################",
                    "########P########",
                    "######OOOOO######",
                    "#####ONNNNNO#####",
                    "#####ONANANO#####",
                    "####PONNNNNOP####",
                    "#####ONANANO#####",
                    "#####ONNNNNO#####",
                    "######OOOOO######",
                    "########P########",
                    "#################",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "#################",
                    "########P########",
                    "######VVGVV######",
                    "#####VNNSNNV#####",
                    "#####VNASANV#####",
                    "####PGSSGSSGP####",
                    "#####VNASANV#####",
                    "#####VNNSNNV#####",
                    "######VVGVV######",
                    "########P########",
                    "#################",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "#################",
                    "########P########",
                    "######OOOOO######",
                    "#####ONNNNNO#####",
                    "#####ONANANO#####",
                    "####PONNNNNOP####",
                    "#####ONANANO#####",
                    "#####ONNNNNO#####",
                    "######OOOOO######",
                    "########P########",
                    "#################",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "#################",
                    "########P########",
                    "######VVGVV######",
                    "#####VNNSNNV#####",
                    "#####VNASANV#####",
                    "####PGSSGSSGP####",
                    "#####VNASANV#####",
                    "#####VNNSNNV#####",
                    "######VVGVV######",
                    "########P########",
                    "#################",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "#################",
                    "#################",
                    "######OOOOO######",
                    "#####ONNNNNO#####",
                    "#####ONANANO#####",
                    "####PONNNNNOP####",
                    "#####ONANANO#####",
                    "#####ONNNNNO#####",
                    "######OOOOO######",
                    "########P########",
                    "#################",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "#################",
                    "#################",
                    "########P########",
                    "######FETEF######",
                    "#####FNNNNNF#####",
                    "#####ENANANE#####",
                    "####PTNNNNNTP####",
                    "#####ENANANE#####",
                    "#####FNNNNNF#####",
                    "######FETEF######",
                    "########P########",
                    "#################",
                    "#################",
                    "#################",
                    "#################")
            .aisle(
                    "#################",
                    "#################",
                    "####TTTTTTTTT####",
                    "###TTTTTTTTTTT###",
                    "##TTTTTTPTTTTTT##",
                    "##TTTTTTPTTTTTT##",
                    "##TTTTTTPTTTTTT##",
                    "##TTTTTTPTTTTTT##",
                    "##TTPPPPMPPPPTT##",
                    "##TTTTTTPTTTTTT##",
                    "##TTTTTTPTTTTTT##",
                    "##TTTTTTPTTTTTT##",
                    "##TTTTTTPTTTTTT##",
                    "###TTTTTTTTTTT###",
                    "####TTTTTTTTT####",
                    "#################",
                    "#################");
}
