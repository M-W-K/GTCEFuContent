package com.m_w_k.gtcefucontent.integration;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;
import gregtech.api.GTValues;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SteamMetaTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.category.GTRecipeCategory;
import gregtech.api.recipes.machines.RecipeMapFurnace;
import gregtech.integration.jei.recipe.RecipeMapCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

    public static IGuiHelper guiHelper;

    @Override
    public void register(IModRegistry registry) {
        guiHelper = registry.getJeiHelpers().getGuiHelper();
        registerRecipeMapCatalyst(registry, RecipeMaps.COMBUSTION_GENERATOR_FUELS, GTCEFuCMetaTileEntities.SYMPATHETIC_COMBUSTOR);
        registerRecipeMapCatalyst(registry, RecipeMaps.GAS_TURBINE_FUELS, GTCEFuCMetaTileEntities.SYMPATHETIC_COMBUSTOR);
        registerRecipeMapCatalyst(registry, RecipeMaps.SEMI_FLUID_GENERATOR_FUELS, GTCEFuCMetaTileEntities.SYMPATHETIC_COMBUSTOR);
        registry.addRecipeCatalyst(GTCEFuCMetaTileEntities.SYMPATHETIC_COMBUSTOR.getStackForm(), VanillaRecipeCategoryUid.FUEL);

        for (var mte : GTCEFuCMetaTileEntities.HEAT_EXCHANGER) {
            registerRecipeMapCatalyst(registry, GTCEFuCRecipeMaps.EXCHANGER_PLACEHOLDER_MAP, mte);
        }
    }

    /**
     * Taken directly from {@link gregtech.integration.jei.JustEnoughItemsModule}
     */
    private void registerRecipeMapCatalyst(IModRegistry registry, RecipeMap<?> recipeMap,
                                           MetaTileEntity metaTileEntity) {
        for (GTRecipeCategory category : recipeMap.getRecipesByCategory().keySet()) {
            RecipeMapCategory jeiCategory = RecipeMapCategory.getCategoryFor(category);
            if (jeiCategory != null) {
                registry.addRecipeCatalyst(metaTileEntity.getStackForm(), jeiCategory.getUid());
            }
        }

        if (recipeMap instanceof RecipeMapFurnace) {
            registry.addRecipeCatalyst(metaTileEntity.getStackForm(), VanillaRecipeCategoryUid.SMELTING);
            return;
        }
        if (recipeMap.getSmallRecipeMap() != null) {
            registry.addRecipeCatalyst(metaTileEntity.getStackForm(),
                    GTValues.MODID + ":" + recipeMap.getSmallRecipeMap().unlocalizedName);
            return;
        }

        for (GTRecipeCategory category : recipeMap.getRecipesByCategory().keySet()) {
            RecipeMapCategory jeiCategory = RecipeMapCategory.getCategoryFor(category);
            // don't allow a Steam Machine to be a JEI tab icon
            if (jeiCategory != null && !(metaTileEntity instanceof SteamMetaTileEntity)) {
                Object icon = category.getJEIIcon();
                if (icon instanceof TextureArea textureArea) {
                    icon = guiHelper.drawableBuilder(textureArea.imageLocation, 0, 0, 18, 18)
                            .setTextureSize(18, 18)
                            .build();
                } else if (icon == null) {
                    icon = metaTileEntity.getStackForm();
                }
                jeiCategory.setIcon(icon);
            }
        }
    }
}
