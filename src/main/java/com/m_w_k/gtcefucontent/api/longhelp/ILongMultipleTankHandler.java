package com.m_w_k.gtcefucontent.api.longhelp;

import org.jetbrains.annotations.Nullable;

import gregtech.api.capability.IMultipleTankHandler;

@SuppressWarnings("unused")
public interface ILongMultipleTankHandler extends IMultipleTankHandler {

    long fill(LongFluidStack resource, boolean doFill);

    @Nullable
    LongFluidStack drain(LongFluidStack resource, boolean doDrain);

    @Nullable
    LongFluidStack drain(long maxDrain, boolean doDrain);
}
