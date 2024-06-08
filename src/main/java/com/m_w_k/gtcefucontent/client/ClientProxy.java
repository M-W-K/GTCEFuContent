package com.m_w_k.gtcefucontent.client;


import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;
import com.m_w_k.gtcefucontent.common.CommonProxy;
import gregtech.api.util.FluidTooltipUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void overrideUniversalBucketTemperatureTooltip(@NotNull ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.hasTagCompound()) { // Test for forge-esque fluid containers
            // noinspection ConstantConditions
            if (itemStack.getTagCompound().hasKey("FluidName")) {
                FluidStack stack = FluidStack.loadFluidStackFromNBT(itemStack.getTagCompound());
                if (stack != null && stack.getFluid() instanceof EutecticFluid fluid) {
                    List<String> tooltip = event.getToolTip();
                    for (int i = 0; i < tooltip.size(); i++) {
                        if (I18n.format("gregtech.fluid.temperature", fluid.getTemperature()).equals(tooltip.get(i))) {
                            tooltip.set(i, I18n.format("gregtech.fluid.temperature", GTCEFuCUtil.getTemp(stack)));
                        }
                    }
                }
            }
        }
    }
}
