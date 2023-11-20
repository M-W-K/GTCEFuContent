package com.m_w_k.gtcefucontent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.common.CommonProxy;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.item.GTCEFuCMetaItems;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.MetaTileEntityAntimatterCompressor;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.MetaTileEntityFusionStack;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuCMiscRecipes;

@Mod(modid = GTCEFuContent.MODID,
     name = GTCEFuContent.NAME,
     version = GTCEFuContent.VERSION,
     dependencies = GTCEFuContent.DEP_VERSION_STRING)
public final class GTCEFuContent {

    public static final String MODID = "gtcefucontent";
    public static final String NAME = "GregTechCEFuContent";
    public static final String VERSION = "1.3.0";

    public static final String DEP_VERSION_STRING = "required-after:gregtech@[2.7.4-beta,);" +
            "required-after:gcym@[1.2.5,);" +
            "required-after:projecte@[1.4.1,);" +
            "required-after:projectex@[1.2.0,);";

    @SidedProxy(modId = MODID,
                clientSide = "com.m_w_k.gtcefucontent.common.ClientProxy",
                serverSide = "com.m_w_k.gtcefucontent.common.CommonProxy")
    public static CommonProxy proxy;

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        log("Beginning PreInit");
        GTCEFuContentSoundEvents.register();

        GTCEFuCRecipeMaps.init();

        GTCEFuCMetaBlocks.init();
        GTCEFuCMetaItems.init();
        GTCEFuCMetaTileEntities.init();

        MetaTileEntityFusionStack.init();
        MetaTileEntityAntimatterCompressor.init();

        proxy.preLoad();
        log("PreInit complete");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        log("Beginning PostInit");
        GTCEFuCMiscRecipes.initPost();
        log("PostInit complete");
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
