package com.m_w_k.gtcefucontent.loaders.recipe;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import com.latmod.mods.projectex.item.ProjectEXItems;
import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;

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
                .fluidOutputs(GTCEFuCMaterials.VaporSeedRaw.getFluid(20000))
                .duration(200)
                .EUt(GTValues.VA[GTValues.EV])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.CRYSTAL_ENDER)
                .fluidInputs(GTCEFuCMaterials.LightEssence.getFluid(32000))
                .output(GTCEFuCMetaItems.CRYSTAL_ENDLIGHT)
                .duration(1100).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        RecipeMaps.VACUUM_RECIPES.recipeBuilder()
                .fluidInputs(GTCEFuCMaterials.VaporSeedRaw.getFluid(6000),
                        Materials.Mercury.getFluid(54))
                .input(OrePrefix.dustTiny, Materials.Beryllium)
                .fluidOutputs(GTCEFuCMaterials.VaporSeed.getFluid(25))
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

        RecipeMaps.DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(GTCEFuCMaterials.VoidEssence.getFluid(8000))
                .fluidOutputs(
                        Materials.Helium.getFluid(2000),
                        Materials.Neon.getFluid(2000),
                        Materials.Argon.getFluid(2000),
                        Materials.Krypton.getFluid(500),
                        Materials.Xenon.getFluid(500),
                        Materials.Radon.getFluid(1000))
                .chancedOutput(GTCEFuCMetaItems.POWDER_VOID, 1000, 100)
                .duration(800)
                .EUt(GTValues.VA[GTValues.EV])
                .buildAndRegister();

        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.POWDER_VOID)
                .output(GTCEFuCMetaItems.POWDER_ENDER)
                .chancedOutput(GTCEFuCMetaItems.POWDER_STARLIGHT, 500, 150)
                .chancedOutput(GTCEFuCMetaItems.POWDER_STARLIGHT, 1000, 212)
                .duration(20000).EUt(24 * 4).buildAndRegister();

        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
                .fluidInputs(GTCEFuCMaterials.VoidEssence.getFluid(1000))
                .input(GTCEFuCMetaItems.POWDER_ENDER)
                .input(OrePrefix.dustTiny, Materials.SodaAsh)
                .output(GTCEFuCMetaItems.CRYSTAL_ENDER)
                .fluidOutputs(Materials.Water.getFluid(1000))
                .duration(1800).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();

        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
                .fluidInputs(GTCEFuCMaterials.VaporSeed.getFluid(1000))
                .input(GTCEFuCMetaItems.POWDER_VOID)
                .input(OrePrefix.dustTiny, Materials.SodaAsh)
                .output(GTCEFuCMetaItems.CRYSTAL_VOID)
                .fluidOutputs(GTCEFuCMaterials.VaporSeedRaw.getFluid(1000))
                .duration(1800).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();

        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
                .fluidInputs(GTCEFuCMaterials.VaporSeed.getFluid(1000))
                .input(GTCEFuCMetaItems.POWDER_STARLIGHT)
                .input(OrePrefix.dustTiny, Materials.SodaAsh)
                .output(GTCEFuCMetaItems.CRYSTAL_STARLIGHT)
                .fluidOutputs(GTCEFuCMaterials.VaporSeedRaw.getFluid(1000))
                .duration(1800).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.CRYSTAL_VOIDLIGHT)
                .fluidOutputs(Materials.UUMatter.getFluid(144))
                .output(GTCEFuCMetaItems.POWDER_ENDLIGHT)
                .duration(300).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.CRYSTAL_ENDER)
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
                .fluidOutputs(GTCEFuCMaterials.ExperienceEssence.getFluid(200))
                .duration(8400).EUt(GTValues.VA[GTValues.UV]).buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .input(ProjectEXItems.FINAL_STAR)
                .output(ProjectEXItems.FINAL_STAR_SHARD, 8)
                .duration(100).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCMetaItems.CRYSTAL_STARLIGHT.getStackForm(6),
                        new ItemStack(ProjectEXItems.COLLECTOR, 2, 9))
                .fluidInputs(Materials.UUMatter.getFluid(144))
                .outputs(new ItemStack(ProjectEXItems.COLLECTOR, 1, 15))
                .duration(200).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCMetaItems.CRYSTAL_ENDLIGHT.getStackForm(6),
                        new ItemStack(ProjectEXItems.RELAY, 2, 9))
                .fluidInputs(Materials.UUMatter.getFluid(216))
                .outputs(new ItemStack(ProjectEXItems.RELAY, 1, 15))
                .duration(200).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();

        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder()
                .inputs(GTCEFuCMetaItems.CRYSTAL_VOID.getStackForm(),
                        GTCEFuCMetaItems.CRYSTAL_ENDLIGHT.getStackForm(2),
                        GTCEFuCMetaItems.CRYSTAL_STARLIGHT.getStackForm(2),
                        GTCEFuCMetaItems.INFINITY_REAGENT.getStackForm(64))
                .input(OrePrefix.dust, GTCEFuCMaterials.TriniumReduced)
                .output(GTCEFuCMetaItems.CRYSTAL_VOIDLIGHT)
                .duration(1200).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();

        RecipeMaps.BENDER_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.STELLAR_INGOT, 5)
                .output(GTCEFuCMetaItems.STELLAR_BALL)
                .duration(8000).EUt(GTValues.VA[GTValues.HV]).buildAndRegister();
    }

    public static void initPost() {
        GTCEFuContent.log("Removing recipes, DON'T BE SCARED OF FML's WARNING ABOUT DANGEROUS ALTERNATIVE PREFIX");
        ModHandler.removeRecipeByName("projectex:final_star_shard");
        ModHandler.removeRecipeByName("projectex:relay/final");
        ModHandler.removeRecipeByName("projectex:collector/final");
    }

    public static void cutterUpdate() {
        GTCEFuContent.log("Updating cutter recipes with Vapor Seed...");
        Collection<Recipe> oldRecipes = RecipeMaps.CUTTER_RECIPES.getRecipeList();
        oldRecipes.forEach(recipe -> {
            // replicate lubricant recipes, but halve the duration and switch to vapor seed
            if (recipe.hasInputFluid(Materials.Lubricant.getFluid(1))) {
                // Extrapolation of the greg formula for water, distilled water, and lubricant.
                int fluidAmount = (int) Math.max(50, Math.min(12500, recipe.getDuration() * recipe.getEUt() / 25.6));
                RecipeMaps.CUTTER_RECIPES.recipeBuilder()
                        .fluidInputs(GTCEFuCMaterials.VaporSeedRaw.getFluid(fluidAmount))
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
