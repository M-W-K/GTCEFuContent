package com.m_w_k.gtcefucontent;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.common.CommonProxy;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.MetaTileEntityFusionStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = GTCEFuContent.MODID, name = GTCEFuContent.NAME, version = GTCEFuContent.VERSION, dependencies = GTCEFuContent.DEP_VERSION_STRING)
public class GTCEFuContent
{
    public static final String MODID = "gtcefucontent";
    public static final String NAME = "GregTechCEFuContent";
    public static final String VERSION = "1.0";

    public static final String DEP_VERSION_STRING =
            "required-after:gregtech@[2.7.0-beta,);required-after:gcym@[1.2.5,);required-after:enderio@[5.3,);";

    @SidedProxy(modId = MODID,
            clientSide = "com.m_w_k.gtcefucontent.common.ClientProxy",
            serverSide = "com.m_w_k.gtcefucontent.common.CommonProxy")
    public static CommonProxy proxy;


    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        log("Beginning PreInit");
        MetaTileEntityFusionStack.init();
        GTCEFuCRecipeMaps.init();

        GTCEFuCMetaBlocks.init();
        GTCEFuCMetaTileEntities.init();

        GTCEFuContentSoundEvents.register();
        proxy.preLoad();
        log("PreInit complete");
    }

    public static void log(Object message, LogType logType) {
        switch (logType) {
            case INFO -> logger.info(message);
            case WARN -> logger.warn(message);
            case ERROR -> logger.error(message);
        }
    }
    public enum LogType {
        INFO, WARN, ERROR
    }

    public static void log(Object message) {
        log(message, LogType.INFO);
    }
}
