package com.m_w_k.gtcefucontent.common.block;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockAdvancedCasing;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

public final class GTCEFuCMetaBlocks {

    private GTCEFuCMetaBlocks() {}

    public static GTCEFuCBlockAdvancedCasing ADVANCED_CASING;
    public static GTCEFuCBlockHardenedCasing HARDENED_CASING;

    public static void init() {
        ADVANCED_CASING = new GTCEFuCBlockAdvancedCasing();
        ADVANCED_CASING.setRegistryName("advanced_casing");
        HARDENED_CASING = new GTCEFuCBlockHardenedCasing();
        HARDENED_CASING.setRegistryName("hardened_casing");
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        ADVANCED_CASING.onModelRegister();
        registerItemModel(HARDENED_CASING);
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(@NotNull Block block) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            // noinspection ConstantConditions
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                    block.getMetaFromState(state),
                    new ModelResourceLocation(block.getRegistryName(),
                            MetaBlocks.statePropertiesToString(state.getProperties())));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> @NotNull String getPropertyName(@NotNull IProperty<T> property,
                                                                             Comparable<?> value) {
        return property.getName((T) value);
    }
}
