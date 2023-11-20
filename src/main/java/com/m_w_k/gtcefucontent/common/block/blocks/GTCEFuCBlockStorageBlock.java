package com.m_w_k.gtcefucontent.common.block.blocks;

import gregtech.api.block.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

public class GTCEFuCBlockStorageBlock extends VariantBlock<GTCEFuCBlockStorageBlock.StorageType> {

    public GTCEFuCBlockStorageBlock() {
        super(Material.SAND);
        setTranslationKey("storage_block");
        setHardness(1.0f);
        setResistance(1.0f);
        setSoundType(SoundType.SAND);
        setDefaultState(getState(StorageType.INFINITY_1));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public enum StorageType implements IStringSerializable {

        INFINITY_1("infinity1"),
        INFINITY_2("infinity2"),
        INFINITY_3("infinity3");

        private final String name;

        StorageType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getName() {
            return this.name;
        }
    }
}
