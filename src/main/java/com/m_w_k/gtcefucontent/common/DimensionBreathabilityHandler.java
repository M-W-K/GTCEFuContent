package com.m_w_k.gtcefucontent.common;

import static net.minecraft.inventory.EntityEquipmentSlot.*;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import com.m_w_k.gtcefucontent.api.damagesources.GTCEFuCDamageSources;

import gregtech.api.damagesources.DamageSources;
import gregtech.api.unification.material.Materials;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public final class DimensionBreathabilityHandler {

    private static FluidStack oxyStack;
    private static final Int2ObjectOpenHashMap<BreathabilityInfo> dimensionBreathabilityMap = new Int2ObjectOpenHashMap<>();
    private static BreathabilityInfo defaultDimensionBreathability;
    private static final Object2ObjectOpenHashMap<BreathabilityItemMapKey, BreathabilityInfo> itemBreathabilityMap = new Object2ObjectOpenHashMap<>();

    private static boolean hasDrainedOxy = false;
    private static boolean hasSuffocated = false;

    private DimensionBreathabilityHandler() {}

    public static void loadConfig() {
        oxyStack = Materials.Oxygen.getFluid(1);

        dimensionBreathabilityMap.clear();
        defaultDimensionBreathability = new BreathabilityInfo(false, false, -1, -1);
        String[] configData = ConfigHolder.dimensionAirHazards;
        for (String dim : configData) {
            try {
                String[] d = dim.concat(" ").split("\\|");
                if (d.length != 2) throw new Exception();
                // lookahead to split into 's' 't93' 'r3' units
                String[] breaths = d[1].split("(?=[str])");
                boolean s = false;
                int t = -1;
                int r = -1;
                for (String breath : breaths) {
                    switch (breath.charAt(0)) {
                        case 's' -> s = true;
                        case 't' -> t = Integer.parseInt(breath.substring(1).trim());
                        case 'r' -> r = Integer.parseInt(breath.substring(1).trim());
                    }
                }
                BreathabilityInfo info = new BreathabilityInfo(s, false, t, r);

                if (Objects.equals(d[0], "default")) defaultDimensionBreathability = info;
                else dimensionBreathabilityMap.put(Integer.parseInt(d[0]), info);

            } catch (Exception e) {
                // Should I instead throw a soft error and ignore the dimension?
                throw new IllegalArgumentException("Unparsable dim breathability data: " + dim);
            }
        }

        itemBreathabilityMap.clear();
        configData = ConfigHolder.itemHazardProtection;

        for (String item : configData) {
            try {
                String[] d = item.concat(" ").split("\\|");
                if (d.length != 2) throw new Exception();
                // lookahead to split into 's' 't93' 'r3' units
                String[] breaths = d[1].split("(?=[str])");
                boolean s = false;
                boolean sealed = false;
                int t = -1;
                int r = -1;
                for (String breath : breaths) {
                    final int i = Integer.parseInt(breath.substring(1).trim());
                    switch (breath.charAt(0)) {
                        case 's' -> {
                            s = true;
                            sealed = i == 1;
                        }
                        case 't' -> t = i;
                        case 'r' -> r = i;
                    }
                }
                BreathabilityInfo info = new BreathabilityInfo(s, sealed, t, r);

                String[] e = d[0].split(":");
                itemBreathabilityMap.put(new BreathabilityItemMapKey(
                        Item.getByNameOrId(e[0] + ":" + e[1]), e.length == 3 ? Integer.parseInt(e[2]) : 0), info);

            } catch (Exception e) {
                // Should I instead throw a soft error and ignore the item?
                throw new IllegalArgumentException("Unparsable item breathability data: " + item);
            }
        }
    }

    public static void checkPlayer(EntityPlayer player) {
        BreathabilityInfo dimInfo = dimensionBreathabilityMap.get(player.dimension);
        if (dimInfo == null) {
            dimInfo = defaultDimensionBreathability;
        }
        if (ConfigHolder.enableDimSuffocation && dimInfo.suffocation) suffocationCheck(player);
        if (ConfigHolder.enableDimToxicity && dimInfo.toxic) toxicityCheck(player, dimInfo.toxicityRating);
        if (ConfigHolder.enableDimRadiation && dimInfo.radiation) radiationCheck(player, dimInfo.radiationRating);
        hasDrainedOxy = false;
        hasSuffocated = false;
    }

    private static void suffocationCheck(EntityPlayer player) {
        BreathabilityInfo itemInfo = itemBreathabilityMap.get(getItemKey(player, HEAD));
        if (itemInfo != null && itemInfo.suffocation && drainOxy(player)) return;
        suffocate(player);
    }

    private static void suffocate(EntityPlayer player) {
        if (hasSuffocated) return;
        player.attackEntityFrom(GTCEFuCDamageSources.getSuffocationDamage(), 2);
        hasSuffocated = true;
    }

    private static void toxicityCheck(EntityPlayer player, int dimRating) {
        BreathabilityInfo itemInfo = itemBreathabilityMap.get(getItemKey(player, HEAD));
        if (itemInfo != null && itemInfo.toxic) {
            // if sealed, no need for toxicity check
            if (itemInfo.isSealed) {
                if (drainOxy(player)) return;
                else if (ConfigHolder.enableDimSuffocation) suffocate(player);
            } else if (dimRating > itemInfo.toxicityRating) {
                toxificate(player, dimRating - itemInfo.toxicityRating);
                return;
            }
        }
        toxificate(player, 100);
    }

    private static void toxificate(EntityPlayer player, int mult) {
        player.attackEntityFrom(GTCEFuCDamageSources.getToxicAtmoDamage(), 0.03f * mult);
    }

    private static void radiationCheck(EntityPlayer player, int dimRating) {
        // natural radiation protection based on missing health
        int ratingSum = MathHelper.ceil(Math.log1p(player.getMaxHealth() - player.getHealth()) * 6);

        BreathabilityInfo itemInfo = itemBreathabilityMap.get(getItemKey(player, HEAD));
        if (itemInfo != null && itemInfo.radiation) ratingSum += itemInfo.radiationRating;
        itemInfo = itemBreathabilityMap.get(getItemKey(player, CHEST));
        if (itemInfo != null && itemInfo.radiation) ratingSum += itemInfo.radiationRating;
        itemInfo = itemBreathabilityMap.get(getItemKey(player, LEGS));
        if (itemInfo != null && itemInfo.radiation) ratingSum += itemInfo.radiationRating;
        itemInfo = itemBreathabilityMap.get(getItemKey(player, FEET));
        if (itemInfo != null && itemInfo.radiation) ratingSum += itemInfo.radiationRating;

        if (dimRating > ratingSum) radiate(player, dimRating - ratingSum);
    }

    private static void radiate(EntityPlayer player, int mult) {
        float damage = 0.01f * mult;
        if (Math.random() < damage) {
            player.attackEntityFrom(DamageSources.getRadioactiveDamage(), Math.max(1, damage));
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

    public void addBreathabilityItem(ItemStack item, BreathabilityInfo info) {
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

    public static final class BreathabilityInfo {

        public final boolean suffocation;
        public final boolean toxic;
        public final boolean radiation;

        private final int toxicityRating;
        private final int radiationRating;

        public final boolean isSealed;

        public BreathabilityInfo(boolean suffocation, boolean isSealed, int toxic, int radiation) {
            this.suffocation = suffocation;
            this.toxic = toxic != -1;
            this.radiation = radiation != -1;
            this.radiationRating = radiation;
            this.toxicityRating = toxic;
            this.isSealed = isSealed;
        }
    }
}
