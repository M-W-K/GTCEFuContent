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
            "gtcefucontent:gtcefuc_armor:2|s1t15", "gregtech:gt_armor:20|s1t15r5", "gregtech:gt_armor:21|r8",
            "gregtech:gt_armor:22|r7", "gregtech:gt_armor:23|r4", "gregtech:gt_armor:30|r16",
            "gregtech:gt_armor:40|s1t20r20", "gregtech:gt_armor:41|r26", "gregtech:gt_armor:42|r24",
            "gregtech:gt_armor:43|r18", "gregtech:gt_armor:50|r52" };
    /*
     * In order: primitive mask w/ suffocation unsealed and toxic 10
     * normal mask w/ suffocation sealed and toxic 15
     * nano suite helm w/ suffocation sealed, toxic 15, and radiation 5
     * nano suite chest w/ radiation 8
     * nano suite leggings w/ radiation 7
     * nano suite boots w/ radiation 4
     * nano suite adv. chest w/ radiation 16
     * quark suite helm w/ suffocation sealed, toxic 20, and radiation 20
     * quark suite chest w/ radiation 26
     * quark suite leggings w/ radiation 24
     * quark suite boots w/ radiation 18
     * quark suite adv. chest w/ radiation 52
     */

    @Config.Comment({ "Whether to override ProjectEX collector & relay recipes with gregified ones." })
    @Config.RequiresMcRestart
    public static boolean overrideProjectEXGeneration = true;
}
