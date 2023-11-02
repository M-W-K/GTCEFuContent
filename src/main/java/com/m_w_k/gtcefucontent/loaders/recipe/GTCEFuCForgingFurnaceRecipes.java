package com.m_w_k.gtcefucontent.loaders.recipe;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.recipes.ForgingFurnaceRecipeProducer;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.stack.MaterialStack;

public final class GTCEFuCForgingFurnaceRecipes {

    private GTCEFuCForgingFurnaceRecipes() {}

    public static void init() {
        GTCEFuContent.log("Starting the construction of Forging Furnace custom recipes...");
        // maybe use GregTechAPI.materialManager.getRegisteredMaterials() instead?

        Collection<Recipe> blastRecipes = RecipeMaps.BLAST_RECIPES.getRecipeList();
        // get list of materials that can be processed from one OrePrefix to another in the blast furnace
        List<Material> blastMaterials = blastRecipes.stream().map(recipe -> {
            ItemStack[] inputs = recipe.getInputs().get(0).getInputStacks();
            // Multi-input recipes are obviously not valid
            if (inputs.length == 1) {
                MaterialStack inputMaterialStack = OreDictUnifier.getMaterial(inputs[0]);
                // there is always an output, and we only care about the first
                MaterialStack outputMaterialStack = OreDictUnifier.getMaterial(recipe.getOutputs().get(0));
                if (inputMaterialStack != null && outputMaterialStack != null) {
                    Material material = inputMaterialStack.material;
                    if (material == outputMaterialStack.material) return material;
                }
            }
            return null;
        }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        ForgingFurnaceRecipeProducer.DEFAULT_PRODUCER.produce(blastMaterials);
        GTCEFuContent.log("Finished Forging Furnace recipe construction.");
    }
}
