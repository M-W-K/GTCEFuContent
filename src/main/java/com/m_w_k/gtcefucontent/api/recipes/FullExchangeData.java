package com.m_w_k.gtcefucontent.api.recipes;

import net.minecraftforge.fluids.FluidStack;

import java.math.BigInteger;

public class FullExchangeData {

    public FluidStack initialA;
    public FluidStack finalA;
    public long thermalEnergyA;
    public FluidStack initialB;
    public FluidStack finalB;
    public long thermalEnergyB;

    protected FullExchangeData() {}

    public FullExchangeData(HalfExchangeData A, HalfExchangeData B) {
        this.thermalEnergyA = A.thermalEnergy;
        this.initialA = A.in.copy();
        this.finalA = A.out.copy();
        this.thermalEnergyB = B.thermalEnergy;
        this.initialB = B.in.copy();
        this.finalB = B.out.copy();
    }

    public FullExchangeData equalize() {
        long gcd = BigInteger.valueOf(thermalEnergyA).gcd(BigInteger.valueOf(thermalEnergyB)).longValue();
        int factorA = (int) (this.thermalEnergyB / gcd);
        int factorB = (int) (this.thermalEnergyA / gcd);
        // say A's conversion took 180 energy units and B's conversion gave 120.
        // Now we have 2 for factorA, 3 for factorB, and a gcd of 60.
        this.thermalEnergyA *= factorA;
        this.initialA.amount *= factorA;
        this.finalA.amount *= factorA;
        this.thermalEnergyB *= factorB;
        this.initialB.amount *= factorB;
        this.finalB.amount *= factorB;
        return this;
    }
}
