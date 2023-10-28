package com.m_w_k.gtcefucontent.common.block.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import org.jetbrains.annotations.NotNull;

import gregtech.api.block.VariantBlock;

public class GTCEFuCBlockHardenedCasing extends VariantBlock<GTCEFuCBlockHardenedCasing.CasingType> {

    public GTCEFuCBlockHardenedCasing() {
        super(Material.IRON);
        setTranslationKey("hardened_casing");
        setHardness(10.0f);
        setResistance(500.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 4);
        setDefaultState(getState(CasingType.INDESTRUCTIBLE_CASING));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public enum CasingType implements IStringSerializable {

        INDESTRUCTIBLE_CASING("indestructible_casing"),
        INDESTRUCTIBLE_PIPE_CASING("indestructible_pipe_casing"),
        PLASMA_PIPE_CASING("plasma_pipe_casing"),
        PRESSURE_CASING("high_pressure_casing");

        private final String name;

        CasingType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getName() {
            return this.name;
        }
    }
}
