package com.m_w_k.gtcefucontent.loaders.recipe;

import static gregtech.api.recipes.RecipeMaps.ASSEMBLER_RECIPES;

import com.m_w_k.gtcefucontent.api.capability.IHEUComponent;
import com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.multiblockpart.MetaTileEntityHEUComponent;

import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;

public final class GTCEFuCHEUComponentLoader {

    private GTCEFuCHEUComponentLoader() {}

    public static void init() {
        MetaTileEntityHEUComponent hStandard = GTCEFuCMetaTileEntities.HEU_COMPONENTS
                .get(IHEUComponent.HEUComponentType.H_STANDARD);
        MetaTileEntityHEUComponent hExpanded = GTCEFuCMetaTileEntities.HEU_COMPONENTS
                .get(IHEUComponent.HEUComponentType.H_EXPANDED);
        MetaTileEntityHEUComponent hConductive = GTCEFuCMetaTileEntities.HEU_COMPONENTS
                .get(IHEUComponent.HEUComponentType.H_CONDUCTIVE);
        MetaTileEntityHEUComponent eStandard = GTCEFuCMetaTileEntities.HEU_COMPONENTS
                .get(IHEUComponent.HEUComponentType.E_STANDARD);
        MetaTileEntityHEUComponent eReturning = GTCEFuCMetaTileEntities.HEU_COMPONENTS
                .get(IHEUComponent.HEUComponentType.E_RETURNING);

        // did you know diamond is incredibly thermally conductive?
        ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, GCYMMaterials.TantalumCarbide, 8)
                .input(OrePrefix.plate, Materials.Diamond, 16)
                .input(MetaTileEntities.HULL[GTValues.LuV])
                .input(MetaItems.ELECTRIC_PUMP_LuV)
                .input(OrePrefix.foil, Materials.Invar, 6)
                .input(OrePrefix.bolt, Materials.HSSE, 32)
                .fluidInputs(Materials.TinAlloy.getFluid(576))
                .output(hStandard, 4)
                .duration(200).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        ASSEMBLER_RECIPES.recipeBuilder()
                .input(hStandard)
                .input(OrePrefix.frameGt, GCYMMaterials.TantalumCarbide, 2)
                .input(OrePrefix.foil, GTCEFuCMaterials.NaquadricAlloy, 10)
                .input(OrePrefix.stickLong, Materials.Invar, 2)
                .input(OrePrefix.bolt, Materials.HSSE, 8)
                .fluidInputs(Materials.TinAlloy.getFluid(576))
                .output(hExpanded)
                .duration(200).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        ModHandler.addShapelessRecipe("h_expanded_downgrade", hStandard.getStackForm(),
                hExpanded.getStackForm(), 'w');

        ASSEMBLER_RECIPES.recipeBuilder()
                .input(hStandard)
                .input(OrePrefix.plate, Materials.Diamond, 4)
                .input(OrePrefix.foil, GTCEFuCMaterials.NaquadricAlloy, 10)
                .input(OrePrefix.stickLong, Materials.Invar, 2)
                .input(OrePrefix.bolt, Materials.HSSE, 8)
                .fluidInputs(Materials.TinAlloy.getFluid(576))
                .output(hConductive)
                .duration(200).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        ModHandler.addShapelessRecipe("h_conductive_downgrade", hStandard.getStackForm(),
                hConductive.getStackForm(), 'w');

        ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, GCYMMaterials.TantalumCarbide, 2)
                .input(MetaTileEntities.HULL[GTValues.LuV])
                .input(MetaItems.FLUID_REGULATOR_LUV)
                .input(OrePrefix.stick, GCYMMaterials.MolybdenumDisilicide, 2)
                .input(OrePrefix.wireFine, Materials.Naquadah, 8)
                .input(OrePrefix.foil, Materials.Invar, 6)
                .input(OrePrefix.screw, Materials.HSSE, 16)
                .fluidInputs(Materials.TinAlloy.getFluid(576))
                .output(eStandard)
                .duration(200).EUt(GTValues.VA[GTValues.LuV]).buildAndRegister();

        ASSEMBLER_RECIPES.recipeBuilder()
                .input(eStandard)
                .input(MetaItems.FLUID_REGULATOR_LUV)
                .input(OrePrefix.stickLong, GCYMMaterials.MolybdenumDisilicide, 2)
                .input(OrePrefix.wireFine, Materials.Naquadah, 8)
                .input(OrePrefix.screw, Materials.HSSE, 8)
                .fluidInputs(Materials.TinAlloy.getFluid(576))
                .output(eReturning)
                .duration(100).EUt(GTValues.VA[GTValues.ZPM]).buildAndRegister();
    }
}
