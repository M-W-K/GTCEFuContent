package com.m_w_k.gtcefucontent.loaders.recipe;

import static com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps.INFINITY_EXTRACTOR_RECIPES;

import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import gregtech.api.unification.material.Materials;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockStorageBlock;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;

import gregtech.api.GTValues;

public final class GTCEFuCInfinityExtractorRecipes {

    private GTCEFuCInfinityExtractorRecipes() {}

    static final ItemStack dust = GTCEFuCMetaItems.INFINITY_REAGENT.getStackForm();
    static final ItemStack block1 = GTCEFuCMetaBlocks.STORAGE_BLOCK
            .getItemVariant(GTCEFuCBlockStorageBlock.StorageType.INFINITY_1);
    static final ItemStack block2 = GTCEFuCMetaBlocks.STORAGE_BLOCK
            .getItemVariant(GTCEFuCBlockStorageBlock.StorageType.INFINITY_2);
    static final ItemStack block3 = GTCEFuCMetaBlocks.STORAGE_BLOCK
            .getItemVariant(GTCEFuCBlockStorageBlock.StorageType.INFINITY_3);

    public static void init() {
        INFINITY_EXTRACTOR_RECIPES.recipeBuilder()
                .input(Items.FIRE_CHARGE)
                .circuitMeta(1)
                .outputs(dust)
                .duration(1200)
                .EUt(GTValues.VA[GTValues.EV])
                .buildAndRegister();

        INFINITY_EXTRACTOR_RECIPES.recipeBuilder()
                .input(Items.FIRE_CHARGE, 4)
                .circuitMeta(2)
                .outputs(dust, block1)
                .duration(2400)
                .EUt(GTValues.VA[GTValues.IV])
                .buildAndRegister();

        INFINITY_EXTRACTOR_RECIPES.recipeBuilder()
                .input(Items.FIRE_CHARGE, 16)
                .circuitMeta(3)
                .outputs(dust, block1, block2)
                .duration(4800)
                .EUt(GTValues.VA[GTValues.LuV])
                .buildAndRegister();

        INFINITY_EXTRACTOR_RECIPES.recipeBuilder()
                .input(Items.FIRE_CHARGE, 64)
                .circuitMeta(4)
                .outputs(dust, block1, block2, block3)
                .duration(9600)
                .EUt(GTValues.VA[GTValues.ZPM])
                .buildAndRegister();

        INFINITY_EXTRACTOR_RECIPES.recipeBuilder()
                .circuitMeta(5)
                .chancedFluidOutput(GTCEFuCMaterials.Infinitesimality.getFluid(1), 10000, -1000)
                .duration(19200)
                .EUt(GTValues.VA[GTValues.HV])
                .buildAndRegister();
    }
}
