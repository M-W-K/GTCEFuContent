package com.m_w_k.gtcefucontent.common.block;

import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockStandardCasing;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockAdvancedCasing;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockStorageBlock;

import gregtech.common.blocks.MetaBlocks;

public final class GTCEFuCMetaBlocks {

    private GTCEFuCMetaBlocks() {}

    public static GTCEFuCBlockAdvancedCasing ADVANCED_CASING;
    public static GTCEFuCBlockHardenedCasing HARDENED_CASING;
    public static GTCEFuCBlockStandardCasing STANDARD_CASING;
    public static GTCEFuCBlockStorageBlock STORAGE_BLOCK;

    public static void init() {
        ADVANCED_CASING = new GTCEFuCBlockAdvancedCasing();
        ADVANCED_CASING.setRegistryName("advanced_casing");
        HARDENED_CASING = new GTCEFuCBlockHardenedCasing();
        HARDENED_CASING.setRegistryName("hardened_casing");
        STANDARD_CASING = new GTCEFuCBlockStandardCasing();
        STANDARD_CASING.setRegistryName("standard_casing");
        STORAGE_BLOCK = new GTCEFuCBlockStorageBlock();
        STORAGE_BLOCK.setRegistryName("storage_block");
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        ADVANCED_CASING.onModelRegister();
        registerItemModel(HARDENED_CASING);
        registerItemModel(STANDARD_CASING);
        registerItemModel(STORAGE_BLOCK);
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(@NotNull Block block) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            // noinspection DataFlowIssue
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                    block.getMetaFromState(state),
                    new ModelResourceLocation(block.getRegistryName(),
                            MetaBlocks.statePropertiesToString(state.getProperties())));
        }
    }

    @SuppressWarnings({ "unused", "unchecked" })
    private static <T extends Comparable<T>> @NotNull String getPropertyName(@NotNull IProperty<T> property,
                                                                             Comparable<?> value) {
        return property.getName((T) value);
    }
}
