package com.m_w_k.gtcefucontent.loaders.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemStack;

import com.latmod.mods.projectex.item.ProjectEXItems;
import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;

import gregtech.api.GTValues;
import gregtech.api.recipes.ingredients.nbtmatch.NBTCondition;
import gregtech.api.recipes.ingredients.nbtmatch.NBTMatcher;
import gregtech.api.unification.material.Materials;
import moze_intel.projecte.gameObjs.items.ItemPE;

public final class GTCEFuCStarSiphonRecipes {

    private GTCEFuCStarSiphonRecipes() {}

    public final static List<ItemStack> starsRaw = new ArrayList<>() {

        {
            this.add(new ItemStack(Objects.requireNonNull(ItemPE.getByNameOrId("projecte:item.pe_fuel")), 2, 1));
            this.add(new ItemStack(Objects.requireNonNull(ItemPE.getByNameOrId("projecte:item.pe_klein_star")), 1, 0));
            this.add(new ItemStack(Objects.requireNonNull(ItemPE.getByNameOrId("projecte:item.pe_klein_star")), 1, 1));
            this.add(new ItemStack(Objects.requireNonNull(ItemPE.getByNameOrId("projecte:item.pe_klein_star")), 1, 2));
            this.add(new ItemStack(Objects.requireNonNull(ItemPE.getByNameOrId("projecte:item.pe_klein_star")), 1, 3));
            this.add(new ItemStack(Objects.requireNonNull(ItemPE.getByNameOrId("projecte:item.pe_klein_star")), 1, 4));
            this.add(new ItemStack(Objects.requireNonNull(ItemPE.getByNameOrId("projecte:item.pe_klein_star")), 1, 5));
            this.add(new ItemStack(ProjectEXItems.MAGNUM_STAR_EIN));
            this.add(new ItemStack(ProjectEXItems.MAGNUM_STAR_ZWEI));
            this.add(new ItemStack(ProjectEXItems.MAGNUM_STAR_DREI));
            this.add(new ItemStack(ProjectEXItems.MAGNUM_STAR_VIER));
            this.add(new ItemStack(ProjectEXItems.MAGNUM_STAR_SPHERE));
            this.add(new ItemStack(ProjectEXItems.MAGNUM_STAR_OMEGA));
            this.add(new ItemStack(ProjectEXItems.COLOSSAL_STAR_EIN));
            this.add(new ItemStack(ProjectEXItems.COLOSSAL_STAR_ZWEI));
            this.add(new ItemStack(ProjectEXItems.COLOSSAL_STAR_DREI));
            this.add(new ItemStack(ProjectEXItems.COLOSSAL_STAR_VIER));
            this.add(new ItemStack(ProjectEXItems.COLOSSAL_STAR_SPHERE));
            this.add(new ItemStack(ProjectEXItems.COLOSSAL_STAR_OMEGA));
        }
    };

    private final static List<List<ItemStack>> stars = new ArrayList<>() {

        {
            this.add(starsRaw.subList(1, 7));
            this.add(starsRaw.subList(7, 13));
            this.add(starsRaw.subList(13, 19));
        }
    };

    public static void init() {
        int t = 0;
        int i;
        for (List<ItemStack> tier : stars) {
            int mod2 = (int) Math.pow(2.0, t);
            i = 0;
            for (ItemStack star : tier) {
                int fluid = (int) (Math.pow(2.0, t * 7 + i - 2) * 125);
                GTCEFuCRecipeMaps.STAR_SIPHON_RECIPES.recipeBuilder()
                        .inputNBT(star.getItem(), 1, star.getMetadata(), NBTMatcher.ANY, NBTCondition.ANY)
                        .outputs(starsRaw.get(t * 6 + i))
                        .fluidOutputs(Materials.Thorium.getPlasma(fluid))
                        .EUToStart(mod2 * 20000000000L)
                        .duration(16).EUt((int) (GTValues.V[GTValues.UV + t] * 2)).buildAndRegister();
                i++;
            }
            t++;
        }
    }
}
