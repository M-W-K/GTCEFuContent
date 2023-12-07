package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class GTCEFuCCraftingRecipeLoader {

    private GTCEFuCCraftingRecipeLoader() {}

    public static void init() {

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
