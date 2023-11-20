package com.m_w_k.gtcefucontent.loaders.recipe;

import com.latmod.mods.projectex.item.ProjectEXItems;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;
import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import net.minecraft.item.ItemStack;

import java.util.Collections;

import static com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCInfinityExtractorRecipes.*;

@SuppressWarnings("SameParameterValue")
public final class GTCEFuCPackingLoader {

    public static void init() {
        ItemStack[] stacksIn;

        simplePackingRegister("infinity_reagent_block1", GTCEFuCUtil.setStackCount(dust, 9), block1, true);
        simplePackingRegister("infinity_reagent_block2", GTCEFuCUtil.setStackCount(block1, 9), block2, true);
        simplePackingRegister("infinity_reagent_block3", GTCEFuCUtil.setStackCount(block2, 9), block3, true);

        // I know that this is incredibly annoying. That's the point.
        stacksIn = new ItemStack[]{GTCEFuCMetaItems.STELLAR_NUGGET.getStackForm(9), new ItemStack(ProjectEXItems.FINAL_STAR_SHARD, 1)};
        ItemStack[] stacksOut = new ItemStack[]{GTCEFuCMetaItems.STELLAR_INGOT.getStackForm()};
        packerPackingRegister(stacksIn, stacksOut, 1, GTValues.VA[GTValues.UV], true);
    }

    public static void simplePackingRegister(String name, ItemStack stackIn, ItemStack stackOut, boolean reversible) {
        ModHandler.addShapelessRecipe(name + (reversible ? "_pack" : ""), stackOut, Collections.nCopies(stackIn.getCount(), GTCEFuCUtil.setStackCount(stackIn, 1)).toArray());
        RecipeMaps.PACKER_RECIPES.recipeBuilder()
                .inputs(stackIn)
                .outputs(stackOut)
                .buildAndRegister();
        if (reversible) {
            ModHandler.addShapelessRecipe(name + "_unpack", stackIn, stackOut);
            RecipeMaps.PACKER_RECIPES.recipeBuilder()
                    .inputs(stackOut)
                    .outputs(stackIn)
                    .buildAndRegister();
        }
    }

    public static void packerPackingRegister(ItemStack[] stacksIn, ItemStack[] stacksOut, int duration, int EU, boolean reversible) {
        RecipeMaps.PACKER_RECIPES.recipeBuilder()
                .inputs(stacksIn)
                .outputs(stacksOut)
                .duration(duration).EUt(EU).buildAndRegister();
        if (reversible) RecipeMaps.PACKER_RECIPES.recipeBuilder()
                .inputs(stacksOut)
                .outputs(stacksIn)
                .duration(duration).EUt(EU).buildAndRegister();
    }
}
