package com.m_w_k.gtcefucontent;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.Logger;

import com.m_w_k.gtcefucontent.api.fluids.void_starlight.TileEntityVoidStarlight;
import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.common.CommonProxy;
import com.m_w_k.gtcefucontent.common.DimensionBreathabilityHandler;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.MetaTileEntityAntimatterCompressor;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.MetaTileEntityFusionStack;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCCraftingRecipeLoader;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCMiscRecipes;

@Mod(modid = GTCEFuContent.MODID,
     name = GTCEFuContent.NAME,
     version = GTCEFuContent.VERSION,
     dependencies = GTCEFuContent.DEP_VERSION_STRING)
public final class GTCEFuContent {

    public static final String MODID = Tags.MODID;
    public static final String NAME = Tags.MODNAME;
    public static final String VERSION = Tags.VERSION;

    public static final String DEP_VERSION_STRING = "required-after:gregtech@[2.8.10-beta,);" +
            "required-after:gcym@[1.2.10,);" +
            "after:projecte@[1.4.1,);" +
            "after:projectex@[1.2.0,);";

    @SidedProxy(modId = MODID,
                clientSide = "com.m_w_k.gtcefucontent.client.ClientProxy",
                serverSide = "com.m_w_k.gtcefucontent.common.CommonProxy")
    public static CommonProxy proxy;

    private static Logger logger;

    public static boolean isProjectEXLoaded;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        log("Beginning PreInit");

        isProjectEXLoaded = Loader.isModLoaded("projectex") && Loader.isModLoaded("projecte");
        if (isProjectEXLoaded) log("Project E & EX found. Integration and dependent endgame content enabled.");
        else log("Project E & EX not found. Integration and dependent endgame content disabled.", LogType.WARN);

        GTCEFuContentSoundEvents.register();

        GTCEFuCRecipeMaps.init();

        GTCEFuCMetaBlocks.init();
        GTCEFuCMetaItems.init();
        GTCEFuCMetaTileEntities.init();

        MetaTileEntityFusionStack.init();
        MetaTileEntityAntimatterCompressor.init();

        GameRegistry.registerTileEntity(TileEntityVoidStarlight.class,
                new ResourceLocation(Tags.MODID, "void_starlight"));
        proxy.preLoad();
        log("PreInit complete");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        log("Beginning PostInit");
        GTCEFuContent.log("Removing recipes, DON'T BE SCARED OF FML's WARNING ABOUT DANGEROUS ALTERNATIVE PREFIX");
        GTCEFuCMiscRecipes.initPost();
        GTCEFuCCraftingRecipeLoader.initPost();
        log("PostInit complete");
    }

    @EventHandler
    public static void serverStart(FMLServerStartingEvent event) {
        log("Loading breathability config.");
        DimensionBreathabilityHandler.loadConfig();
    }

    public static void log(Object message, LogType logType) {
        switch (logType) {
            case INFO -> logger.info(message);
            case WARN -> logger.warn(message);
            case ERROR -> logger.error(message);
        }
    }

    public enum LogType {
        INFO,
        WARN,
        ERROR
    }

    public static void log(Object message) {
        log(message, LogType.INFO);
    }
}
