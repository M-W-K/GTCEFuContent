package com.m_w_k.gtcefucontent.mixins.forge;

import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mixin(UniversalBucket.class)
public class UniversalBucketMixin extends Item {

    // TODO fix not working and remove asm
    @Inject(method = "getSubItems", at = @At(value = "HEAD"))
    private void getSubItemsExtended(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems, CallbackInfo info) {
        UniversalBucket item = (UniversalBucket) (Item) this;
        if (!GTCEFuContent$isInCreativeTab(item, tab))
            return;
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
            if (fluid instanceof EutecticFluid eutectic) {
                FluidStack base = new FluidStack(fluid, ((UniversalBucket) item).getCapacity());
                GTCEFuContent$register(item, eutectic.getWithTemperature(base, Integer.MAX_VALUE), subItems);
                GTCEFuContent$register(item, eutectic.getWithTemperature(base, 0), subItems);
            }
        }
    }

    @Unique
    private static boolean GTCEFuContent$isInCreativeTab(Item item, CreativeTabs targetTab) {
        for (CreativeTabs tab : item.getCreativeTabs())
            if (tab == targetTab)
                return true;
        CreativeTabs creativetabs = item.getCreativeTab();
        return creativetabs != null && (targetTab == CreativeTabs.SEARCH || targetTab == creativetabs);
    }

    @Unique
    private static void GTCEFuContent$register(Item item, FluidStack fs, NonNullList<ItemStack> subItems) {
        ItemStack stack = new ItemStack(item);
        IFluidHandlerItem fluidHandler = new FluidBucketWrapper(stack);
        if (fluidHandler.fill(fs, true) == fs.amount) {
            ItemStack filled = fluidHandler.getContainer();
            subItems.add(filled);
        }
    }
}
