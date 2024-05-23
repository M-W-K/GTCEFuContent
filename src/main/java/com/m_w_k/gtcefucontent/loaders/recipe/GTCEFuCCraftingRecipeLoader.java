package com.m_w_k.gtcefucontent.loaders.recipe;

import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.latmod.mods.projectex.block.EnumTier;
import com.latmod.mods.projectex.item.ProjectEXItems;
import com.m_w_k.gtcefucontent.common.ConfigHolder;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class GTCEFuCCraftingRecipeLoader {

    private static Set<String> recipesToRemove = new ObjectOpenHashSet<>();

    private GTCEFuCCraftingRecipeLoader() {}

    public static void init() {
        breathabilityItems();
        if (ConfigHolder.overrideProjectEXGeneration) emcGeneration();
    }

    public static void initPost() {
        for (String recipeToRemove : recipesToRemove) {
            ModHandler.removeRecipeByName(recipeToRemove);
        }
        recipesToRemove = null;
    }

    private static void emcGeneration() {
        Material[] tiers = new Material[] { MarkerMaterials.Tier.ULV, MarkerMaterials.Tier.LV, MarkerMaterials.Tier.MV,
                MarkerMaterials.Tier.HV, MarkerMaterials.Tier.EV, MarkerMaterials.Tier.IV, MarkerMaterials.Tier.LuV,
                MarkerMaterials.Tier.ZPM, MarkerMaterials.Tier.UV, MarkerMaterials.Tier.UHV, MarkerMaterials.Tier.UEV,
                MarkerMaterials.Tier.UIV, MarkerMaterials.Tier.UXV, MarkerMaterials.Tier.OpV,
                MarkerMaterials.Tier.MAX };
        // ULV to UHV in lowtier, to MAX in hightier
        for (int i = 0; i <= (GregTechAPI.isHighTier() ? GTValues.MAX : GTValues.UHV); i++) {
            EnumTier tier = EnumTier.byMeta(i);
            String name;
            if (i == 0) name = "mk1";
            else name = tier.getName();
            recipesToRemove.add("projectex:relay/" + name);
            recipesToRemove.add("projectex:collector/" + name);
            if (i < 3) {
                recipesToRemove.add("projectex:relay/mk" + (i + 1) + "_alt");
                recipesToRemove.add("projectex:collector/mk" + (i + 1) + "_alt");
            }
            ModHandler.addShapedRecipe("relay/" + GTValues.VN[i], new ItemStack(ProjectEXItems.RELAY, 1, i),
                    "ACA", "AHA", "AMA",
                    'A', Blocks.OBSIDIAN,
                    'C', new UnificationEntry(OrePrefix.circuit, tiers[i]),
                    'H', MetaTileEntities.HULL[i].getStackForm(),
                    'M', tier.matter.get());
            ModHandler.addShapedRecipe("collector/" + GTValues.VN[i], new ItemStack(ProjectEXItems.COLLECTOR, 1, i),
                    "ACA", "AHA", "AMA",
                    'A', Blocks.GLOWSTONE,
                    'C', new UnificationEntry(OrePrefix.circuit, tiers[i]),
                    'H', MetaTileEntities.HULL[i].getStackForm(),
                    'M', tier.matter.get());
        }
    }

    private static void breathabilityItems() {
        ModHandler.addShapedRecipe("simple_gas_mask", GTCEFuCMetaItems.SIMPLE_GAS_MASK.getStackForm(),
                " T ", "WFW", "SMR",
                'W', Blocks.WOOL,
                'F', new UnificationEntry(OrePrefix.rotor, Materials.Steel),
                'T', Items.STRING,
                'S', MetaItems.STICKY_RESIN,
                'M', MetaItems.ELECTRIC_MOTOR_MV,
                'R', new UnificationEntry(OrePrefix.plate, Materials.Rubber));

        ModHandler.addShapedRecipe("gas_mask", GTCEFuCMetaItems.GAS_MASK.getStackForm(),
                "CkC", "RNR", "xMd",
                'N', MetaItems.NIGHTVISION_GOGGLES.getStackForm(),
                'C', MetaItems.CARBON_FIBER_PLATE.getStackForm(),
                'M', GTCEFuCMetaItems.SIMPLE_GAS_MASK.getStackForm(),
                'R', new UnificationEntry(OrePrefix.foil, Materials.Rubber));

        ModHandler.removeRecipeByOutput(MetaItems.NANO_HELMET.getStackForm());
        ModHandler.addShapedRecipe("nano_helmet", MetaItems.NANO_HELMET.getStackForm(),
                "PPP", "PNP", "xEd",
                'P', MetaItems.CARBON_FIBER_PLATE.getStackForm(),
                'N', GTCEFuCMetaItems.GAS_MASK.getStackForm(),
                'E', MetaItems.ENERGIUM_CRYSTAL.getStackForm());
    }
}
