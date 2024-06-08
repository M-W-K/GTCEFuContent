package com.m_w_k.gtcefucontent;

import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;
import com.m_w_k.gtcefucontent.asm.hooks.UniversalBucketHooks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Decoy extends Item {

    @Override
    public void getSubItems(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems)
    {
        if (!this.isInCreativeTab(tab))
            return;
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
        {
            if (fluid != FluidRegistry.WATER && fluid != FluidRegistry.LAVA && !fluid.getName().equals("milk"))
            {
                // add all fluids that the bucket can be filled  with
                FluidStack fs = new FluidStack(fluid, getCapacity());
                ItemStack stack = new ItemStack(this);
                IFluidHandlerItem fluidHandler = new FluidBucketWrapper(stack);
                if (fluidHandler.fill(fs, true) == fs.amount)
                {
                    ItemStack filled = fluidHandler.getContainer();
                    subItems.add(filled);
                }
            }
        }
    }

    public void getSubItemsTarget(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems)
    {
        UniversalBucketHooks.generateEutecticBuckets(this, tab, subItems);
        if (!this.isInCreativeTab(tab))
            return;
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
        {
            if (fluid != FluidRegistry.WATER && fluid != FluidRegistry.LAVA && !fluid.getName().equals("milk"))
            {
                // add all fluids that the bucket can be filled  with
                FluidStack fs = new FluidStack(fluid, getCapacity());
                ItemStack stack = new ItemStack(this);
                IFluidHandlerItem fluidHandler = new FluidBucketWrapper(stack);
                if (fluidHandler.fill(fs, true) == fs.amount)
                {
                    ItemStack filled = fluidHandler.getContainer();
                    subItems.add(filled);
                }
            }
        }
    }

    private int getCapacity() {
        return 0;
    }
}
