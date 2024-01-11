package com.m_w_k.gtcefucontent.api.util;

import java.util.Arrays;
import java.util.OptionalDouble;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import com.m_w_k.gtcefucontent.GTCEFuContent;

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

    public static int truncateLong(long number) {
        return (int) Math.min(Integer.MAX_VALUE, number);
    }
}
