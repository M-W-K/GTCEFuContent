package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.*;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class MetaTileEntityInfinityExtractor extends RecipeMapMultiblockController {
    public MetaTileEntityInfinityExtractor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTCEFuCRecipeMaps.INFINITY_EXTRACTOR_RECIPES);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.DOWN, RelativeDirection.FRONT)
                .aisle("GG#", "IGG", "IXS", "FSS", "###")
                .aisle("BB#", "RPR", "CNO", "FNG", "#V#").setRepeatable(1, 4)
                .aisle("GG#", "EGG", "EMS", "FSS", "###")
                .where('S', getCasingState(0).or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setMinGlobalLimited(0).setMaxGlobalLimited(1,1)))
                .where('G', getCasingState(1))
                .where('P', getCasingState(2))
                .where('C', heatingCoils())
                .where('B', frames(Materials.Steel))
                .where('R', getCasingState(5))
                .where('F', getCasingState(6))
                .where('M', autoAbilities(true, false))
                .where('O', metaTileEntities(MetaTileEntities.ITEM_EXPORT_BUS[GTValues.ULV]))
                .where('I', getCasingState(0).or(abilities(MultiblockAbility.IMPORT_ITEMS).setPreviewCount(1)))
                .where('E', getCasingState(0).or(abilities(MultiblockAbility.INPUT_ENERGY).setPreviewCount(2)))
                .where('V', states(Blocks.BEDROCK.getDefaultState()))
                .where('X', selfPredicate())
                .where('N', air())
                .where('#', any())
                .build();
    }
    @Nonnull
    protected static TraceabilityPredicate getCasingState(int id) {
        return states(switch (id) {
            default -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
            case 1 -> MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.GRATE_CASING);
            case 2 -> MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE);
            case 5 -> MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX);
            case 6 -> MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
        });

    }
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.ELECTROMAGNETIC_SEPARATOR_OVERLAY;
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
        return new MetaTileEntityInfinityExtractor(this.metaTileEntityId);
    }
}
