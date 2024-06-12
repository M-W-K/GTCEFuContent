package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import javax.annotation.Nonnull;

import com.m_w_k.gtcefucontent.api.render.GTCEFuCTextures;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockStandardCasing;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;

import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;

public class MetaTileEntityPneumaticInfuser extends RecipeMapMultiblockController {

    public MetaTileEntityPneumaticInfuser(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                .aisle("CCCCCC", "CCCCCC", "CCCCCC", "CCCCCC", "CCCCCC")
                .aisle("CCCCCC", "CPRRRC", "CRRRRC", "CPRRRC", "CXCCCC")
                .aisle("CCCCCC", "CPRRRC", "CRNNRC", "CPRRRC", "CCCCCC")
                .aisle("CC####", "CPRRR#", "CRNRR#", "CPRRR#", "CC####")
                .aisle("CC####", "CPRGG#", "CRRGG#", "CPRGG#", "CC####")
                .aisle("CC####", "CCRRR#", "CCRRR#", "CCRRR#", "CC####")
                .where('C', getCasingState(0).setMinGlobalLimited(78)
                        .or(autoAbilities(false, true, true, true, true, true, false))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1, 1).setMaxGlobalLimited(3)))
                .where('R', getCasingState(1))
                .where('P', getCasingState(2))
                .where('G', getCasingState(3))
                .where('X', selfPredicate())
                .where('N', air())
                .where('#', any())
                .build();
    }

    @Nonnull
    protected static TraceabilityPredicate getCasingState(int id) {
        return states(switch (id) {
            default -> GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                    .getState(BlockLargeMultiblockCasing.CasingType.STRESS_PROOF_CASING);
            case 1 -> GTCEFuCMetaBlocks.STANDARD_CASING.getState(GTCEFuCBlockStandardCasing.CasingType.PRESSURE_CASING);
            case 2 -> MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE);
            case 3 -> MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.LAMINATED_GLASS);
        });
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GCYMTextures.STRESS_PROOF_CASING;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return GTCEFuCTextures.PNEUMATIC_INFUSER_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityPneumaticInfuser(this.metaTileEntityId);
    }
}
