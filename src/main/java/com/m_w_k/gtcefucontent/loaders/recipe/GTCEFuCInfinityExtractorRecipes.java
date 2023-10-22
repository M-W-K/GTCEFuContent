package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.material.Material;
import gregtech.api.GTValues;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import static com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps.INFINITY_EXTRACTOR_RECIPES;

public class GTCEFuCInfinityExtractorRecipes {
    private GTCEFuCInfinityExtractorRecipes() {}

    static final ItemStack dust = new ItemStack(ModObject.itemMaterial.getItemNN(), 1, 20);
    static final ItemStack block1 = new ItemStack(ModObject.block_infinity.getItemNN(), 1, 0);
    private static final ItemStack block2 = new ItemStack(ModObject.block_infinity.getItemNN(), 1, 1);
    static final ItemStack block3 = new ItemStack(ModObject.block_infinity.getItemNN(), 1, 2);

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
    }
}
