package com.m_w_k.gtcefucontent.common.item;

import com.m_w_k.gtcefucontent.common.item.armor.GTCEFuCMetaArmor;

import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.metaitem.MetaItem;

public final class GTCEFuCMetaItems {

    private GTCEFuCMetaItems() {}

    public static MetaItem<?>.MetaValueItem CRYSTAL_VOID; // precient
    public static MetaItem<?>.MetaValueItem CRYSTAL_ENDER; // ender
    public static MetaItem<?>.MetaValueItem CRYSTAL_STARLIGHT; // pulsating
    public static MetaItem<?>.MetaValueItem CRYSTAL_ENDLIGHT; // vibrant
    public static MetaItem<?>.MetaValueItem CRYSTAL_VOIDLIGHT; // weather

    public static MetaItem<?>.MetaValueItem POWDER_VOID;
    public static MetaItem<?>.MetaValueItem POWDER_ENDER;
    public static MetaItem<?>.MetaValueItem POWDER_STARLIGHT;
    public static MetaItem<?>.MetaValueItem POWDER_ENDLIGHT;

    public static MetaItem<?>.MetaValueItem STELLAR_NUGGET;
    public static MetaItem<?>.MetaValueItem STELLAR_INGOT;
    public static MetaItem<?>.MetaValueItem STELLAR_BALL;

    public static MetaItem<?>.MetaValueItem INFINITY_REAGENT;

    public static ArmorMetaItem<?>.ArmorMetaValueItem SIMPLE_GAS_MASK;
    public static ArmorMetaItem<?>.ArmorMetaValueItem GAS_MASK;

    public static void init() {
        GTCEFuCMetaItem first = new GTCEFuCMetaItem();
        first.setRegistryName("meta_item_2");
        GTCEFuCMetaArmor armor = new GTCEFuCMetaArmor();
        armor.setRegistryName("gtcefuc_armor");
    }
}
