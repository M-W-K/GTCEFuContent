package com.m_w_k.gtcefucontent.api.recipes.recipeproperties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import gregtech.api.recipes.recipeproperties.RecipeProperty;
import gregtech.api.util.TextFormattingUtil;

public class HeatToConvertProperty extends RecipeProperty<Long> {

    public static final String KEY = "heat_to_convert";

    private static HeatToConvertProperty INSTANCE;

    protected HeatToConvertProperty() {
        super(KEY, Long.class);
    }

    public static HeatToConvertProperty getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HeatToConvertProperty();
        }

        return INSTANCE;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
        minecraft.fontRenderer.drawString(I18n.format("gtcefucontent.recipe.heattoconvert",
                TextFormattingUtil.formatLongToCompactString(Math.abs(castValue(value)))) +
                getHeatType(castValue(value)), x, y, color);
    }

    private static String getHeatType(long heat) {
        return heat > 0 ? " (Heating)" : " (Cooling)";
    }

    @Override
    public boolean hideTotalEU() {
        return true;
    }

    @Override
    public boolean hideEUt() {
        return true;
    }

    @Override
    public boolean hideDuration() {
        return true;
    }

    @Override
    public int getInfoHeight(Object value) {
        // jank patch over bug where EUt, duration, and total EU still take up space
        return 0;
    }
}
