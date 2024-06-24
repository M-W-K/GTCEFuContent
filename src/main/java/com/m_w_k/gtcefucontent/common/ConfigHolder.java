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
    public static String[] dimensionAirHazards = new String[] { "default|", "0|", "-1|st100", "1|st100r100" };

    @Config.Comment({ "What items protect from which hazards. Non-helmets cannot block suffocation or toxicity." +
            "Pair items with their hazards and protection rating. Suffocation 1 is sealed, 0 is unsealed.",
            "Suffocation - s | Toxic - t | Radiation - r" })
    @Config.RequiresWorldRestart
    public static String[] itemHazardProtection = new String[] { "gtcefucontent:gtcefuc_armor:1|s0t10",
            "gtcefucontent:gtcefuc_armor:2|s1t15", "gregtech:gt_armor:20|s1t15r10", "gregtech:gt_armor:21|r16",
            "gregtech:gt_armor:22|r14", "gregtech:gt_armor:23|r8", "gregtech:gt_armor:30|r22",
            "gregtech:gt_armor:40|s1t20r20", "gregtech:gt_armor:41|r26", "gregtech:gt_armor:42|r24",
            "gregtech:gt_armor:43|r18", "gregtech:gt_armor:50|r48" };
    /*
     * In order: primitive mask w/ suffocation unsealed and toxic 10
     * normal mask w/ suffocation sealed and toxic 15
     * nano suite helm w/ suffocation sealed, toxic 15, and radiation 10
     * nano suite chest w/ radiation 16
     * nano suite leggings w/ radiation 14
     * nano suite boots w/ radiation 8
     * nano suite adv. chest w/ radiation 22
     * quark suite helm w/ suffocation sealed, toxic 20, and radiation 20
     * quark suite chest w/ radiation 26
     * quark suite leggings w/ radiation 24
     * quark suite boots w/ radiation 18
     * quark suite adv. chest w/ radiation 48
     */

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
}
