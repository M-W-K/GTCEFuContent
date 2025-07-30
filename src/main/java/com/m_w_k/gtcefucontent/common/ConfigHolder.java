package com.m_w_k.gtcefucontent.common;

import net.minecraftforge.common.config.Config;

import com.m_w_k.gtcefucontent.GTCEFuContent;

@SuppressWarnings("CanBeFinal")
@Config(modid = GTCEFuContent.MODID)
public class ConfigHolder {

    @Config.Comment({ "Whether suffocation damage based on dimension is enabled.", "Default: true" })
    public static boolean enableDimSuffocation = true;

    @Config.Comment({ "Whether toxicity damage based on dimension is enabled.", "Default: true" })
    public static boolean enableDimToxicity = true;

    @Config.Comment({ "Whether radiation damage based on dimension is enabled.", "Default: true" })
    public static boolean enableDimRadiation = true;

    @Config.Comment({ "What dimensions have which hazards. Pair dimension IDs with their hazards and rating.",
            "Suffocation - s | Toxic - t | Radiation - r" })
    @Config.RequiresWorldRestart
    public static String[] dimensionHazards = new String[] { "default|", "0|", "-1|st30", "1|st80r100" };

    @Config.Comment({ "What items protect from which hazards. Non-helmets cannot block suffocation or toxicity." +
            "Pair items with their hazards and protection rating. Suffocation 1 is sealed, 0 is unsealed.",
            "Add the postscript 'd' to any rating to make the item take durability damage from the source.",
            "Numbers after the 'd' will be interpreted as the amount of durability damage to take.",
            "Suffocation - s | Toxic - t | Radiation - r" })
    @Config.RequiresWorldRestart
    public static String[] armorHazardProtection = new String[] { "gtcefucontent:gtcefuc_armor:1|s0d1t30d10",
            "gtcefucontent:gtcefuc_armor:2|s1d1t30d10", "gregtech:gt_armor:20|s1t30d1r10", "gregtech:gt_armor:21|r16",
            "gregtech:gt_armor:22|r14", "gregtech:gt_armor:23|r8", "gregtech:gt_armor:30|r22",
            "gregtech:gt_armor:40|s1d1t30d1r20", "gregtech:gt_armor:41|r26", "gregtech:gt_armor:42|r24",
            "gregtech:gt_armor:43|r18", "gregtech:gt_armor:50|r48" };
    /*
     * In order: primitive mask w/ suffocation unsealed and toxic 3-
     * normal mask w/ suffocation sealed and toxic 30
     * nano suite helm w/ suffocation sealed, toxic 30, and radiation 10
     * nano suite chest w/ radiation 16
     * nano suite leggings w/ radiation 14
     * nano suite boots w/ radiation 8
     * nano suite adv. chest w/ radiation 22
     * quark suite helm w/ suffocation sealed, toxic 30, and radiation 20
     * quark suite chest w/ radiation 26
     * quark suite leggings w/ radiation 24
     * quark suite boots w/ radiation 18
     * quark suite adv. chest w/ radiation 48
     */

    @Config.Comment("Config settings applying to the Linear Forging Furnace")
    @Config.Name("Linear Forging Furnace Settings")
    public static LinearForgingFurnaceSettings linearForgingFurnaceSettings = new LinearForgingFurnaceSettings();

    @Config.Comment({ "Whether to override ProjectEX collector & relay recipes with gregified ones.",
            "Default: true" })
    @Config.RequiresMcRestart
    public static boolean overrideProjectEXGeneration = true;

    @Config.Comment({ "The multiplicative bonus per unique fuel used in the Sympathetic Combustor.",
            "0 means no bonus, 100 means 100% extra efficiency per.",
            "Default: 15" })
    @Config.RangeInt(min = 0, max = 100)
    public static int sympatheticCombustorBonus = 15;

    @Config.Comment({
            "Whether the Heat Disperser should explode when overticked, or simply not make use of the extra ticks.",
            "Default: true" })
    public static boolean heatDisperserExplodesOnOvertick = true;

    public static class LinearForgingFurnaceSettings {

        @Config.Comment({ "Sets the multiplier to duration applied for cooling recipes.",
                "Default: 1" })
        @Config.RequiresMcRestart
        @Config.RangeDouble(
                            min = 0.1,
                            max = 2.0)
        public double coolingDurationModifier = 1.0;

        @Config.Comment({ "Sets the flat temperature penalty applied for cooling recipes.",
                "Default: 0" })
        @Config.RequiresMcRestart
        @Config.RangeInt(
                         min = 0,
                         max = 2000)
        public int coolingTemperaturePenalty = 0;

        @Config.Comment({ "Sets the multiplier to duration applied for forging recipes.",
                "Default: 1.05" })
        @Config.RequiresMcRestart
        @Config.RangeDouble(
                            min = 0.1,
                            max = 2.0)
        public double forgingDurationModifier = 1.05;

        @Config.Comment({ "Sets the flat temperature penalty applied for forging recipes.",
                "Default: 200" })
        @Config.RequiresMcRestart
        @Config.RangeInt(
                         min = 0,
                         max = 2000)
        public int forgingTemperaturePenalty = 200;
    }
}
