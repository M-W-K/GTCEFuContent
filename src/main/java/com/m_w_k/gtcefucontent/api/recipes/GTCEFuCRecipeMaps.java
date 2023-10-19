package com.m_w_k.gtcefucontent.api.recipes;

import com.m_w_k.gtcefucontent.GTCEFuContentSoundEvents;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.BlastRecipeBuilder;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.recipes.builders.FusionRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;

import java.util.ArrayList;
import java.util.List;

public final class GTCEFuCRecipeMaps {

    public static final RecipeMap<SimpleRecipeBuilder> INFINITY_EXTRACTOR_RECIPES = new RecipeMap<>("infinity_extractor",
            2, 4, 0, 0, new SimpleRecipeBuilder(), false)
            .setSlotOverlay(false, false, false, GuiTextures.FURNACE_OVERLAY_1)
            .setSlotOverlay(false, false, true, GuiTextures.INT_CIRCUIT_OVERLAY)
            .setSlotOverlay(true, false, false, GuiTextures.DUST_OVERLAY)
            .setSlotOverlay(true, false, true, GuiTextures.DUST_OVERLAY)
            .setSound(GTSoundEvents.FIRE);

    public static final RecipeMap<FuelRecipeBuilder> SYMPATHETIC_COMBUSTOR_RECIPES = new RecipeMap<>("sympathetic_combustor",
            3, 0, 6, 0, new FuelRecipeBuilder(), false)
            .setSlotOverlay(false, false, false, GuiTextures.FURNACE_OVERLAY_1)
            .setSlotOverlay(false, false, true, GuiTextures.INT_CIRCUIT_OVERLAY)
            .setSlotOverlay(false, true, false, GuiTextures.FURNACE_OVERLAY_2)
            .setSlotOverlay(false, true, true, GuiTextures.FURNACE_OVERLAY_2)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressWidget.MoveType.HORIZONTAL)
            .allowEmptyOutput()
            .setSound(GTSoundEvents.COMBUSTION);

    public static final RecipeMap<BlastRecipeBuilder> FORGING_FURNACE_RECIPES = new RecipeMap<>("forging_furnace",
            3, 3, 2, 1, new BlastRecipeBuilder(), false)
            .setSlotOverlay(false, false, false, GuiTextures.FURNACE_OVERLAY_1)
            .setSlotOverlay(false, false, true, GuiTextures.INT_CIRCUIT_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_EXTRUDER, ProgressWidget.MoveType.HORIZONTAL)
            .setSound(GTCEFuContentSoundEvents.FORGING_FURNACE);

    public static final List<RecipeMap<FusionRecipeBuilder>> FUSION_STACK_RECIPE_MAPS = new ArrayList<>(3) {};

    // Trying to generate the list contents in-place throws a compiler error for some reason.
    public static void init() {
        FUSION_STACK_RECIPE_MAPS.add(fusionRecipeBuilderRecipeMap("stack"));
        FUSION_STACK_RECIPE_MAPS.add(fusionRecipeBuilderRecipeMap("array"));
        FUSION_STACK_RECIPE_MAPS.add(fusionRecipeBuilderRecipeMap("complex"));
    }

    private static RecipeMap<FusionRecipeBuilder> fusionRecipeBuilderRecipeMap(String id) {
        return new RecipeMap<>("fusion_stack." + id,
                0, 0, 2, 1, new FusionRecipeBuilder(), false)
                .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressWidget.MoveType.HORIZONTAL)
                .setSound(GTSoundEvents.ARC);
    }


    private GTCEFuCRecipeMaps() {}
}
