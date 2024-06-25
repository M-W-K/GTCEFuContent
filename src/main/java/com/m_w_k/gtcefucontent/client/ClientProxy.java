package com.m_w_k.gtcefucontent.client;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.fluids.void_starlight.TileEntityVoidStarlight;
import com.m_w_k.gtcefucontent.api.render.GTCEFuCTextures;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;
import com.m_w_k.gtcefucontent.client.renderer.VoidStarlightRenderer;
import com.m_w_k.gtcefucontent.common.CommonProxy;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preLoad() {
        super.preLoad();
        GTCEFuCTextures.preInit();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVoidStarlight.class, new VoidStarlightRenderer());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        GTCEFuCMetaBlocks.registerItemModels();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void overrideUniversalBucketTemperatureTooltip(@NotNull ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.hasTagCompound()) { // Test for forge-esque fluid containers
            // noinspection ConstantConditions
            if (itemStack.getTagCompound().hasKey("FluidName")) {
                FluidStack stack = FluidStack.loadFluidStackFromNBT(itemStack.getTagCompound());
                if (stack != null) {
                    GTCEFuCUtil.fluidStackTooltipOverride(stack, event.getToolTip());
                }
            }
        }
    }
}
