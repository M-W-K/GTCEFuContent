package com.m_w_k.gtcefucontent.loaders.recipe;

import java.util.Objects;

import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockHardenedCasing;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.init.ModObject;
import gregtech.api.GTValues;
import gregtech.api.recipes.ingredients.nbtmatch.NBTCondition;
import gregtech.api.recipes.ingredients.nbtmatch.NBTMatcher;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;

public final class GTCEFuCAntimatterCompressorRecipes {

    private GTCEFuCAntimatterCompressorRecipes() {}

    public static void init() {
        // while this may look worse, it is actually significantly better due to perfect overclocking
        GTCEFuCRecipeMaps.ANTIMATTER_COMPRESSOR_RECIPES.recipeBuilder()
                .fluidInputs(Materials.Nickel.getPlasma(24000))
                .input(OrePrefix.block, Materials.Neutronium, 64)
                .input(OrePrefix.block, Materials.Neutronium, 64)
                .output(MetaItems.NAN_CERTIFICATE)
                .duration(Integer.MAX_VALUE).EUt(GTValues.VA[GTValues.LV])
                .EUToStart(10000000000L)
                .buildAndRegister();

        // I mean, the tooltip does say "creating the infinite"
        GTCEFuCRecipeMaps.ANTIMATTER_COMPRESSOR_RECIPES.recipeBuilder()
                .fluidInputs(Materials.Nickel.getPlasma(1000))
                .inputs(GTCEFuCInfinityExtractorRecipes.block3)
                .inputs(GTCEFuCMetaBlocks.HARDENED_CASING.getItemVariant(GTCEFuCBlockHardenedCasing.CasingType.INDESTRUCTIBLE_CASING))
                .input(OrePrefix.dust, Materials.Stone, 6)
                .output(Blocks.BEDROCK)
                .duration(1000).EUt(GTValues.VA[GTValues.UHV])
                .EUToStart(120000000000L)
                .buildAndRegister();

        GTCEFuCRecipeMaps.ANTIMATTER_COMPRESSOR_RECIPES.recipeBuilder()
                .fluidInputs(Materials.Nickel.getPlasma(64000),
                        GTCEFuCMaterials.LightEssence.getFluid(1024000))
                .input(OrePrefix.dustSmall, Materials.Neutronium)
                .input(GTCEFuCMetaItems.CRYSTAL_VOID)
                .input(GTCEFuCMetaItems.POWDER_ENDLIGHT)
                .output(GTCEFuCMetaItems.STELLAR_NUGGET)
                .duration(6000).EUt(GTValues.VA[GTValues.UHV])
                .EUToStart(50000000000L)
                .buildAndRegister();

        // the fabled winning of minecraft
        GTCEFuCRecipeMaps.ANTIMATTER_COMPRESSOR_RECIPES.recipeBuilder()
                // 4096000 = 1 UHV hatch
                .fluidInputs(Materials.Nickel.getPlasma(4096000 * 4),
                        GTCEFuCMaterials.ExperienceEssence.getFluid(1000))
                .inputNBT(MetaItems.ULTIMATE_BATTERY, NBTMatcher.ANY, NBTCondition.ANY)
                .input(GTCEFuCMetaItems.STELLAR_BALL)
                .outputs(MetaItems.ULTIMATE_BATTERY.getChargedStack(Long.MAX_VALUE))
                .duration(Integer.MAX_VALUE).EUt(GTValues.VA[GTValues.EV])
                .EUToStart(160000000000L).buildAndRegister();
    }
}
