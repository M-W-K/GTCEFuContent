package com.m_w_k.gtcefucontent.common.item;

import static com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems.*;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;

import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.StandardMetaItem;

public class GTCEFuCMetaItem extends StandardMetaItem {

    private int id = 0;

    public GTCEFuCMetaItem() {
        super();
    }

    @Override
    public void registerSubItems() {
        CRYSTAL_VOID = addItem(id++, "crystal.void").setRarity(EnumRarity.UNCOMMON);
        CRYSTAL_ENDER = addItem(id++, "crystal.ender");
        CRYSTAL_STARLIGHT = addItem(id++, "crystal.starlight").setRarity(EnumRarity.RARE);
        CRYSTAL_ENDLIGHT = addItem(id++, "crystal.endlight").setRarity(EnumRarity.RARE);
        CRYSTAL_VOIDLIGHT = addItem(id++, "crystal.voidlight").setRarity(EnumRarity.EPIC);

        POWDER_VOID = addItem(id++, "powder.void").setRarity(EnumRarity.UNCOMMON);
        POWDER_ENDER = addItem(id++, "powder.ender");
        POWDER_STARLIGHT = addItem(id++, "powder.starlight").setRarity(EnumRarity.RARE);
        POWDER_ENDLIGHT = addItem(id++, "powder.endlight").setRarity(EnumRarity.RARE);

        STELLAR_NUGGET = addItem(id++, "nugget.stellar_alloy").setRarity(EnumRarity.EPIC);
        STELLAR_INGOT = addItem(id++, "ingot.stellar_alloy").setRarity(EnumRarity.EPIC);
        STELLAR_BALL = addItem(id++, "ball.stellar_alloy").setRarity(EnumRarity.EPIC);

        INFINITY_REAGENT = addItem(id++, "infinity_reagent");

        REGRET = addItem(id++, "regret").setInvisible().setMaxStackSize(1);
    }

    @Override
    public ResourceLocation createItemModelPath(MetaItem<?>.MetaValueItem metaValueItem, String postfix) {
        return GTCEFuCUtil.gtcefucId(formatModelPath(metaValueItem) + postfix);
    }
}
