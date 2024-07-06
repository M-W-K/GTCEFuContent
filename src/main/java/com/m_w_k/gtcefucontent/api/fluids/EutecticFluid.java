package com.m_w_k.gtcefucontent.api.fluids;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.m_w_k.gtcefucontent.api.recipes.HeatExchangerRecipeHandler;
import com.m_w_k.gtcefucontent.api.unification.properties.GTCEFuCPropertyKey;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.FluidState;
import gregtech.api.fluids.GTFluid;
import gregtech.api.fluids.GTFluidRegistration;
import gregtech.api.fluids.store.FluidStorageKey;
import gregtech.api.unification.material.Material;
import gregtech.api.util.FluidTooltipUtil;

public class EutecticFluid extends GTFluid.GTMaterialFluid {

    private final int minTemp;
    private final int maxTemp;

    private final int capacity;

    public EutecticFluid(@NotNull String fluidName, ResourceLocation still, ResourceLocation flowing,
                         @NotNull FluidState state, @Nullable String translationKey, @NotNull Material material,
                         int minTemp, int maxTemp, int thermalCapacity) {
        super(fluidName, still, flowing, state, translationKey, material);
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.capacity = thermalCapacity;
    }

    @Override
    public int getTemperature(FluidStack stack) {
        if (stack.tag == null || !stack.tag.hasKey("Temperature")) return getTemperature();
        else return stack.tag.getInteger("Temperature");
    }

    public int getThermalCapacity() {
        return capacity;
    }

    public int getMaxTemperature() {
        return maxTemp;
    }

    public int getMinTemperature() {
        return minTemp;
    }

    public FluidStack stackWithTemperature(int amount, int temp) {
        if (temp == this.getTemperature()) return new FluidStack(this, amount);
        temp = boundTemp(temp);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Temperature", temp);
        return new FluidStack(this, amount, tag);
    }

    public FluidStack getWithTemperature(FluidStack stack, int temp) {
        if (temp == this.getTemperature()) return new FluidStack(this, stack.amount);
        temp = boundTemp(temp);
        stack = stack.copy();
        if (stack.tag == null) stack.tag = new NBTTagCompound();
        stack.tag.setInteger("Temperature", temp);
        return stack;
    }

    private int boundTemp(int temp) {
        return MathHelper.clamp(temp, this.getMinTemperature(), this.getMaxTemperature());
    }

    public static class Builder extends FluidBuilder {

        private String name;
        private String translationKey;
        private FluidState state = null;
        private ResourceLocation still = null;
        private ResourceLocation flowing = null;
        private boolean hasCustomStill = false;
        private boolean hasCustomFlowing = false;
        private boolean hasBucket = true;

        private int minTemp = 1;
        private int maxTemp = 1000;

        @Override
        public @NotNull FluidBuilder disableBucket() {
            this.hasBucket = false;
            return super.disableBucket();
        }

        private void determineName(@Nullable Material material, @Nullable FluidStorageKey key) {
            if (name != null) return;
            if (material == null || key == null) throw new IllegalArgumentException("Fluid must have a name");
            name = key.getRegistryNameFor(material);
        }

        private void determineTextures(@Nullable Material material, @Nullable FluidStorageKey key,
                                       @NotNull String modid) {
            if (material != null && key != null) {
                if (hasCustomStill) {
                    still = new ResourceLocation(modid, "blocks/fluids/fluid." + name);
                } else {
                    still = key.getIconType().getBlockTexturePath(material.getMaterialIconSet());
                }
            } else {
                still = new ResourceLocation(modid, "blocks/fluids/fluid." + name);
            }

            if (hasCustomFlowing) {
                flowing = new ResourceLocation(modid, "blocks/fluids/fluid." + name + "_flow");
            } else {
                flowing = still;
            }
        }

        public Builder minTemp(int minTemp) {
            Preconditions.checkArgument(minTemp > 0, "temperature must be > 0");
            this.minTemp = minTemp;
            return this;
        }

        public Builder defaultTemp(int defaultTemp) {
            temperature(defaultTemp);
            return this;
        }

        public Builder maxTemp(int maxTemp) {
            Preconditions.checkArgument(maxTemp > 0, "temperature must be > 0");
            this.maxTemp = maxTemp;
            return this;
        }

        @Override
        public @NotNull Builder customStill() {
            this.hasCustomStill = true;
            super.customStill();
            return this;
        }

        @Override
        public @NotNull Builder customFlow() {
            this.hasCustomFlowing = true;
            super.customFlow();
            return this;
        }

        @Override
        public @NotNull Fluid build(@NotNull String modid, @Nullable Material material, @Nullable FluidStorageKey key) {
            determineName(material, key);
            determineTextures(material, key, modid);

            if (state == null) {
                if (key != null && key.getDefaultFluidState() != null) {
                    state = key.getDefaultFluidState();
                } else {
                    state = FluidState.LIQUID; // default fallback
                }
            }

            // try to find an already registered fluid that we can use instead of a new one
            Fluid fluid = FluidRegistry.getFluid(name);

            boolean needsRegistration = false;
            if (fluid == null) {
                needsRegistration = true;
                if (material == null) {
                    fluid = new GTFluid(name, still, flowing, state);
                } else if (key != null) {
                    if (translationKey == null) {
                        translationKey = key.getTranslationKeyFor(material);
                    }
                    fluid = new EutecticFluid(name, still, flowing, state, translationKey, material, minTemp, maxTemp,
                            material.getProperty(GTCEFuCPropertyKey.EUTECTIC).getThermalCapacity1mb());
                    HeatExchangerRecipeHandler.registerEutectic((EutecticFluid) fluid);
                } else {
                    throw new IllegalArgumentException("Fluids with materials must have a FluidStorageKey");
                }
            }
            if (needsRegistration) {
                GTFluidRegistration.INSTANCE.registerFluid(fluid, modid, hasBucket);
            }
            if (fluid instanceof EutecticFluid eutectic) {
                FluidTooltipUtil.registerTooltip(fluid, () -> {
                    List<String> tooltip = new ArrayList<>();
                    tooltip.add(I18n.format("gtcefucontent.fluid.temperature.max", eutectic.getMaxTemperature()));
                    tooltip.add(I18n.format("gtcefucontent.fluid.temperature.min", eutectic.getMinTemperature()));
                    return tooltip;
                });
            }

            return super.build(modid, material, key);
        }
    }
}
