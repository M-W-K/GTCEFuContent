package com.m_w_k.gtcefucontent.mixins.forge;

import java.util.Iterator;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;

@Mixin(UniversalBucket.class)
public class UniversalBucketMixin extends Item {

    @Inject(method = "getSubItems",
            at = @At(value = "JUMP", ordinal = 5, shift = At.Shift.BY, by = -5),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void GTCEFuContent$eutecticSubItemOverride(CreativeTabs tab, NonNullList<ItemStack> subItems,
                                                       CallbackInfo ci, Iterator<?> var3, Fluid fluid, FluidStack fs,
                                                       ItemStack stack, IFluidHandlerItem fluidHandler) {
        if (fluid instanceof EutecticFluid eutectic) {
            GTCEFuContent$registerSubItem(this, eutectic.getWithTemperature(fs, Integer.MAX_VALUE), subItems);
            GTCEFuContent$registerSubItem(this, fs, subItems);
            GTCEFuContent$registerSubItem(this, eutectic.getWithTemperature(fs, 0), subItems);
            // this prevents the normal temperature bucket from generating twice
            stack.setCount(2);
        }
    }

    @Unique
    private static void GTCEFuContent$registerSubItem(Item item, FluidStack fs, NonNullList<ItemStack> subItems) {
        ItemStack stack = new ItemStack(item);
        IFluidHandlerItem fluidHandler = new FluidBucketWrapper(stack);
        if (fluidHandler.fill(fs, true) == fs.amount) {
            ItemStack filled = fluidHandler.getContainer();
            subItems.add(filled);
        }
    }
}
