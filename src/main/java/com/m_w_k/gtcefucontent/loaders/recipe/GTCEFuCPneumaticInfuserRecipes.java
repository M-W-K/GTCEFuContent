package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.init.ModObject;
import gregtech.api.GTValues;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public final class GTCEFuCPneumaticInfuserRecipes {
    private GTCEFuCPneumaticInfuserRecipes() {}

    public static void init() {
        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, Materials.Steel)
                .fluidInputs(Materials.Lava.getFluid(1000),
                        Materials.Oxygen.getFluid(100))
                .output(OrePrefix.ingot, Materials.DamascusSteel)
                .duration(500).EUt(GTValues.VA[GTValues.EV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, Materials.Obsidian, 4)
                .fluidInputs(Materials.Tungsten.getFluid(144),
                        Materials.DamascusSteel.getFluid(144))
                .outputs(new ItemStack(ModObject.itemAlloyIngot.getItemNN(), 1, 6))
                .duration(750).EUt(GTValues.VA[GTValues.IV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .input(MetaItems.COVER_SOLAR_PANEL, 64)
                .chancedOutput(MetaItems.COVER_SOLAR_PANEL, 64, 9000, 0)
                .fluidInputs(Materials.Glowstone.getFluid(4800),
                        Materials.Ice.getFluid(13500))
                .fluidOutputs(new FluidStack(Fluids.LIQUID_SUNSHINE.getFluid(), 14000))
                .duration(200).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(new FluidStack(Fluids.LIQUID_SUNSHINE.getFluid(), 10000),
                        Materials.Blaze.getFluid(1296))
                .fluidOutputs(new FluidStack(Fluids.FIRE_WATER.getFluid(), 10000))
                .duration(250).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(new FluidStack(Fluids.CLOUD_SEED_CONCENTRATED.getFluid(), 100),
                        Materials.Hydrogen.getFluid(30000))
                .fluidOutputs(new FluidStack(Fluids.VAPOR_OF_LEVITY.getFluid(), 5000))
                .duration(400).EUt(GTValues.VA[GTValues.IV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(GTCEFuCInfinityExtractorRecipes.dust)
                .fluidInputs(new FluidStack(Fluids.CLOUD_SEED_CONCENTRATED.getFluid(), 100),
                        Materials.Radon.getFluid(1000))
                .fluidOutputs(new FluidStack(Fluids.ENDER_DISTILLATION.getFluid(), 5000))
                .duration(400).EUt(GTValues.VA[GTValues.IV]).buildAndRegister();

        GTCEFuCRecipeMaps.PNEUMATIC_INFUSER_RECIPES.recipeBuilder()
                .inputs(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, 35))
                .fluidInputs(new FluidStack(Fluids.CLOUD_SEED_CONCENTRATED.getFluid(), 300),
                        Materials.Plutonium239.getFluid(72),
                        Materials.Argon.getPlasma(64000))
                .fluidOutputs(Materials.McGuffium239.getFluid(72))
                .duration(1380).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();
    }
}
