package com.m_w_k.gtcefucontent.mixins.gregtech;

import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;

import gregtech.api.util.FluidTooltipUtil;

@Mixin(FluidTooltipUtil.class)
public class FluidTooltipUtilMixin {

    @Inject(method = "getFluidTooltip(Lnet/minecraftforge/fluids/FluidStack;)Ljava/util/List;",
            at = @At(value = "RETURN"),
            remap = false)
    private static void getFluidTooltipExtended(FluidStack fluid, CallbackInfoReturnable<List<String>> cir) {
        if (cir.getReturnValue() == null) return;
        GTCEFuCUtil.fluidStackTooltipOverride(fluid, cir.getReturnValue());
    }
}
