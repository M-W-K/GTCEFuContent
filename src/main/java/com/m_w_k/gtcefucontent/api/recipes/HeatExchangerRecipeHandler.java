package com.m_w_k.gtcefucontent.api.recipes;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCHeatExchangerLoader;

public final class HeatExchangerRecipeHandler {

    /**
     * Approximate conversion of heat units to EU, assuming that the fluid heater is perfectly efficient.
     */
    public static final long HEU = GTCEFuCHeatExchangerLoader.waterVaporizationEnergy / 150;

    private static final Map<Fluid, Tuple<FluidStack, long[]>> HEATING_MAP = new HashMap<>();
    private static final Map<Fluid, Tuple<FluidStack, long[]>> COOLING_MAP = new HashMap<>();
    private static final Set<String> EUTECTICS = new HashSet<>();

    /**
     * Registers a two-way heat exchange. Use this overload for simple fluid heating and cooling.
     * 
     * @param fluidA          The first fluid
     * @param fluidB          The second fluid
     * @param thermalCapacity The energy required to increase the temp of fluid A by 1 degree,
     *                        in order to reach fluid B, in units of J.
     *                        This means that 418600 units would increase the temperature of 1L water by 1°K,
     *                        as the thermal capacity of water is 418.6 kJ/kg, or kJ/L.
     *                        <br>
     *                        <br>
     *                        Note that if this is negative,
     *                        it would require thermal energy to cool fluid A into fluid B, and vice versa.
     */
    public static void registerHeatExchange(FluidStack fluidA, FluidStack fluidB, int thermalCapacity) {
        registerHeatExchange(fluidA, fluidB, thermalCapacity, true);
    }

    /**
     * Registers a heat exchange. Use this overload for simple fluid heating and cooling.
     * 
     * @param fluidA          The first fluid
     * @param fluidB          The second fluid
     * @param thermalCapacity The energy required to increase the temp of fluid A by 1 degree,
     *                        in order to reach fluid B, in units of J.
     *                        This means that 418600 units would increase the temperature of 1L water by 1°K,
     *                        as the thermal capacity of water is 418.6 kJ/kg, or kJ/L.
     *                        <br>
     *                        <br>
     *                        Note that if this is negative,
     *                        it would require thermal energy to cool fluid A into fluid B, and vice versa.
     * @param isTwoWay        Whether the heat exchange should be reversible.
     */
    public static void registerHeatExchange(FluidStack fluidA, FluidStack fluidB, int thermalCapacity,
                                            boolean isTwoWay) {
        int tempDifference = fluidB.getFluid().getTemperature() - fluidA.getFluid().getTemperature();
        // If tempDifference is positive, then we are heating fluid, and vice versa.
        registerHeatExchange(fluidA, fluidB, (long) tempDifference * thermalCapacity, isTwoWay);
    }

    /**
     * Registers a two-way heat exchange. Use this overload for more complex conversions.
     * 
     * @param fluidA        The first fluid
     * @param fluidB        The second fluid
     * @param thermalEnergy The total energy required to convert fluid A to fluid B, in units of J.
     *                      This means that 418600 units would increase the temperature of 1L water by 1°K,
     *                      as the thermal capacity of water is 418.6 kJ/kg, or kJ/L.
     *                      A negative thermal energy means that the conversion releases heat.
     */
    public static void registerHeatExchange(FluidStack fluidA, FluidStack fluidB, long thermalEnergy) {
        registerHeatExchange(fluidA, fluidB, thermalEnergy, true);
    }

    /**
     * Registers a heat exchange. Use this overload for more complex conversions.
     * 
     * @param fluidA        The first fluid
     * @param fluidB        The second fluid
     * @param thermalEnergy The total energy required to convert fluid A to fluid B, in units of J.
     *                      This means that 418600 units would increase the temperature of 1L water by 1°K,
     *                      as the thermal capacity of water is 418.6 kJ/kg, or kJ/L.
     *                      A negative thermal energy means that the conversion releases heat.
     * @param isTwoWay      Whether the heat exchange should be reversible.
     */
    public static void registerHeatExchange(FluidStack fluidA, FluidStack fluidB, long thermalEnergy,
                                            boolean isTwoWay) {
        // No zero-energy conversions, they would cause divide by 0 errors.
        if (thermalEnergy == 0)
            GTCEFuContent.log("Someone tried to register a zero-energy Heat Exchanger recipe. THIS IS INVALID!",
                    GTCEFuContent.LogType.WARN);

        if (thermalEnergy > 0) {
            HEATING_MAP.put(fluidA.getFluid(), new Tuple<>(fluidB, new long[] { fluidA.amount, thermalEnergy }));
            if (isTwoWay)
                COOLING_MAP.put(fluidB.getFluid(), new Tuple<>(fluidA, new long[] { fluidB.amount, thermalEnergy }));
        } else {
            COOLING_MAP.put(fluidA.getFluid(), new Tuple<>(fluidB, new long[] { fluidA.amount, -thermalEnergy }));
            if (isTwoWay)
                HEATING_MAP.put(fluidB.getFluid(), new Tuple<>(fluidA, new long[] { fluidB.amount, -thermalEnergy }));

        }
    }

    /**
     * Removes the heat exchange associated with a given fluid.
     * 
     * @param fluid The fluid to remove the heat exchange for.
     * @param type  Which types of exchange to remove
     */
    @SuppressWarnings("unused")
    public static void removeHeatExchange(Fluid fluid, ExchangeType type) {
        switch (type) {
            case BOTH -> {
                HEATING_MAP.remove(fluid);
                COOLING_MAP.remove(fluid);
            }
            case HEATING -> HEATING_MAP.remove(fluid);
            case COOLING -> COOLING_MAP.remove(fluid);
        }
    }

    public enum ExchangeType {
        HEATING,
        COOLING,
        BOTH
    }

    /**
     * Find out whether we can do a heat exchange with two fluids, and if so what are the mb values for the involved
     * fluids.
     * 
     * @param fluidA The first fluid
     * @param fluidB The second fluid
     * @return A tuple of arrays, where the first array is the required amount of fluidA and fluidB respectively,
     *         and the second array is the output FluidStacks in order. If the conversion cannot be done, returns null.
     */
    @Nullable
    public static Tuple<int[], FluidStack[]> getHeatExchange(Fluid fluidA, Fluid fluidB) {
        int tempDifference = fluidB.getTemperature() - fluidA.getTemperature();
        if (tempDifference == 0) return null;

        Tuple<FluidStack, long[]> A;
        Tuple<FluidStack, long[]> B;

        boolean heatA = tempDifference > 0;

        if (heatA) {
            // Heating A, Cooling B
            A = HEATING_MAP.get(fluidA);
            B = COOLING_MAP.get(fluidB);
        } else {
            // Cooling A, Heating B
            A = COOLING_MAP.get(fluidA);
            B = HEATING_MAP.get(fluidB);
        }

        if (A == null || B == null) return null;

        if (heatA) {
            // We can't heat A to more than B's starting temp, and we can't cool B to less than A's starting temp
            if (A.getFirst().getFluid().getTemperature() > fluidB.getTemperature() ||
                    B.getFirst().getFluid().getTemperature() < fluidA.getTemperature())
                return null;
        } else {
            // We can't cool A to less than B's starting temp, and we can't heat B to more than A's starting temp
            if (A.getFirst().getFluid().getTemperature() < fluidB.getTemperature() ||
                    B.getFirst().getFluid().getTemperature() > fluidA.getTemperature())
                return null;
        }

        long factorA = A.getSecond()[1];
        long factorB = B.getSecond()[1];
        long gcd = BigInteger.valueOf(factorA).gcd(BigInteger.valueOf(factorB)).intValue();
        factorA /= gcd;
        factorB /= gcd;
        // say A's conversion took 180 energy units and B's conversion gave 120.
        // Now we have 3 for A, 2 for B, and a gcd of 60.
        int amountA = (int) (A.getSecond()[0] * factorA);
        int amountB = (int) (B.getSecond()[0] * factorB);
        FluidStack outA = new FluidStack(A.getFirst(), (int) (A.getFirst().amount * factorA));
        FluidStack outB = new FluidStack(B.getFirst(), (int) (B.getFirst().amount * factorB));
        return new Tuple<>(new int[] { amountA, amountB }, new FluidStack[] { outA, outB });
    }

    /**
     * Gets a copy of the cooling map. The key is the fluid the exchange takes in,
     * the FluidStack is the fluid the exchange puts out,
     * and the long[] encodes the amount of input fluid and the thermal energy of the exchange.
     * 
     * @return Copy of the cooling map.
     */
    public static Map<Fluid, Tuple<FluidStack, long[]>> getCoolingMapCopy() {
        // We don't want people to be able to modify the map directly
        return new HashMap<>(COOLING_MAP);
    }

    /**
     * Gets a copy of the heating map. The key is the fluid the exchange takes in,
     * the FluidStack is the fluid the exchange puts out,
     * and the long[] encodes the amount of input fluid and the thermal energy of the exchange.
     * 
     * @return Copy of the heating map.
     */
    public static Map<Fluid, Tuple<FluidStack, long[]>> getHeatingMapCopy() {
        // We don't want people to be able to modify the map directly
        return new HashMap<>(HEATING_MAP);
    }

    /**
     * Register one or more fluids as a variant of a eutectic alloy.
     * 
     * @param fluids The fluids to register.
     */
    @SuppressWarnings("SimplifyStreamApiCallChains")
    public static void addEutectic(Fluid... fluids) {
        EUTECTICS.addAll(Arrays.stream(fluids).map(Fluid::getName).collect(Collectors.toList()));
    }

    /**
     * Check if a given fluid is a eutectic alloy.
     * 
     * @param fluid The fluid to check.
     * @return Whether the fluid is a eutectic alloy.
     */
    public static boolean isEutectic(Fluid fluid) {
        return EUTECTICS.contains(fluid.getName());
    }
}
