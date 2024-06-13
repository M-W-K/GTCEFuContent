package com.m_w_k.gtcefucontent.api.fluids.void_starlight;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.m_w_k.gtcefucontent.Tags;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.store.FluidStorageKey;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class VoidStarlightBlockFluid extends BlockFluidFinite {

    public static List<Block> TO_REGISTER = new ObjectArrayList<>();

    private VoidStarlightBlockFluid(Fluid fluid) {
        super(fluid, Material.PORTAL);
    }

    @SuppressWarnings("deprecation") // fine for overriding
    @Override
    public @NotNull EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean hasTileEntity(@NotNull IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
        return new TileEntityVoidStarlight();
    }

    @Override
    public void onEntityCollision(World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state,
                                  @NotNull Entity entityIn) {
        if (!worldIn.isRemote && !entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.isNonBoss() &&
                entityIn.getEntityBoundingBox().intersects(state.getBoundingBox(worldIn, pos).offset(pos))) {
            entityIn.changeDimension(1);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(@NotNull IBlockState stateIn, @NotNull World worldIn, BlockPos pos, Random rand) {
        double d0 = (float) pos.getX() + rand.nextFloat();
        double d1 = (float) pos.getY() + this.getBlockLiquidHeight(worldIn, pos, stateIn, Material.PORTAL);
        double d2 = (float) pos.getZ() + rand.nextFloat();
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    public static final class VoidStarlightFluidBuilder extends FluidBuilder {

        @Override
        public @NotNull Fluid build(@NotNull String modid,
                                    gregtech.api.unification.material.@Nullable Material material,
                                    @Nullable FluidStorageKey key) {
            Fluid fluid = super.build(modid, material, key);
            TO_REGISTER.add(new VoidStarlightBlockFluid(fluid)
                    .setRegistryName(Tags.MODID, fluid.getUnlocalizedName() + ".block"));
            return fluid;
        }
    }
}
