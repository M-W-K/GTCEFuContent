package com.m_w_k.gtcefucontent.api.damagesources;

import net.minecraft.util.DamageSource;

public final class GTCEFuCDamageSources {

    private static final DamageSource SUFFOCATION = new DamageSource("suffocation").setDamageBypassesArmor();
    private static final DamageSource TOXIC_ATMO = new DamageSource("toxic_atmo").setDamageBypassesArmor();

    public static DamageSource getSuffocationDamage() {
        return SUFFOCATION;
    }

    public static DamageSource getToxicAtmoDamage() {
        return TOXIC_ATMO;
    }
}
