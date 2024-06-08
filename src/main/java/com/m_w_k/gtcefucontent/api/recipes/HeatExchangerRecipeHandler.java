package com.m_w_k.gtcefucontent.api.recipes;

import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.getTemp;

import java.util.*;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCHeatExchangerLoader;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public final class HeatExchangerRecipeHandler {

    /**
     * Approximate conversion of heat units to EU, assuming that the fluid heater is perfectly efficient.
     */
    public static final long HEU = GTCEFuCHeatExchangerLoader.WATER_TO_STEAM_ENERGY / 150;

    private static final Map<Fluid, HalfExchangeData> HEATING_MAP = new Object2ObjectOpenHashMap<>();
    private static final Map<Fluid, HalfExchangeData> COOLING_MAP = new Object2ObjectOpenHashMap<>();
    private static final Set<EutecticFluid> EUTECTICS = new ObjectOpenHashSet<>();

    public static void registerEutectic(EutecticFluid fluid) {
        EUTECTICS.add(fluid);
    }

    /**
     * Registers a two-way heat exchange. Use this overload for simple fluid heating and cooling. Do not do this for
     * Eutectics!
     * 
     * @param fluidA          The first fluid
     * @param fluidB          The second fluid
     * @param thermalCapacity The energy required to increase/energy released by decreasing the temp of fluid A by 1
     *                        degree,in order to reach fluid B, in units of J.
     *                        This means that 418600 units would increase the temperature of 1L water by 1°K,
     *                        as the thermal capacity of water is 418.6 kJ/kg, or kJ/L.
     *                        <br>
     *                        <br>
     *                        Note that if this is negative when fluid B is hotter than fluid A,
     *                        it would release thermal energy to heat fluid A into fluid B, and vice versa.
     */
    public static void registerHeatExchange(FluidStack fluidA, FluidStack fluidB, int thermalCapacity) {
        registerHeatExchange(fluidA, fluidB, thermalCapacity, true);
    }

    /**
     * Registers a heat exchange. Use this overload for simple fluid heating and cooling. Do not do this for Eutectics!
     * 
     * @param fluidA          The first fluid
     * @param fluidB          The second fluid
     * @param thermalCapacity The energy required to increase/energy released by decreasing the temp of fluid A by 1
     *                        degree,in order to reach fluid B, in units of J.
     *                        This means that 418600 units would increase the temperature of 1L water by 1°K,
     *                        as the thermal capacity of water is 418.6 kJ/kg, or kJ/L.
     *                        <br>
     *                        <br>
     *                        Note that if this is negative when fluid B is hotter than fluid A,
     *                        it would release thermal energy to heat fluid A into fluid B, and vice versa.
     * @param isTwoWay        Whether the heat exchange should be reversible.
     */
    public static void registerHeatExchange(FluidStack fluidA, FluidStack fluidB, int thermalCapacity,
                                            boolean isTwoWay) {
        int tempDifference = getTemp(fluidB) - getTemp(fluidA);
        // If tempDifference is positive, then we are heating fluid, and vice versa.
        // No zero-energy conversions, they would cause divide by 0 errors.
        if (thermalCapacity == 0)
            GTCEFuContent.log("Someone tried to register a zero-energy Heat Exchanger recipe. THIS IS INVALID!",
                    GTCEFuContent.LogType.WARN);
        else if (thermalCapacity > 0) {
            HEATING_MAP.put(fluidA.getFluid(), new HalfExchangeData(fluidA, fluidB, thermalCapacity));
            if (isTwoWay) COOLING_MAP.put(fluidB.getFluid(), new HalfExchangeData(fluidB, fluidA, thermalCapacity));
        } else {
            COOLING_MAP.put(fluidA.getFluid(), new HalfExchangeData(fluidA, fluidB, -thermalCapacity));
            if (isTwoWay) HEATING_MAP.put(fluidB.getFluid(), new HalfExchangeData(fluidB, fluidA, -thermalCapacity));

        }
    }

    /**
     * Registers a two-way heat exchange. Use this overload for more complex conversions. Do not do this for Eutectics!
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
     * Registers a heat exchange. Use this overload for more complex conversions. Do not do this for Eutectics!
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
        int tempDifference = getTemp(fluidB) - getTemp(fluidA);
        registerHeatExchange(fluidA, fluidB, (int) (thermalEnergy / tempDifference), isTwoWay);
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
     * Find out whether we can do a heat exchange with a given fluid, given a direction of exchange and a
     * temperature limit.
     *
     * @param fluid The first fluid
     * @param type  The direction of exchange. Should not be {@link ExchangeType#BOTH}
     * @return the exchange data
     */
    @Nullable
    public static HalfExchangeData getHeatExchange(FluidStack fluid, ExchangeType type,
                                                   int temperatureLimit) {
        if (type == ExchangeType.HEATING) {
            if (getTemp(fluid) >= temperatureLimit) return null;
            HalfExchangeData data = getHeating(fluid);
            if (data == null) return null;
            // We can't heat to more than the temperature limit
            if (getTemp(data.out) > temperatureLimit) {
                EutecticFluid eutectic = data.getEutectic();
                if (eutectic != null) {
                    FluidStack newOut = eutectic.getWithTemperature(data.out, temperatureLimit);
                    if (getTemp(newOut) <= temperatureLimit) return HalfExchangeData.withNewOut(data, newOut);
                }
            }
        } else if (type == ExchangeType.COOLING) {
            if (getTemp(fluid) <= temperatureLimit) return null;
            HalfExchangeData data = getCooling(fluid);
            if (data == null) return null;
            // We can't cool fluid to less than the temperature limit
            if (getTemp(data.out) < temperatureLimit) {
                EutecticFluid eutectic = data.getEutectic();
                if (eutectic != null) {
                    FluidStack newOut = eutectic.getWithTemperature(data.out, temperatureLimit);
                    if (getTemp(newOut) >= temperatureLimit) return HalfExchangeData.withNewOut(data, newOut);
                }
            }
        }
        return null;
    }

    /**
     * Find out whether we can do a heat exchange with two fluids, and if so return the exchange.
     * 
     * @param fluidA The first fluid
     * @param fluidB The second fluid
     * @return the exchange data
     */
    @Nullable
    public static FullExchangeData getHeatExchange(FluidStack fluidA, FluidStack fluidB) {
        int tempDifference = getTemp(fluidB) - getTemp(fluidA);
        if (tempDifference == 0) return null;

        HalfExchangeData A;
        HalfExchangeData B;

        boolean heatA = tempDifference > 0;

        if (heatA) {
            // Heating A, Cooling B
            A = getHeating(fluidA);
            B = getCooling(fluidB);
        } else {
            // Cooling A, Heating B
            A = getCooling(fluidA);
            B = getHeating(fluidB);
        }

        if (A == null || B == null) return null;

        if (heatA) {
            // We can't heat A to more than B's starting temp, and we can't cool B to less than A's starting temp
            int bIn = getTemp(B.in);
            if (getTemp(A.out) > bIn) {
                EutecticFluid eutectic = A.getEutectic();
                if (eutectic != null) {
                    FluidStack newOut = eutectic.getWithTemperature(A.out, bIn);
                    if (getTemp(newOut) > bIn) return null;
                    else A = HalfExchangeData.withNewOut(A, newOut);
                } else return null;
            }
            int aIn = getTemp(A.in);
            if (getTemp(B.out) < aIn) {
                EutecticFluid eutectic = B.getEutectic();
                if (eutectic != null) {
                    FluidStack newOut = eutectic.getWithTemperature(B.out, bIn);
                    if (getTemp(newOut) < bIn) return null;
                    else B = HalfExchangeData.withNewOut(B, newOut);
                } else return null;
            }
        } else {
            // We can't cool A to less than B's starting temp, and we can't heat B to more than A's starting temp
            int bIn = getTemp(B.in);
            if (getTemp(A.out) < bIn) {
                EutecticFluid eutectic = A.getEutectic();
                if (eutectic != null) {
                    FluidStack newOut = eutectic.getWithTemperature(A.out, bIn);
                    if (getTemp(newOut) < bIn) return null;
                    else A = HalfExchangeData.withNewOut(A, newOut);
                } else return null;
            }
            int aIn = getTemp(A.in);
            if (getTemp(B.out) > aIn) {
                EutecticFluid eutectic = B.getEutectic();
                if (eutectic != null) {
                    FluidStack newOut = eutectic.getWithTemperature(B.out, bIn);
                    if (getTemp(newOut) > bIn) return null;
                    else B = HalfExchangeData.withNewOut(B, newOut);
                } else return null;
            }
        }

        return new FullExchangeData(A, B);
    }

    private static HalfExchangeData getHeating(FluidStack fluid) {
        HalfExchangeData mapped = HEATING_MAP.get(fluid.getFluid());
        if (mapped != null) return mapped;
        if (fluid.getFluid() instanceof EutecticFluid eutectic && eutectic.getMaxTemperature() > getTemp(fluid)) {
            fluid = new FluidStack(fluid, 1);
            return new HalfExchangeData(fluid, eutectic.getWithTemperature(fluid, Integer.MAX_VALUE),
                    eutectic.getThermalCapacity());
        } else return null;
    }

    private static HalfExchangeData getCooling(FluidStack fluid) {
        HalfExchangeData mapped = COOLING_MAP.get(fluid.getFluid());
        if (mapped != null) return mapped;
        if (fluid.getFluid() instanceof EutecticFluid eutectic && eutectic.getMinTemperature() < getTemp(fluid)) {
            fluid = new FluidStack(fluid, 1);
            return new HalfExchangeData(fluid, eutectic.getWithTemperature(fluid, 0), eutectic.getThermalCapacity());
        } else return null;
    }

    /**
     * Gets a copy of the cooling map.
     * 
     * @return Copy of the cooling map.
     */
    public static Map<Fluid, HalfExchangeData> getCoolingMapCopy() {
        // We don't want people to be able to modify the map directly
        return new Object2ObjectOpenHashMap<>(COOLING_MAP);
    }

    /**
     * Gets a copy of the heating map.
     *
     * @return Copy of the heating map.
     */
    public static Map<Fluid, HalfExchangeData> getHeatingMapCopy() {
        // We don't want people to be able to modify the map directly
        return new Object2ObjectOpenHashMap<>(HEATING_MAP);
    }

    /**
     * Gets a copy of the registered eutectics.
     *
     * @return Copy of the registered eutectics.
     */
    public static Set<EutecticFluid> getEutecticsCopy() {
        // We don't want people to be able to modify the map directly
        return new ObjectOpenHashSet<>(EUTECTICS);
    }

    /**
     * Check if a given fluid can be cooled.
     *
     * @param fluid The fluid to check.
     * @return Whether the fluid can be cooled.
     */
    public static boolean isCoolable(Fluid fluid) {
        return isEutectic(fluid) || COOLING_MAP.containsKey(fluid);
    }

    /**
     * Check if a given fluid can be heated.
     *
     * @param fluid The fluid to check.
     * @return Whether the fluid can be heated.
     */
    public static boolean isHeatable(Fluid fluid) {
        return isEutectic(fluid) || HEATING_MAP.containsKey(fluid);
    }

    /**
     * Check if a given fluid is a eutectic alloy.
     * 
     * @param fluid The fluid to check.
     * @return Whether the fluid is a eutectic alloy.
     */
    public static boolean isEutectic(Fluid fluid) {
        return fluid instanceof EutecticFluid;
    }
}
