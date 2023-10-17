package com.m_w_k.gtcefucontent.api.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;

public final class GTCEFuCRecipeMaps {

    public static final RecipeMap<SimpleRecipeBuilder> INFINITY_EXTRACTOR_RECIPES = new RecipeMap<>("infinity_extractor", 2, 4,
            0, 0, new SimpleRecipeBuilder(), false)
            .setSlotOverlay(false, false, false, GuiTextures.FURNACE_OVERLAY_1)
            .setSlotOverlay(false, false, true, GuiTextures.INT_CIRCUIT_OVERLAY)
            .setSlotOverlay(true, false, false, GuiTextures.DUST_OVERLAY)
            .setSlotOverlay(true, false, true, GuiTextures.DUST_OVERLAY)
            .setSound(GTSoundEvents.FIRE);

    public static final RecipeMap<FuelRecipeBuilder> SYMPATHETIC_COMBUSTOR_RECIPES = new RecipeMap<>("sympathetic_combustor", 6, 3,
            6, 0, new FuelRecipeBuilder(), false)
            .setSlotOverlay(false, false, false, GuiTextures.SLOT)
            .setSlotOverlay(false, false, true, GuiTextures.SLOT)
            .setSlotOverlay(true, false, false, GuiTextures.SLOT)
            .setSlotOverlay(true, false, true, GuiTextures.SLOT)
            .setSlotOverlay(false, true, false, GuiTextures.FURNACE_OVERLAY_2)
            .setSlotOverlay(false, true, true, GuiTextures.FURNACE_OVERLAY_2)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressWidget.MoveType.HORIZONTAL)
            .allowEmptyOutput()
            .setSound(GTSoundEvents.COMBUSTION);

    private GTCEFuCRecipeMaps() {}
}
