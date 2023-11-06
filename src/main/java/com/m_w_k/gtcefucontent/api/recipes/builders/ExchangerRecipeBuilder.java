package com.m_w_k.gtcefucontent.api.recipes.builders;

import com.m_w_k.gtcefucontent.api.recipes.recipeproperties.HeatToConvertProperty;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.EnumValidationResult;
import gregtech.api.util.GTLog;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;

public class ExchangerRecipeBuilder extends RecipeBuilder<ExchangerRecipeBuilder> {

    public ExchangerRecipeBuilder() {}

    public ExchangerRecipeBuilder(Recipe recipe, RecipeMap<ExchangerRecipeBuilder> recipeMap) {
        super(recipe, recipeMap);
    }

    public ExchangerRecipeBuilder(RecipeBuilder<ExchangerRecipeBuilder> recipeBuilder) {
        super(recipeBuilder);
    }

    @Override
    public ExchangerRecipeBuilder copy() {
        return new ExchangerRecipeBuilder(this);
    }

    @Override
    public boolean applyProperty(@Nonnull String key, Object value) {
        if (key.equals(HeatToConvertProperty.KEY)) {
            this.heatToConvert(((Number) value).longValue());
            return true;
        }
        return super.applyProperty(key, value);
    }

    @SuppressWarnings("UnusedReturnValue")
    public ExchangerRecipeBuilder heatToConvert(long HeatToConvert) {
        if (HeatToConvert == 0) {
            GTLog.logger.error("Heat to convert cannot be equal to 0", new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }
        this.applyProperty(HeatToConvertProperty.getInstance(), HeatToConvert);
        return this;
    }

    public long getHeatToConvert() {
        return this.recipePropertyStorage == null ? 0L :
                this.recipePropertyStorage.getRecipePropertyValue(HeatToConvertProperty.getInstance(), 0L);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append(HeatToConvertProperty.getInstance().getKey(), getHeatToConvert())
                .toString();
    }
}
