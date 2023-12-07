package com.m_w_k.gtcefucontent.common.item.armor;

import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.common.ConfigHolder;
import gregtech.common.items.MetaItems;
import gregtech.common.items.armor.NightvisionGoggles;
import net.minecraft.inventory.EntityEquipmentSlot;

public class GTCEFuCMetaArmor extends ArmorMetaItem<ArmorMetaItem<?>.ArmorMetaValueItem> {

    @Override
    public void registerSubItems() {

        GTCEFuCMetaItems.SIMPLE_GAS_MASK = addItem(1, "simple_gas_mask")
                .setArmorLogic(new SimpleGasMask());

        GTCEFuCMetaItems.GAS_MASK = addItem(2, "gas_mask").setArmorLogic(new GasMask(2,
                80_000L * (long) Math.max(1, Math.pow(1, ConfigHolder.tools.voltageTierNightVision - 1)),
                ConfigHolder.tools.voltageTierNightVision, EntityEquipmentSlot.HEAD));
    }
}
