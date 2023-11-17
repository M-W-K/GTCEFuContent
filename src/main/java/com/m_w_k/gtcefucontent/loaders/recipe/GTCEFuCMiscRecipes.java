package com.m_w_k.gtcefucontent.loaders.recipe;

import java.util.Collection;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.latmod.mods.projectex.item.ProjectEXItems;
import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.material.Material;
import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;

public final class GTCEFuCMiscRecipes {

    private GTCEFuCMiscRecipes() {}

    public static void init() {
        RecipeMaps.PLASMA_GENERATOR_FUELS.recipeBuilder()
                .fluidInputs(GTCEFuCMaterials.ChargedEnder.getFluid(1))
                .fluidOutputs(Materials.Beryllium.getFluid(1))
                .duration(750)
                .EUt((int) GTValues.V[GTValues.EV])
                .buildAndRegister();

        RecipeMaps.LARGE_CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(Materials.LiquidAir.getFluid(60000), Materials.Water.getFluid(60000))
                .input(OrePrefix.dust, Materials.Ash)
                .input(OrePrefix.dust, Materials.DarkAsh)
                .fluidOutputs(new FluidStack(Fluids.CLOUD_SEED.getFluid(), 20000))
                .duration(200)
                .EUt(GTValues.VA[GTValues.EV])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .inputs(Material.ENDER_CRYSTAL.getStack())
                .fluidInputs(Materials.Glowstone.getFluid(76000))
                .outputs(Material.VIBRANT_CRYSTAL.getStack())
                .duration(1100).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        RecipeMaps.VACUUM_RECIPES.recipeBuilder()
                .fluidInputs(new FluidStack(Fluids.CLOUD_SEED.getFluid(), 6000))
                .fluidOutputs(new FluidStack(Fluids.CLOUD_SEED_CONCENTRATED.getFluid(), 25))
                .duration(120)
                .EUt(GTValues.VA[GTValues.EV])
                .buildAndRegister();

        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .fluidInputs(Materials.Lutetium.getFluid(144))
                .input(OrePrefix.dust, Materials.Stone)
                .output(OrePrefix.dust, Materials.Lutetium)
                .EUt(GTValues.VA[GTValues.IV])
                .duration(90)
                .buildAndRegister();

        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder()
                .input(ModObject.blockDarkIronBars.getItemNN(), 20)
                .input(OrePrefix.block, Materials.Obsidian, 3)
                .inputs(GTCEFuCInfinityExtractorRecipes.block1)
                .output(ModObject.blockReinforcedObsidian.getItemNN())
                .duration(3000).EUt(GTValues.VA[GTValues.IV]).buildAndRegister();

        RecipeMaps.DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(new FluidStack(Fluids.ENDER_DISTILLATION.getFluid(), 8000))
                .fluidOutputs(
                        Materials.Helium.getFluid(2000),
                        Materials.Neon.getFluid(2000),
                        Materials.Argon.getFluid(2000),
                        Materials.Krypton.getFluid(500),
                        Materials.Xenon.getFluid(500),
                        Materials.Radon.getFluid(1000))
                .chancedOutput(Material.POWDER_PRECIENT.getStack(), 1000, 100)
                .duration(800)
                .EUt(GTValues.VA[GTValues.EV])
                .buildAndRegister();

        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder()
                .inputs(Material.POWDER_PRECIENT.getStack())
                .outputs(Material.POWDER_ENDER_CYSTAL.getStack())
                .chancedOutput(Material.POWDER_PULSATING.getStack(), 500, 150)
                .chancedOutput(Material.POWDER_PULSATING.getStack(), 1000, 212)
                .duration(200).EUt(24).buildAndRegister();

        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
                .fluidInputs(new FluidStack(Fluids.ENDER_DISTILLATION.getFluid(), 1000))
                .inputs(Material.POWDER_ENDER_CYSTAL.getStack())
                .input(OrePrefix.dustTiny, Materials.SodaAsh)
                .outputs(Material.ENDER_CRYSTAL.getStack())
                .fluidOutputs(Materials.Water.getFluid(1000))
                .duration(1800).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();

        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
                .fluidInputs(new FluidStack(Fluids.CLOUD_SEED_CONCENTRATED.getFluid(), 1000))
                .inputs(Material.POWDER_PRECIENT.getStack())
                .input(OrePrefix.dustTiny, Materials.SodaAsh)
                .outputs(Material.PRECIENT_CRYSTAL.getStack())
                .fluidOutputs(new FluidStack(Fluids.CLOUD_SEED.getFluid(), 1000))
                .duration(1800).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();

        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
                .fluidInputs(new FluidStack(Fluids.CLOUD_SEED_CONCENTRATED.getFluid(), 1000))
                .inputs(Material.POWDER_PULSATING.getStack())
                .input(OrePrefix.dustTiny, Materials.SodaAsh)
                .outputs(Material.PULSATING_CRYSTAL.getStack())
                .fluidOutputs(new FluidStack(Fluids.CLOUD_SEED.getFluid(), 1000))
                .duration(1800).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                .inputs(Material.WEATHER_CRYSTAL.getStack())
                .fluidOutputs(Materials.UUMatter.getFluid(144))
                .outputs(Material.POWDER_VIBRANT.getStack())
                .duration(300).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                .inputs(Material.ENDER_CRYSTAL.getStack())
                .fluidOutputs(GTCEFuCMaterials.ChargedEnder.getFluid(500))
                .duration(300).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        // Yep. That's exactly how much neutronium I'm expecting them to make.
        RecipeMaps.ELECTROLYZER_RECIPES.recipeBuilder()
                .input(MetaItems.NAN_CERTIFICATE)
                .fluidInputs(Materials.McGuffium239.getFluid(216))
                .output(OrePrefix.dust, Materials.Neutronium, 64)
                .output(OrePrefix.dust, Materials.Neutronium, 64)
                .output(OrePrefix.dust, Materials.Neutronium, 64)
                .output(OrePrefix.dust, Materials.Neutronium, 64)
                .output(OrePrefix.dust, Materials.Neutronium, 64)
                .output(OrePrefix.dust, Materials.Neutronium, 64)
                .fluidOutputs(new FluidStack(Fluids.XP_JUICE.getFluid(), 200))
                .duration(8400).EUt(GTValues.VA[GTValues.UV]).buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .input(ProjectEXItems.FINAL_STAR)
                .output(ProjectEXItems.FINAL_STAR_SHARD, 8)
                .duration(100).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(Material.VIBRANT_CRYSTAL.getStack(6),
                        new ItemStack(ProjectEXItems.COLLECTOR, 2, 9))
                .fluidInputs(Materials.UUMatter.getFluid(144))
                .outputs(new ItemStack(ProjectEXItems.COLLECTOR, 1, 15))
                .duration(200).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(ModObject.blockReinforcedObsidian.getItemNN(), 6)
                .inputs(new ItemStack(ProjectEXItems.RELAY, 2, 9))
                .fluidInputs(Materials.UUMatter.getFluid(216))
                .outputs(new ItemStack(ProjectEXItems.RELAY, 1, 15))
                .duration(200).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();

        if (GTCEFuCUtil.stellarAlloyCheck()) {
            RecipeMaps.BENDER_RECIPES.recipeBuilder()
                    .inputs(new ItemStack(Objects.requireNonNull(ModObject.itemAlloyEndergyIngot.getItem()), 5, 3))
                    .outputs(new ItemStack(Objects.requireNonNull(ModObject.itemAlloyEndergyBall.getItem()), 1, 3))
                    .duration(8000).EUt(GTValues.VA[GTValues.HV]).buildAndRegister();

            // I know that this is incredibly annoying. That's the point.
            RecipeMaps.PACKER_RECIPES.recipeBuilder()
                    .inputs(new ItemStack(Objects.requireNonNull(ModObject.itemAlloyEndergyNugget.getItem()), 9, 3))
                    .input(ProjectEXItems.FINAL_STAR_SHARD)
                    .outputs(new ItemStack(Objects.requireNonNull(ModObject.itemAlloyEndergyIngot.getItem()), 1, 3))
                    .duration(1).EUt(GTValues.VA[GTValues.UV]).buildAndRegister();
        }
    }

    public static void initPost() {
        GTCEFuContent.log("Removing recipes, DON'T BE SCARED OF FML's WARNING ABOUT DANGEROUS ALTERNATIVE PREFIX");
        if (GTCEFuCUtil.stellarAlloyCheck()) {
            ModHandler.removeRecipeByName("enderio:auto_stellar_alloy_9_nuggets_to_1_ingot");
            ModHandler.removeRecipeByName("enderio:auto_stellar_alloy_1_ingot_to_9_nuggets");
            ModHandler.removeRecipeByName("enderio:auto_stellar_alloy_9_ingots_to_1_block");
            ModHandler.removeRecipeByName("enderio:auto_stellar_alloy_1_block_to_9_ingots");
        }
        ModHandler.removeRecipeByName("enderio:pulsating_crystal");
        ModHandler.removeRecipeByName("enderio:vibrant_crystal");
        ModHandler.removeRecipeByName("enderio:reinforced_obsidian");
        ModHandler.removeRecipeByName("projectex:final_star_shard");
        ModHandler.removeRecipeByName("projectex:relay/final");
        ModHandler.removeRecipeByName("projectex:collector/final");
    }

    public static void cutterUpdate() {
        GTCEFuContent.log("Updating cutter recipes with Cloud Seed...");
        Collection<Recipe> oldRecipes = RecipeMaps.CUTTER_RECIPES.getRecipeList();
        oldRecipes.forEach(recipe -> {
            // replicate lubricant recipes, but halve the duration and switch to cloud seed
            if (recipe.hasInputFluid(Materials.Lubricant.getFluid(1))) {
                // Extrapolation of the greg formula for water, distilled water, and lubricant.
                int fluidAmount = (int) Math.max(50, Math.min(12500, recipe.getDuration() * recipe.getEUt() / 25.6));
                RecipeMaps.CUTTER_RECIPES.recipeBuilder()
                        .fluidInputs(new FluidStack(Fluids.CLOUD_SEED.getFluid(), fluidAmount))
                        // we know the cutter only has one input
                        .input(recipe.getInputs().get(0))
                        .outputs(recipe.getOutputs().toArray(new ItemStack[0]))
                        .chancedOutputs(recipe.getChancedOutputs())
                        .duration(recipe.getDuration() / 2)
                        .EUt(recipe.getEUt())
                        .buildAndRegister();
            }
        });
        GTCEFuContent.log("Finished updating cutter recipes.");
    }
}
