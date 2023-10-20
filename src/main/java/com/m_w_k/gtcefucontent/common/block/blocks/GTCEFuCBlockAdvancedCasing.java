package com.m_w_k.gtcefucontent.common.block.blocks;

import gregtech.api.block.VariantActiveBlock;
import gregtech.client.utils.BloomEffectUtil;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

public class GTCEFuCBlockAdvancedCasing extends VariantActiveBlock<GTCEFuCBlockAdvancedCasing.AdvancedCasingType> {
    public GTCEFuCBlockAdvancedCasing() {
        super(Material.IRON);
        setTranslationKey("advanced_casing");
        setHardness(5.0f);
        // it's a null field, so it should be completely invincible to force. Thus, bedrock stats.
        setResistance(6000000.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 3);
        setDefaultState(getState(AdvancedCasingType.NULL_FIELD_CASING));

    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    protected boolean isBloomEnabled(AdvancedCasingType value) {
        return value == AdvancedCasingType.NULL_FIELD_CASING;
    }

    public enum AdvancedCasingType implements IStringSerializable {

        NULL_FIELD_CASING("null_field_casing");

        private final String name;

        AdvancedCasingType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getName() {
            return this.name;
        }
    }
}
