package com.m_w_k.gtcefucontent.api.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.util.RelativeDirection;

public final class GTCEFuCUtil {

    public static ResourceLocation gtcefucId(String name) {
        return new ResourceLocation(GTCEFuContent.MODID, name);
    }

    /**
     * Returns a BlockPos offset from the controller by the given values
     *
     * @param controller the controller whose position we should reference
     * @param x          Positive is controller left, Negative is controller right
     * @param y          Positive is controller up, Negative is controller down
     * @param z          Positive is controller front, Negative is controller back
     * @return Offset BlockPos
     */
    public static BlockPos bbHelper(MultiblockControllerBase controller, int x, int y, int z) {
        EnumFacing frontFacing = controller.getFrontFacing();
        EnumFacing upwardsFacing = controller.getUpwardsFacing();
        boolean isFlipped = controller.isFlipped();

        return controller.getPos()
                .offset(RelativeDirection.FRONT.getRelativeFacing(frontFacing, upwardsFacing, isFlipped), z)
                .offset(RelativeDirection.LEFT.getRelativeFacing(frontFacing, upwardsFacing, isFlipped), x)
                .offset(RelativeDirection.UP.getRelativeFacing(frontFacing, upwardsFacing, isFlipped), y);
    }

    public static int filteredPos(BlockPos pos, EnumFacing facing) {
        return filteredPos(pos, facing.getAxis()) * facing.getAxisDirection().getOffset();
    }

    public static int filteredPos(BlockPos pos, EnumFacing.Axis axis) {
        return switch (axis) {
            case Y -> pos.getY();
            case X -> pos.getX();
            default -> pos.getZ();
        };
    }

    public static double geometricMean(double... numbers) {
        OptionalDouble total = Arrays.stream(numbers).reduce((a, b) -> a * b);
        if (total.isPresent()) {
            return Math.pow(total.getAsDouble(), 1D / numbers.length);
        } else return 0;
    }

    public static double pythagoreanAverage(double... numbers) {
        double sum = 0;
        for (double d : numbers) {
            sum += Math.pow(d, 2);
        }
        return Math.pow(sum, 0.5);
    }

    public static ItemStack setStackCount(ItemStack stack, int count) {
        return new ItemStack(stack.getItem(), count, stack.getMetadata());
    }

    public static FluidBuilder fluidAtTemp(int temp) {
        return new FluidBuilder().temperature(temp);
    }

    public static int getTemp(FluidStack stack) {
        return stack.getFluid().getTemperature(stack);
    }

    public static int truncateLong(long number) {
        return (int) Math.min(Integer.MAX_VALUE, number);
    }

    public static void fluidStackTooltipOverride(FluidStack fluid, List<String> tooltip) {
        if (Objects.equals(fluid.getFluid().getUnlocalizedName(), "fluid.void_starlight")) {
            for (int i = 0; i < tooltip.size(); i++) {
                String string = tooltip.get(i);
                if (Objects.equals(string,
                        I18n.format("gregtech.fluid.temperature", fluid.getFluid().getTemperature()))) {
                    tooltip.set(i, I18n.format("gtcefucontent.material.void_starlight.temperature"));
                    tooltip.add(i, I18n.format("gtcefucontent.material.void_starlight.info"));
                }
            }
        }
        if (fluid.getFluid() instanceof EutecticFluid eutecticFluid) {
            for (int i = 0; i < tooltip.size(); i++) {
                if (Objects.equals(tooltip.get(i),
                        I18n.format("gregtech.fluid.temperature", eutecticFluid.getTemperature()))) {
                    tooltip.set(i, I18n.format("gregtech.fluid.temperature", eutecticFluid.getTemperature(fluid)));
                }
            }
        }
    }
}
