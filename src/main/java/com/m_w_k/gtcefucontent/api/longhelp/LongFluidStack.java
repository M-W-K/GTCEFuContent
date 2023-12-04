package com.m_w_k.gtcefucontent.api.longhelp;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;

@SuppressWarnings("unused")
public class LongFluidStack extends FluidStack {

    public long longAmount;

    public LongFluidStack(Fluid fluid, long amount) {
        super(fluid, GTCEFuCUtil.truncateLong(amount));
        this.longAmount = amount;
    }

    public LongFluidStack(Fluid fluid, long amount, NBTTagCompound nbt) {
        super(fluid, GTCEFuCUtil.truncateLong(amount), nbt);
        this.longAmount = amount;
    }

    public LongFluidStack(FluidStack stack, long amount) {
        super(stack, GTCEFuCUtil.truncateLong(amount));
        this.longAmount = amount;
    }

    @Nullable
    public static LongFluidStack loadLongFluidStackFromNBT(NBTTagCompound nbt) {
        if (nbt == null) {
            return null;
        }
        if (!nbt.hasKey("FluidName", Constants.NBT.TAG_STRING)) {
            return null;
        }

        String fluidName = nbt.getString("FluidName");
        if (FluidRegistry.getFluid(fluidName) == null) {
            return null;
        }
        LongFluidStack stack = new LongFluidStack(FluidRegistry.getFluid(fluidName), nbt.getLong("LongAmount"));

        if (nbt.hasKey("Tag")) {
            stack.tag = nbt.getCompoundTag("Tag");
        }
        return stack;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setLong("LongAmount", longAmount);
        return nbt;
    }

    @Override
    public LongFluidStack copy() {
        return new LongFluidStack(getFluid(), longAmount, tag);
    }

    public boolean isFluidStackIdentical(LongFluidStack other) {
        return isFluidEqual(other) && longAmount == other.longAmount;
    }

    public void modAmount(long mod) {
        modAmount(mod, false);
    }

    public void modAmount(long mod, boolean multiply) {
        if (!multiply) {
            this.longAmount += mod;
        } else {
            this.longAmount *= mod;
        }
        this.amount = GTCEFuCUtil.truncateLong(this.longAmount);
    }

    public void setAmount(long amount) {
        this.longAmount = amount;
        this.amount = GTCEFuCUtil.truncateLong(this.longAmount);
    }
}
