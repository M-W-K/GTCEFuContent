package com.m_w_k.gtcefucontent.api.recipes.logic;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import gregtech.api.recipes.logic.OverclockingLogic;
import scala.collection.concurrent.Debug;

import javax.annotation.Nonnull;

/**
 * Contains logic for performing a limited number of perfect overclocks
 */
public class LimitedPerfectOverclockingLogic {

    /**
     * applies modified logic for overclocking, where each overclock modifies energy and duration
     *
     * @param recipeEUt         the EU/t of the recipe to overclock
     * @param maxVoltage        the maximum voltage the recipe is allowed to be run at
     * @param recipeDuration    the duration of the recipe to overclock
     * @param durationDivisor   the value to divide the duration by for each overclock
     * @param voltageMultiplier the value to multiply the voltage by for each overclock
     * @param numberOfOCs       the maximum amount of overclocks allowed
     * @param perfectOCs        the maximum amount of perfect overclocks allowed
     * @return an int array of {OverclockedEUt, OverclockedDuration}
     */
    @Nonnull
    public static int[] limitedPerfectOverclockingLogic(int recipeEUt, long maxVoltage, int recipeDuration, int numberOfOCs, double durationDivisor, double voltageMultiplier, int perfectOCs) {
        double resultDuration = recipeDuration;
        double resultVoltage = recipeEUt;

        for (; numberOfOCs > 0; numberOfOCs--) {
            // perform a number of perfect overclocks equal to or less than the number of allowed perfect overclocks
            boolean perfectOC = perfectOCs > 0;
            perfectOCs--;

            // it is important to do voltage first,
            // so overclocking voltage does not go above the limit before changing duration

            double potentialVoltage = resultVoltage * (perfectOC ? OverclockingLogic.STANDARD_OVERCLOCK_VOLTAGE_MULTIPLIER : voltageMultiplier);
            // do not allow voltage to go above maximum
            if (potentialVoltage > maxVoltage) break;

            double potentialDuration = resultDuration / (perfectOC ? OverclockingLogic.PERFECT_OVERCLOCK_DURATION_DIVISOR : durationDivisor);
            // do not allow duration to go below one tick
            if (potentialDuration < 1) break;
            // update the duration for the next iteration
            resultDuration = potentialDuration;

            // update the voltage for the next iteration after everything else
            // in case duration overclocking would waste energy
            resultVoltage = potentialVoltage;
        }

        return new int[]{(int) resultVoltage, (int) resultDuration};
    }
}
