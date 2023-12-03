package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;

import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.FuelMultiblockController;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.*;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityEnergyHatch;
import org.jetbrains.annotations.Nullable;

public class MetaTileEntitySympatheticCombustor extends FuelMultiblockController {

    public MetaTileEntitySympatheticCombustor(ResourceLocation metaTileEntityId) {
        // Maximum overclock of UHV
        super(metaTileEntityId, GTCEFuCRecipeMaps.SYMPATHETIC_COMBUSTOR_RECIPES, GTValues.UHV);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.FRONT, RelativeDirection.DOWN, RelativeDirection.RIGHT)
                .aisle("F###F#", "FCCCF#", "FEEEF#", "FCCCF#", "F###F#")
                .aisle("######", "#GDG##", "#CNC##", "#GDG##", "######")
                .aisle("######", "#GDG##", "#INE##", "#GDG##", "######").setRepeatable(1,
                        // reference the recipeMap in case it is ever changed
                        GTCEFuCRecipeMaps.SYMPATHETIC_COMBUSTOR_RECIPES.getMaxInputs())
                .aisle("######", "#GDG##", "#ZNC##", "#GDG##", "######")
                .aisle("#VVVPP", "VNNNVY", "VNNNVY", "VNNNVY", "#VVVPP")
                .aisle("#CCC##", "CNNNC#", "XNNNM#", "CNNNC#", "#CCC##")
                .aisle("#VVVPP", "VNNNVY", "VNNNVY", "VNNNVY", "#VVVPP")
                .aisle("######", "#GDG##", "#ZNC##", "#GDG##", "######")
                .aisle("######", "#GDG##", "#HNE##", "#GDG##", "######").setRepeatable(1,
                        // reference the recipeMap in case it is ever changed
                        GTCEFuCRecipeMaps.SYMPATHETIC_COMBUSTOR_RECIPES.getMaxFluidInputs())
                .aisle("######", "#GDG##", "#CNC##", "#GDG##", "######")
                .aisle("F###F#", "FCCCF#", "FEEEF#", "FCCCF#", "F###F#")
                .where('F', frames(GCYMMaterials.HSLASteel))
                .where('C', getCasingState(0))
                .where('E', getCasingState(2))
                .where('G', getCasingState(1))
                .where('D', getCasingState(6))
                .where('V', getCasingState(5))
                .where('P', getCasingState(4))
                // Only allow HV and below; this allows for up to LuV voltage output or so.
                .where('I', metaTileEntities(Arrays.stream(MetaTileEntities.ITEM_IMPORT_BUS)
                        .filter(mte -> mte != null && mte.getTier() <= GTValues.HV)
                        .toArray(MetaTileEntity[]::new)))
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
        // Unfortunately, no interface for energy hatches is available to instanceof with
        return iMultiblockPart instanceof MetaTileEntityEnergyHatch ? Textures.PLASCRETE :
                Textures.STABLE_TITANIUM_CASING;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gcym.machine.steam_engine.tooltip.1", GTValues.VNF[GTValues.UHV]));
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.LARGE_GAS_TURBINE_OVERLAY;
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
        return new MetaTileEntitySympatheticCombustor(this.metaTileEntityId);
    }
}
