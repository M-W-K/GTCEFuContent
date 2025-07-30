package com.m_w_k.gtcefucontent.common.item.armor;

import static net.minecraft.inventory.EntityEquipmentSlot.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.m_w_k.gtcefucontent.common.DimensionBreathabilityHandler;

import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.armor.IArmorLogic;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.api.items.metaitem.stats.IItemDurabilityManager;

public class SimpleGasMask implements IArmorLogic, IItemDurabilityManager {

    static final int DURABILITY = 12000;

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
        }, this);
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

    @Override
    public boolean canBreakWithDamage(ItemStack stack) {
        return true;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack itemStack, DamageSource source, int damage,
                            EntityEquipmentSlot equipmentSlot) {
        if (DimensionBreathabilityHandler.DamageType.is(source)) {
            int dur = getDurabilityLeft(itemStack) - damage;
            if (dur <= 0) {
                itemStack.shrink(1);
                return;
            }
            setDurabilityLeft(itemStack, dur);
        }
    }

    public final int getDurabilityLeft(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null || !tagCompound.hasKey("durability", Constants.NBT.TAG_INT))
            return DURABILITY;
        return tagCompound.getInteger("durability");
    }

    public static void setDurabilityLeft(ItemStack itemStack, int usesLeft) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
            itemStack.setTagCompound(tagCompound);
        }
        tagCompound.setInteger("durability", usesLeft);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        return (double) getDurabilityLeft(itemStack) / DURABILITY;
    }
}
