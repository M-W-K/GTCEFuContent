package com.m_w_k.gtcefucontent.api.recipes;

import java.util.ArrayList;
import java.util.List;

import com.m_w_k.gtcefucontent.GTCEFuContentSoundEvents;
import com.m_w_k.gtcefucontent.api.recipes.builders.ExchangerRecipeBuilder;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.BlastRecipeBuilder;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.recipes.builders.FusionRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;

public final class GTCEFuCRecipeMaps {

    public static final RecipeMap<SimpleRecipeBuilder> INFINITY_EXTRACTOR_RECIPES = new RecipeMap<>(
            "infinity_extractor",
            2, 4, 0, 0, new SimpleRecipeBuilder(), false)
                    .setSlotOverlay(false, false, false, GuiTextures.FURNACE_OVERLAY_1)
                    .setSlotOverlay(false, false, true, GuiTextures.INT_CIRCUIT_OVERLAY)
                    .setSlotOverlay(true, false, GuiTextures.DUST_OVERLAY)
                    .setSound(GTSoundEvents.FIRE);

    public static final RecipeMap<SimpleRecipeBuilder> PNEUMATIC_INFUSER_RECIPES = new RecipeMap<>("pneumatic_infuser",
            1, 1, 3, 1, new SimpleRecipeBuilder(), false)
                    .setSound(GTSoundEvents.BOILER);

    public static final RecipeMap<BlastRecipeBuilder> FORGING_FURNACE_RECIPES = new RecipeMap<>("forging_furnace",
            3, 3, 2, 1, new BlastRecipeBuilder(), false)
                    .setSlotOverlay(false, false, false, GuiTextures.FURNACE_OVERLAY_1)
                    .setSlotOverlay(false, false, true, GuiTextures.INT_CIRCUIT_OVERLAY)
                    .setProgressBar(GuiTextures.PROGRESS_BAR_EXTRUDER, ProgressWidget.MoveType.HORIZONTAL);

    public static final RecipeMap<FusionRecipeBuilder> STAR_SIPHON_RECIPES = new RecipeMap<>("star_siphon",
            1, 1, 1, 1, new FusionRecipeBuilder(), false)
                    .setSlotOverlay(false, false, GuiTextures.EXTRACTOR_OVERLAY)
                    .setSlotOverlay(true, true, GuiTextures.LIGHTNING_OVERLAY_2)
                    .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressWidget.MoveType.HORIZONTAL)
                    .setSound(GTSoundEvents.ARC);

    public static final RecipeMap<FusionRecipeBuilder> ANTIMATTER_COMPRESSOR_RECIPES = new RecipeMap<>(
            "antimatter_compressor",
            3, 3, 2, 0, new FusionRecipeBuilder(), false)
                    .setSlotOverlay(false, false, false, GuiTextures.ATOMIC_OVERLAY_1)
                    .setSlotOverlay(false, false, true, GuiTextures.COMPRESSOR_OVERLAY)
                    .setSlotOverlay(false, true, GuiTextures.ATOMIC_OVERLAY_2)
                    .setSlotOverlay(true, false, GuiTextures.ATOMIC_OVERLAY_1)
                    .setProgressBar(GuiTextures.PROGRESS_BAR_COMPRESS, ProgressWidget.MoveType.HORIZONTAL)
                    .setSound(GTSoundEvents.COMPRESSOR);

    public static final List<RecipeMap<FusionRecipeBuilder>> FUSION_STACK_RECIPE_MAPS = new ArrayList<>(3) {};

    /**
     * For internal usage only! Use HeatExchangerRecipeHandler instead!
     */
    public static final RecipeMap<ExchangerRecipeBuilder> EXCHANGER_PLACEHOLDER_MAP = new RecipeMap<>("heat_exchanger",
            1, 0, 1, 1, new ExchangerRecipeBuilder(), false)
                    .setProgressBar(GuiTextures.PROGRESS_BAR_CRACKING, ProgressWidget.MoveType.HORIZONTAL);

    public static void init() {
        FUSION_STACK_RECIPE_MAPS.add(fusionStackRecipeMap("stack"));
        FUSION_STACK_RECIPE_MAPS.add(fusionStackRecipeMap("array"));
        FUSION_STACK_RECIPE_MAPS.add(fusionStackRecipeMap("complex"));

        // for some unknown reason, running .setSound during map declaration doesn't actually work at all for my custom
        // sound.
        FORGING_FURNACE_RECIPES.setSound(GTCEFuContentSoundEvents.FORGING_FURNACE);
    }

    private static RecipeMap<FusionRecipeBuilder> fusionStackRecipeMap(String id) {
        return new RecipeMap<>("fusion_stack." + id,
                0, 0, 2, 1, new FusionRecipeBuilder(), false)
                        .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressWidget.MoveType.HORIZONTAL)
                        .setSound(GTSoundEvents.ARC);
    }

    private GTCEFuCRecipeMaps() {}
}
