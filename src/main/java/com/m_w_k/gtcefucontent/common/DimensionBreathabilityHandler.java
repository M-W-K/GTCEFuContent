package com.m_w_k.gtcefucontent.common;

import static net.minecraft.inventory.EntityEquipmentSlot.*;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import com.github.bsideup.jabel.Desugar;
import com.m_w_k.gtcefucontent.api.damagesources.GTCEFuCDamageSources;

import gregtech.api.damagesources.DamageSources;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.unification.material.Materials;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public final class DimensionBreathabilityHandler {

    private static FluidStack oxyStack;
    private static final Int2ObjectOpenHashMap<DimBreathabilityInfo> dimensionBreathabilityMap = new Int2ObjectOpenHashMap<>();
    private static DimBreathabilityInfo defaultDimensionBreathability;
    private static final Object2ObjectOpenHashMap<BreathabilityItemMapKey, ItemBreathabilityInfo> itemBreathabilityMap = new Object2ObjectOpenHashMap<>();

    private static boolean hasDrainedOxy = false;
    private static boolean hasSuffocated = false;

    private DimensionBreathabilityHandler() {}

    public static void loadConfig() {
        oxyStack = Materials.Oxygen.getFluid(1);

        dimensionBreathabilityMap.clear();
        defaultDimensionBreathability = new DimBreathabilityInfo(false, 0, 0);
        String[] configData = ConfigHolder.dimensionHazards;
        for (String dim : configData) {
            try {
                String[] d = dim.concat(" ").split("\\|");
                if (d.length != 2) throw new Exception();
                // lookahead to split into 's' 't93' 'r3' units
                String[] breaths = d[1].split("(?=[str])");
                boolean s = false;
                int t = 0;
                int r = 0;
                for (String breath : breaths) {
                    switch (breath.charAt(0)) {
                        case 's' -> s = true;
                        case 't' -> t = Integer.parseInt(breath.substring(1).trim());
                        case 'r' -> r = Integer.parseInt(breath.substring(1).trim());
                    }
                }
                DimBreathabilityInfo info = new DimBreathabilityInfo(s, t, r);

                if (Objects.equals(d[0], "default")) defaultDimensionBreathability = info;
                else dimensionBreathabilityMap.put(Integer.parseInt(d[0]), info);

            } catch (Exception e) {
                // Should I instead throw a soft error and ignore the dimension?
                throw new IllegalArgumentException("Unparsable dim breathability data: " + dim);
            }
        }

        itemBreathabilityMap.clear();
        configData = ConfigHolder.armorHazardProtection;

        for (String item : configData) {
            try {
                String[] parse = item.concat(" ").split("\\|");
                if (parse.length != 2) throw new Exception();
                // lookahead to split into 's' 't93d2' 'r3' units
                String[] breaths = parse[1].split("(?=[str])");
                boolean s = false;
                int ds = 0;
                boolean sealed = false;
                int t = 0;
                int dt = 0;
                int r = 0;
                int dr = 0;
                for (String breath : breaths) {
                    String[] damageSplit = breath.split("d");
                    final int i = Integer.parseInt(damageSplit[0].substring(1).trim());
                    final int d = damageSplit.length > 1 ? Integer.parseInt(damageSplit[1].trim()) : 0;
                    switch (breath.charAt(0)) {
                        case 's' -> {
                            s = true;
                            sealed = i == 1;
                            ds = d;
                        }
                        case 't' -> {
                            t = i;
                            dt = d;
                        }
                        case 'r' -> {
                            r = i;
                            dr = d;
                        }
                    }
                }
                ItemBreathabilityInfo info = new ItemBreathabilityInfo(s, ds, t, dt, r, dr, sealed);

                String[] e = parse[0].split(":");
                itemBreathabilityMap.put(new BreathabilityItemMapKey(
                        Item.getByNameOrId(e[0] + ":" + e[1]), e.length == 3 ? Integer.parseInt(e[2]) : 0), info);

            } catch (Exception e) {
                // Should I instead throw a soft error and ignore the item?
                throw new IllegalArgumentException("Unparsable item breathability data: " + item);
            }
        }
    }

    public static void checkPlayer(EntityPlayer player) {
        DimBreathabilityInfo dimInfo = dimensionBreathabilityMap.getOrDefault(player.dimension,
                defaultDimensionBreathability);
        if (ConfigHolder.enableDimSuffocation && dimInfo.suffocation) suffocationCheck(player);
        if (ConfigHolder.enableDimToxicity && dimInfo.toxicity()) toxicityCheck(player, dimInfo.toxicityRating);
        if (ConfigHolder.enableDimRadiation && dimInfo.radiation()) radiationCheck(player, dimInfo.radiationRating);
        hasDrainedOxy = false;
        hasSuffocated = false;
    }

    private static void suffocationCheck(EntityPlayer player) {
        ItemBreathabilityInfo itemInfo = itemBreathabilityMap.get(getItemKey(player, HEAD));
        if (itemInfo != null && itemInfo.suffocation && drainOxy(player)) {
            itemInfo.damageArmor(player, HEAD, DamageType.SUFFOCATION);
            return;
        }
        suffocate(player);
    }

    private static void suffocate(EntityPlayer player) {
        if (hasSuffocated) return;
        player.attackEntityFrom(DamageType.SUFFOCATION.source(), 2);
        hasSuffocated = true;
    }

    private static void toxicityCheck(EntityPlayer player, int dimRating) {
        ItemBreathabilityInfo itemInfo = itemBreathabilityMap.get(getItemKey(player, HEAD));
        if (itemInfo != null && itemInfo.toxicity()) {
            itemInfo.damageArmor(player, HEAD, DamageType.TOXICITY);
            if (dimRating > itemInfo.toxicityRating) {
                // if sealed, do not toxificate
                if (itemInfo.isSealed) {
                    if (!drainOxy(player) && ConfigHolder.enableDimSuffocation) suffocate(player);
                } else {
                    toxificate(player, dimRating - itemInfo.toxicityRating);
                }
            }
        } else {
            toxificate(player, dimRating);
        }
    }

    private static void toxificate(EntityPlayer player, int mult) {
        float damage = 0.03f * mult;
        if (Math.random() < damage) {
            player.attackEntityFrom(DamageType.TOXICITY.source(), Math.max(1, damage));
        }
    }

    private static void radiationCheck(EntityPlayer player, int dimRating) {
        // natural radiation protection based on missing health
        int ratingSum = MathHelper.ceil(Math.log1p(player.getMaxHealth() - player.getHealth()) * 6);

        ItemBreathabilityInfo itemInfo = itemBreathabilityMap.get(getItemKey(player, HEAD));
        if (itemInfo != null && itemInfo.radiation()) {
            ratingSum += itemInfo.radiationRating;
            itemInfo.damageArmor(player, HEAD, DamageType.RADIATION);
        }
        itemInfo = itemBreathabilityMap.get(getItemKey(player, CHEST));
        if (itemInfo != null && itemInfo.radiation()) {
            ratingSum += itemInfo.radiationRating;
            itemInfo.damageArmor(player, CHEST, DamageType.RADIATION);
        }
        itemInfo = itemBreathabilityMap.get(getItemKey(player, LEGS));
        if (itemInfo != null && itemInfo.radiation()) {
            ratingSum += itemInfo.radiationRating;
            itemInfo.damageArmor(player, LEGS, DamageType.RADIATION);
        }
        itemInfo = itemBreathabilityMap.get(getItemKey(player, FEET));
        if (itemInfo != null && itemInfo.radiation()) {
            ratingSum += itemInfo.radiationRating;
            itemInfo.damageArmor(player, FEET, DamageType.RADIATION);
        }

        if (dimRating > ratingSum) radiate(player, dimRating - ratingSum);
    }

    private static void radiate(EntityPlayer player, int mult) {
        float damage = 0.01f * mult;
        if (Math.random() < damage) {
            player.attackEntityFrom(DamageType.RADIATION.source(), Math.max(1, damage));
        }
    }

    private static BreathabilityItemMapKey getItemKey(EntityPlayer player, EntityEquipmentSlot slot) {
        return new BreathabilityItemMapKey(player.getItemStackFromSlot(slot));
    }

    private static boolean drainOxy(EntityPlayer player) {
        // don't drain if we are in creative
        if (player.isCreative()) return true;
        Optional<IFluidHandlerItem> tank = player.inventory.mainInventory.stream()
                .map(a -> a.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
                .filter(Objects::nonNull)
                .filter(a -> {
                    FluidStack drain = a.drain(oxyStack, false);
                    return drain != null && drain.amount > 0;
                }).findFirst();
        // don't drain if we've already drained
        if (!hasDrainedOxy) {
            tank.ifPresent(a -> a.drain(oxyStack, true));
            hasDrainedOxy = true;
        }
        return tank.isPresent();
    }

    public void addBreathabilityItem(ItemStack item, ItemBreathabilityInfo info) {
        itemBreathabilityMap.put(new BreathabilityItemMapKey(item), info);
    }

    public void removeBreathabilityItem(ItemStack item) {
        itemBreathabilityMap.remove(new BreathabilityItemMapKey(item));
    }

    private static final class BreathabilityItemMapKey {

        public final Item item;
        public final int meta;

        BreathabilityItemMapKey(ItemStack stack) {
            this.item = stack.getItem();
            this.meta = stack.getMetadata();
        }

        BreathabilityItemMapKey(Item item, int meta) {
            this.item = item;
            this.meta = meta;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BreathabilityItemMapKey that = (BreathabilityItemMapKey) o;
            return meta == that.meta && Objects.equals(item, that.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, meta);
        }
    }

    @Desugar
    public record DimBreathabilityInfo(boolean suffocation, int toxicityRating, int radiationRating) {

        public boolean toxicity() {
            return toxicityRating > 0;
        }

        public boolean radiation() {
            return radiationRating > 0;
        }
    }

    @Desugar
    public record ItemBreathabilityInfo(boolean suffocation, int suffocationDurabilityDamage, int toxicityRating,
                                        int toxicityDurabilityDamage, int radiationRating,
                                        int radiationDurabilityDamage, boolean isSealed) {

        public boolean toxicity() {
            return toxicityRating > 0;
        }

        public boolean radiation() {
            return radiationRating > 0;
        }

        void damageArmor(EntityPlayer player, EntityEquipmentSlot slot, DamageType type) {
            int damage = switch (type) {
                case SUFFOCATION -> suffocationDurabilityDamage;
                case TOXICITY -> toxicityDurabilityDamage;
                case RADIATION -> radiationDurabilityDamage;
            };
            if (damage > 0) {
                ItemStack stack = player.getItemStackFromSlot(slot);
                if (stack.getItem() instanceof ArmorMetaItem<?>item) {
                    item.damageArmor(player, stack, type.source(), damage, slot.getIndex());
                } else {
                    stack.damageItem(damage, player);
                }
            }
        }
    }

    public enum DamageType {

        SUFFOCATION,
        TOXICITY,
        RADIATION;

        public DamageSource source() {
            return switch (this) {
                case SUFFOCATION -> GTCEFuCDamageSources.getSuffocationDamage();
                case TOXICITY -> GTCEFuCDamageSources.getToxicAtmoDamage();
                case RADIATION -> DamageSources.getRadioactiveDamage();
            };
        }

        public static boolean is(DamageSource source) {
            return source == DamageType.SUFFOCATION.source() || source == DamageType.TOXICITY.source() ||
                    source == DamageType.RADIATION.source();
        }
    }
}
