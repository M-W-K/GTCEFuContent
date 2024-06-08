package com.m_w_k.gtcefucontent.api.recipes;

import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.getTemp;

public class HalfExchangeData {

    public FluidStack in;
    public FluidStack out;
    public int thermalCapacity;
    public long thermalEnergy;

    protected HalfExchangeData() {}

    public HalfExchangeData(FluidStack in, FluidStack out, int thermalCapacity) {
        this.in = in;
        this.out = out;
        this.thermalCapacity = thermalCapacity;
        recalculateThermalEnergy();
    }

    @Contract("null, _ -> null; !null, _ -> new")
    public static HalfExchangeData withNewIn(HalfExchangeData data, FluidStack newIn) {
        if (data == null) return null;
        HalfExchangeData newData = data.deepCopy();
        newData.in = newIn;
        return newData.recalculateThermalEnergy();
    }

    @Contract("null, _ -> null; !null, _ -> new")
    public static HalfExchangeData withNewOut(HalfExchangeData data, FluidStack newOut) {
        if (data == null) return null;
        HalfExchangeData newData = data.deepCopy();
        newData.out = newOut;
        return newData.recalculateThermalEnergy();
    }

    @Nullable
    public EutecticFluid getEutectic() {
        if (this.in.getFluid() instanceof EutecticFluid fluid && this.out.getFluid() == fluid) return fluid;
        else return null;
    }

    public HalfExchangeData recalculateThermalEnergy() {
        this.thermalEnergy = (long) (getTemp(out) - getTemp(in)) * thermalCapacity;
        return this;
    }

    @Contract("-> new")
    public HalfExchangeData deepCopy() {
        HalfExchangeData copy = new HalfExchangeData();
        copy.in = this.in.copy();
        copy.out = this.out.copy();
        copy.thermalCapacity = this.thermalCapacity;
        copy.thermalEnergy = this.thermalEnergy;
        return copy;
    }
}
