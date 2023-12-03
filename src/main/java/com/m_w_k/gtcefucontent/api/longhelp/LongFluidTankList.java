package com.m_w_k.gtcefucontent.api.longhelp;

import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.FluidTankList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LongFluidTankList extends FluidTankList implements ILongMultipleTankHandler {
    // it would be great if there were fewer private properties and more protected ones smh
    private final MultiFluidTankEntry[] fluidTanks;

    public LongFluidTankList(boolean allowSameFluidFill, IFluidTank... fluidTanks) {
        super(allowSameFluidFill, fluidTanks);
        ArrayList<MultiFluidTankEntry> list = new ArrayList<>();
        for (IFluidTank tank : fluidTanks) list.add(wrapIntoEntry(tank));
        this.fluidTanks = list.toArray(new MultiFluidTankEntry[0]);
    }

    public LongFluidTankList(boolean allowSameFluidFill, @NotNull List<? extends IFluidTank> fluidTanks) {
        super(allowSameFluidFill, fluidTanks);
        ArrayList<MultiFluidTankEntry> list = new ArrayList<>();
        for (IFluidTank tank : fluidTanks) list.add(wrapIntoEntry(tank));
        this.fluidTanks = list.toArray(new MultiFluidTankEntry[0]);
    }

    public LongFluidTankList(boolean allowSameFluidFill, @NotNull IMultipleTankHandler parent, IFluidTank... additionalTanks) {
        super(allowSameFluidFill, parent, additionalTanks);
        ArrayList<MultiFluidTankEntry> list = new ArrayList<>(parent.getFluidTanks());
        for (IFluidTank tank : additionalTanks) list.add(wrapIntoEntry(tank));
        this.fluidTanks = list.toArray(new MultiFluidTankEntry[0]);
    }

    private MultiFluidTankEntry wrapIntoEntry(IFluidTank tank) {
        return tank instanceof MultiFluidTankEntry entry ? entry : new MultiFluidTankEntry(this, tank);
    }

    public long fill(LongFluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) {
            return 0;
        }
        long totalInserted = 0;
        boolean inputFluidCopied = false;
        // flag value indicating whether the fluid was stored in 'distinct' slot at least once
        boolean distinctSlotVisited = false;

        MultiFluidTankEntry[] fluidTanks = this.fluidTanks.clone();
        Arrays.sort(fluidTanks, IMultipleTankHandler.ENTRY_COMPARATOR);

        // search for tanks with same fluid type first
        for (MultiFluidTankEntry tank : fluidTanks) {
            // if the fluid to insert matches the tank, insert the fluid
            if (resource.isFluidEqual(tank.getFluid())) {
                int inserted = tank.fill(resource, doFill);
                if (inserted > 0) {
                    totalInserted += inserted;
                    if (resource.longAmount - inserted <= 0) {
                        return totalInserted;
                    }
                    if (!inputFluidCopied) {
                        inputFluidCopied = true;
                        resource = resource.copy();
                    }
                    resource.modAmount(-inserted);
                }
                // regardless of whether the insertion succeeded, presence of identical fluid in
                // a slot prevents distinct fill to other slots
                if (!tank.allowSameFluidFill()) {
                    distinctSlotVisited = true;
                }
            }
        }
        // if we still have fluid to insert, loop through empty tanks until we find one that can accept the fluid
        for (MultiFluidTankEntry tank : fluidTanks) {
            // if the tank uses distinct fluid fill (allowSameFluidFill disabled) and another distinct tank had
            // received the fluid, skip this tank
            boolean usesDistinctFluidFill = tank.allowSameFluidFill();
            if ((usesDistinctFluidFill || !distinctSlotVisited) && tank.getFluidAmount() == 0) {
                int inserted = tank.fill(resource, doFill);
                if (inserted > 0) {
                    totalInserted += inserted;
                    if (resource.longAmount - inserted <= 0) {
                        return totalInserted;
                    }
                    if (!inputFluidCopied) {
                        inputFluidCopied = true;
                        resource = resource.copy();
                    }
                    resource.modAmount(-inserted);
                    if (!usesDistinctFluidFill) {
                        distinctSlotVisited = true;
                    }
                }
            }
        }
        // return the amount of fluid that was inserted
        return totalInserted;
    }

    @Nullable
    public LongFluidStack drain(LongFluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0) {
            return null;
        }
        long amountLeft = resource.longAmount;
        LongFluidStack totalDrained = null;
        for (IFluidTank handler : fluidTanks) {
            if (!resource.isFluidEqual(handler.getFluid())) {
                continue;
            }
            FluidStack drain = handler.drain(GTCEFuCUtil.truncateLong(amountLeft), doDrain);
            if (drain != null) {
                if (totalDrained == null) {
                    totalDrained = new LongFluidStack(drain.getFluid(), drain.amount);
                } else {
                    totalDrained.modAmount(drain.amount);
                }
                amountLeft -= drain.amount;
                if (amountLeft <= 0) {
                    return totalDrained;
                }
            }
        }
        return totalDrained;
    }

    @Nullable
    public LongFluidStack drain(long maxDrain, boolean doDrain) {
        if (maxDrain <= 0) {
            return null;
        }
        LongFluidStack totalDrained = null;
        for (IFluidTank handler : fluidTanks) {
            FluidStack drain =  handler.drain(GTCEFuCUtil.truncateLong(maxDrain), doDrain);
            if (drain != null) {
                if (totalDrained == null) {
                    totalDrained = new LongFluidStack(drain.getFluid(), drain.amount);
                    maxDrain -= totalDrained.amount;
                } else {
                    if (!totalDrained.isFluidEqual(handler.getFluid())) {
                        continue;
                    }
                    totalDrained.modAmount(drain.amount);
                    maxDrain -= drain.amount;
                }
                if (maxDrain <= 0) {
                    return totalDrained;
                }
            }
        }
        return totalDrained;
    }
}
