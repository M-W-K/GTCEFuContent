package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;
import gregtech.api.GTValues;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.item.ItemStack;

public class GTCEFuCMTELoader {

    private GTCEFuCMTELoader() {}

    public static void init() {
        ModHandler.addShapedRecipe(true, "naq_reactor_1",
                GTCEFuCMetaTileEntities.NAQ_REACTOR[0].getStackForm(),
                "FCF", "MHS", "XCX",
                'F', MetaItems.FIELD_GENERATOR_LuV.getStackForm(),
                'M', MetaItems.ELECTRIC_MOTOR_LuV,
                'S', MetaItems.SENSOR_LuV.getStackForm(),
                'H', MetaTileEntities.HULL[GTValues.ZPM].getStackForm(),
                'X', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.ZPM),
                'C', GTCEFuCMetaBlocks.HARDENED_CASING
                        .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.HYPERSTATIC_CASING));
        ModHandler.addShapedRecipe(true, "naq_reactor_2",
                GTCEFuCMetaTileEntities.NAQ_REACTOR[1].getStackForm(),
                "FCF", "MHS", "XCX",
                'F', MetaItems.FIELD_GENERATOR_ZPM.getStackForm(),
                'M', MetaItems.ELECTRIC_MOTOR_ZPM,
                'S', MetaItems.SENSOR_ZPM.getStackForm(),
                'H', MetaTileEntities.HULL[GTValues.UV].getStackForm(),
                'X', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.UV),
                'C', GTCEFuCMetaBlocks.HARDENED_CASING
                        .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.HYPERSTATIC_CASING));
        ModHandler.addShapedRecipe(true, "naq_reactor_3",
                GTCEFuCMetaTileEntities.NAQ_REACTOR[2].getStackForm(),
                "FCF", "MHS", "XCX",
                'F', MetaItems.FIELD_GENERATOR_UV.getStackForm(),
                'M', MetaItems.ELECTRIC_MOTOR_UV,
                'S', MetaItems.SENSOR_UV.getStackForm(),
                'H', MetaTileEntities.HULL[GTValues.UHV].getStackForm(),
                'X', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.UHV),
                'C', GTCEFuCMetaBlocks.HARDENED_CASING
                        .getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.HYPERSTATIC_CASING));
    }
}
