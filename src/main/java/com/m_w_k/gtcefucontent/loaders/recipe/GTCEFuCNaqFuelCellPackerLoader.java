package com.m_w_k.gtcefucontent.loaders.recipe;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;
import gregtech.api.recipes.chance.output.ChancedOutputLogic;
import gregtech.common.items.MetaItems;
import net.minecraft.item.ItemStack;

import static com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials.DisruptionEssence;
import static com.m_w_k.gtcefucontent.api.unification.GTCEFuCMaterials.UnstableNaquadahAlloy;
import static gregtech.api.GTValues.LuV;
import static gregtech.api.GTValues.VA;

public class GTCEFuCNaqFuelCellPackerLoader {

    private GTCEFuCNaqFuelCellPackerLoader() {}

    public static void init() {
        standard(MetaItems.ROBOT_ARM_IV.getStackForm(4), 8000);
        standard(MetaItems.ROBOT_ARM_LuV.getStackForm(4), 8500);
        standard(MetaItems.ROBOT_ARM_ZPM.getStackForm(4), 9000);
        standard(MetaItems.ROBOT_ARM_UV.getStackForm(4), 9500);
    }

    private static void standard(ItemStack arm, int chance) {
        GTCEFuCRecipeMaps.NAQ_FUEL_CELL_PACKER_RECIPES.recipeBuilder()
                .input(GTCEFuCMetaItems.NAQ_FUEL_CELL_EMPTY)
                .inputs(arm)
                .fluidInputs(UnstableNaquadahAlloy.getFluid(45), DisruptionEssence.getFluid(45))
                .chancedOutput(GTCEFuCMetaItems.NAQ_FUEL_CELL.getStackForm(), chance, 0)
                .chancedOutput(arm, chance, 0)
                .chancedOutputLogic(ChancedOutputLogic.AND)
                .duration(350).EUt(VA[LuV]).buildAndRegister();
    }
}
