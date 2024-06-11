package com.m_w_k.gtcefucontent.loaders.recipe;

import static com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials.*;
import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;

import java.util.Collection;

import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import gregtech.api.recipes.chance.output.ChancedOutputLogic;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.latmod.mods.projectex.item.ProjectEXItems;
import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;

import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.chance.output.ChancedOutputList;
import gregtech.api.recipes.chance.output.impl.ChancedItemOutput;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;

public final class GTCEFuCMiscRecipes {

    private GTCEFuCMiscRecipes() {}

    public static void init() {
        RecipeMaps.LARGE_CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(LiquidAir.getFluid(60000), Water.getFluid(60000))
                .input(OrePrefix.dust, Ash)
                .input(OrePrefix.dust, DarkAsh)
                .fluidOutputs(VaporSeedRaw.getFluid(20000))
                .duration(200)
                .EUt(VA[EV])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.CRYSTAL_ENDER)
                .fluidInputs(LightEssence.getFluid(32000))
                .output(GTCEFuCMetaItems.CRYSTAL_ENDLIGHT)
                .duration(1100).EUt(VA[LuV]).buildAndRegister();

        RecipeMaps.VACUUM_RECIPES.recipeBuilder()
                .fluidInputs(VaporSeedRaw.getFluid(6000),
                        Mercury.getFluid(54))
                .input(OrePrefix.dustTiny, Beryllium)
                .fluidOutputs(VaporSeed.getFluid(25))
                .duration(120)
                .EUt(VA[EV])
                .buildAndRegister();

        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .fluidInputs(Lutetium.getFluid(144))
                .input(OrePrefix.dust, Stone)
                .output(OrePrefix.dust, Lutetium)
                .EUt(VA[IV])
                .duration(90)
                .buildAndRegister();

        RecipeMaps.DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(VoidEssence.getFluid(8000))
                .fluidOutputs(
                        Helium.getFluid(2000),
                        Neon.getFluid(2000),
                        Argon.getFluid(2000),
                        Krypton.getFluid(500),
                        Xenon.getFluid(500),
                        Radon.getFluid(1000))
                .chancedOutput(GTCEFuCMetaItems.POWDER_VOID, 1000, 100)
                .duration(800)
                .EUt(VA[EV])
                .buildAndRegister();

        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.POWDER_VOID)
                .output(GTCEFuCMetaItems.POWDER_ENDER)
                .chancedOutput(GTCEFuCMetaItems.POWDER_STARLIGHT, 500, 150)
                .chancedOutput(GTCEFuCMetaItems.POWDER_STARLIGHT, 1000, 212)
                .duration(20000).EUt(24 * 4).buildAndRegister();

        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
                .fluidInputs(VoidEssence.getFluid(1000))
                .input(GTCEFuCMetaItems.POWDER_ENDER)
                .input(OrePrefix.dustTiny, SodaAsh)
                .output(GTCEFuCMetaItems.CRYSTAL_ENDER)
                .fluidOutputs(Water.getFluid(1000))
                .duration(1800).EUt(VA[ZPM]).buildAndRegister();

        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
                .fluidInputs(VaporSeed.getFluid(1000))
                .input(GTCEFuCMetaItems.POWDER_VOID)
                .input(OrePrefix.dustTiny, SodaAsh)
                .output(GTCEFuCMetaItems.CRYSTAL_VOID)
                .fluidOutputs(VaporSeedRaw.getFluid(1000))
                .duration(1800).EUt(VA[ZPM]).buildAndRegister();

        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
                .fluidInputs(VaporSeed.getFluid(1000))
                .input(GTCEFuCMetaItems.POWDER_STARLIGHT)
                .input(OrePrefix.dustTiny, SodaAsh)
                .output(GTCEFuCMetaItems.CRYSTAL_STARLIGHT)
                .fluidOutputs(VaporSeedRaw.getFluid(1000))
                .duration(1800).EUt(VA[ZPM]).buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.CRYSTAL_VOIDLIGHT)
                .fluidOutputs(UUMatter.getFluid(144))
                .output(GTCEFuCMetaItems.POWDER_ENDLIGHT)
                .duration(300).EUt(VA[UHV]).buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.CRYSTAL_ENDER)
                .fluidOutputs(ChargedEnder.getFluid(500))
                .duration(300).EUt(VA[LuV]).buildAndRegister();

        RecipeMaps.ELECTROLYZER_RECIPES.recipeBuilder()
                .input(MetaItems.NAN_CERTIFICATE)
                .fluidInputs(ExistenceEssence.getFluid(1200))
                .output(OrePrefix.dust, Neutronium, 64)
                .output(OrePrefix.dust, Neutronium, 64)
                .output(OrePrefix.dust, Neutronium, 64)
                .output(OrePrefix.dust, Neutronium, 64)
                .output(OrePrefix.dust, Neutronium, 64)
                .output(OrePrefix.dust, Neutronium, 64)
                .fluidOutputs(ConcentratedEffort.getFluid(150))
                .duration(8400).EUt(VA[UV]).buildAndRegister();

        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder()
                .input(ProjectEXItems.FINAL_STAR)
                .output(ProjectEXItems.FINAL_STAR_SHARD, 8)
                .duration(100).EUt(VA[UHV]).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCMetaItems.CRYSTAL_STARLIGHT.getStackForm(6),
                        new ItemStack(ProjectEXItems.COLLECTOR, 2, 9))
                .fluidInputs(UUMatter.getFluid(144))
                .outputs(new ItemStack(ProjectEXItems.COLLECTOR, 1, 15))
                .duration(200).EUt(VA[UHV]).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCMetaItems.CRYSTAL_ENDLIGHT.getStackForm(6),
                        new ItemStack(ProjectEXItems.RELAY, 2, 9))
                .fluidInputs(UUMatter.getFluid(216))
                .outputs(new ItemStack(ProjectEXItems.RELAY, 1, 15))
                .duration(200).EUt(VA[UHV]).buildAndRegister();

        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder()
                .inputs(GTCEFuCMetaItems.CRYSTAL_VOID.getStackForm(),
                        GTCEFuCMetaItems.CRYSTAL_ENDLIGHT.getStackForm(2),
                        GTCEFuCMetaItems.CRYSTAL_STARLIGHT.getStackForm(2),
                        GTCEFuCMetaItems.INFINITY_REAGENT.getStackForm(64))
                .input(OrePrefix.dust, TriniumReduced)
                .output(GTCEFuCMetaItems.CRYSTAL_VOIDLIGHT)
                .duration(1200).EUt(VA[ZPM]).buildAndRegister();

        RecipeMaps.BENDER_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.STELLAR_INGOT, 5)
                .output(GTCEFuCMetaItems.STELLAR_BALL)
                .duration(8000).EUt(VA[HV]).buildAndRegister();

        RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.REGRET)
                .output(OrePrefix.dust, DarkAsh)
                .duration(1).EUt(69).buildAndRegister();

        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, NaquadahEnriched, 2)
                .input(OrePrefix.dust, Osmiridium)
                .input(OrePrefix.dust, TriniumReduced)
                .circuitMeta(2)
                .output(OrePrefix.dust, UnstableNaquadahAlloy, 4)
                .duration(400).EUt(VA[IV]).buildAndRegister();

        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, Titanium, 1)
                .input(OrePrefix.dust, Boron, 2)
                .output(OrePrefix.dust, TitaniumBoride, 3)
                .duration(300).EUt(VA[EV]).buildAndRegister();

        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, Silicon, 1)
                .input(OrePrefix.dust, Carbon, 1)
                .output(OrePrefix.dust, SiliconCarbide, 2)
                .duration(200).EUt(VA[EV]).buildAndRegister();

        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, TitaniumBoride, 4)
                .input(OrePrefix.plate, SiliconCarbide)
                .output(OrePrefix.plate, UncuredThermostableCeramic, 5)
                .duration(600).EUt(VA[EV]).buildAndRegister();

        RecipeMaps.FURNACE_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, UncuredThermostableCeramic)
                .output(OrePrefix.plate, ThermostableCeramic)
                .duration(2000).EUt(VA[HV]).buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(MetaItems.COVER_SOLAR_PANEL, 64)
                .chancedOutput(MetaItems.COVER_SOLAR_PANEL, 64, 9000, 0)
                .fluidInputs(Ice.getFluid(13500))
                .fluidOutputs(FrozenStarlight.getFluid(12000))
                // TODO enforce overworld-only recipe
                .duration(150).EUt(VA[IV]).buildAndRegister();

        RecipeMaps.LARGE_CHEMICAL_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, UnstableNaquadahAlloy, 4)
                .fluidInputs(Infinitesimality.getFluid(99),
                        ExistenceEssence.getFluid(3000))
                .chancedOutput(OrePrefix.plate, NaquadahAlloy, 4, 8000, -1000)
                .chancedFluidOutput(DisruptionEssence.getFluid(1500), 1000, 1000)
                .chancedFluidOutput(StabilityEssence.getFluid(1500), 1000, 1000)
                .duration(330).EUt(VA[IV]).buildAndRegister();

        RecipeMaps.BREWING_RECIPES.recipeBuilder()
                .input(Items.CHORUS_FRUIT_POPPED)
                .fluidInputs(DisruptionEssence.getFluid(10))
                .fluidOutputs(ChaosEssence.getFluid(1))
                .duration(40).EUt(VA[LuV]).buildAndRegister();

        RecipeMaps.DISTILLERY_RECIPES.recipeBuilder()
                .input(OrePrefix.gemExquisite, TriniumReduced)
                .fluidInputs(StabilityEssence.getFluid(10))
                .fluidOutputs(OrderEssence.getFluid(1))
                .chancedOutput(OrePrefix.gemExquisite, TriniumReduced, 5000, 0)
                .duration(40).EUt(VA[LuV]).buildAndRegister();
    }

    public static void initPost() {
        ModHandler.removeRecipeByName("projectex:final_star_shard");
        ModHandler.removeRecipeByName("projectex:relay/final");
        ModHandler.removeRecipeByName("projectex:collector/final");
    }

    public static void cutterUpdate() {
        GTCEFuContent.log("Updating cutter recipes with Vapor Seed...");
        Collection<Recipe> oldRecipes = RecipeMaps.CUTTER_RECIPES.getRecipeList();
        oldRecipes.forEach(recipe -> {
            // replicate lubricant recipes, but halve the duration and switch to vapor seed
            if (recipe.hasInputFluid(Lubricant.getFluid(1))) {
                // Extrapolation of the greg formula for water, distilled water, and lubricant.
                int fluidAmount = (int) Math.max(50, Math.min(12500, recipe.getDuration() * recipe.getEUt() / 25.6));

                ChancedOutputList<ItemStack, ChancedItemOutput> chance = recipe.getChancedOutputs();

                RecipeMaps.CUTTER_RECIPES.recipeBuilder()
                        .fluidInputs(VaporSeedRaw.getFluid(fluidAmount))
                        // we know the cutter only has one input
                        .input(recipe.getInputs().get(0))
                        .outputs(recipe.getOutputs().toArray(new ItemStack[0]))
                        .chancedOutputs(chance.getChancedEntries())
                        .chancedOutputLogic(chance.getChancedOutputLogic())
                        .duration(recipe.getDuration() / 2)
                        .EUt(recipe.getEUt())
                        .buildAndRegister();
            }
        });
        GTCEFuContent.log("Finished updating cutter recipes.");
    }
}
