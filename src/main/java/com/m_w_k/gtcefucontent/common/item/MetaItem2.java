package com.m_w_k.gtcefucontent.common.item;

import static com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems.*;

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;

import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.StandardMetaItem;

public class MetaItem2 extends StandardMetaItem {

    private final AtomicInteger ID = new AtomicInteger(22550);

    public MetaItem2() {
        super();
    }

    @Override
    public void registerSubItems() {
        CRYSTAL_VOID = addItem(ID.getAndIncrement(), "crystal.void").setRarity(EnumRarity.UNCOMMON);
        CRYSTAL_ENDER = addItem(ID.getAndIncrement(), "crystal.ender");
        CRYSTAL_STARLIGHT = addItem(ID.getAndIncrement(), "crystal.starlight").setRarity(EnumRarity.RARE);
        CRYSTAL_ENDLIGHT = addItem(ID.getAndIncrement(), "crystal.endlight").setRarity(EnumRarity.RARE);
        CRYSTAL_VOIDLIGHT = addItem(ID.getAndIncrement(), "crystal.voidlight").setRarity(EnumRarity.EPIC);

        POWDER_VOID = addItem(ID.getAndIncrement(), "powder.void").setRarity(EnumRarity.UNCOMMON);
        POWDER_ENDER = addItem(ID.getAndIncrement(), "powder.ender");
        POWDER_STARLIGHT = addItem(ID.getAndIncrement(), "powder.starlight").setRarity(EnumRarity.RARE);
        POWDER_ENDLIGHT = addItem(ID.getAndIncrement(), "powder.endlight").setRarity(EnumRarity.RARE);

        STELLAR_NUGGET = addItem(ID.getAndIncrement(), "nugget.stellar_alloy").setRarity(EnumRarity.EPIC);
        STELLAR_INGOT = addItem(ID.getAndIncrement(), "ingot.stellar_alloy").setRarity(EnumRarity.EPIC);
        STELLAR_BALL = addItem(ID.getAndIncrement(), "ball.stellar_alloy").setRarity(EnumRarity.EPIC);

        INFINITY_REAGENT = addItem(ID.getAndIncrement(), "infinity_reagent");
    }

    @Override
    public ResourceLocation createItemModelPath(MetaItem<?>.MetaValueItem metaValueItem, String postfix) {
        return GTCEFuCUtil.gtcefucId(formatModelPath(metaValueItem) + postfix);
    }
}
