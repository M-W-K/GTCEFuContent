package com.m_w_k.gtcefucontent.asmm.hooks;

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

import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;

@SuppressWarnings("unused")
public class UniversalBucketHooks {

    public static void generateEutecticBuckets(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (!isInCreativeTab(item, tab))
            return;
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
            if (fluid instanceof EutecticFluid eutectic) {
                FluidStack base = new FluidStack(fluid, ((UniversalBucket) item).getCapacity());
                register(item, eutectic.getWithTemperature(base, Integer.MAX_VALUE), subItems);
                register(item, eutectic.getWithTemperature(base, 0), subItems);
            }
        }
    }

    private static boolean isInCreativeTab(Item item, CreativeTabs targetTab) {
        for (CreativeTabs tab : item.getCreativeTabs())
            if (tab == targetTab)
                return true;
        CreativeTabs creativetabs = item.getCreativeTab();
        return creativetabs != null && (targetTab == CreativeTabs.SEARCH || targetTab == creativetabs);
    }

    private static void register(Item item, FluidStack fs, NonNullList<ItemStack> subItems) {
        ItemStack stack = new ItemStack(item);
        IFluidHandlerItem fluidHandler = new FluidBucketWrapper(stack);
        if (fluidHandler.fill(fs, true) == fs.amount) {
            ItemStack filled = fluidHandler.getContainer();
            subItems.add(filled);
        }
    }
}
