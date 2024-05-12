package com.m_w_k.gtcefucontent.common.item.armor;

import static net.minecraft.inventory.EntityEquipmentSlot.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.armor.IArmorLogic;
import gregtech.api.items.metaitem.stats.IItemBehaviour;

public class SimpleGasMask implements IArmorLogic {

    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack itemStack) {
        return HEAD;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "gtcefucontent:textures/armor/simple_gas_mask.png";
    }

    @Override
    public void addToolComponents(ArmorMetaItem.ArmorMetaValueItem mvi) {
        mvi.addComponents(new IItemBehaviour() {

            @Override
            public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
                return onRightClick(player, hand);
            }
        });
    }

    public ActionResult<ItemStack> onRightClick(EntityPlayer player, EnumHand hand) {
        if (player.getHeldItem(hand).getItem() instanceof ArmorMetaItem) {
            ItemStack armor = player.getHeldItem(hand);
            if (armor.getItem() instanceof ArmorMetaItem &&
                    player.inventory.armorInventory.get(HEAD.getIndex()).isEmpty() && !player.isSneaking()) {
                player.inventory.armorInventory.set(HEAD.getIndex(), armor.copy());
                player.setHeldItem(hand, ItemStack.EMPTY);
                player.playSound(new SoundEvent(new ResourceLocation("item.armor.equip_generic")), 1.0F, 1.0F);
                return ActionResult.newResult(EnumActionResult.SUCCESS, armor);
            }
        }

        return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
    }
}
