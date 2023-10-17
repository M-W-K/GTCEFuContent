package com.m_w_k.gtcefucontent;

import com.m_w_k.gtcefucontent.common.CommonProxy;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;
import com.m_w_k.gtcefucontent.loaders.recipe.GTCEFuMiscRecipes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = GTCEFuContent.MODID, name = GTCEFuContent.NAME, version = GTCEFuContent.VERSION)
public class GTCEFuContent
{
    public static final String MODID = "gtcefucontent";
    public static final String NAME = "GTCEFuContent";
    public static final String VERSION = "1.0";

//    @SidedProxy(modId = MODID,
//            clientSide = "com.m_w_k.gtcefucontent.common.ClientProxy",
//            serverSide = "com.m_w_k.gtcefucontent.common.CommonProxy")
//    public static CommonProxy proxy;


    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        GTCEFuCMetaTileEntities.init();

//        proxy.preLoad();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        // Trigger the adjustment of Cutter recipes to also use cloud seed.
        // As this requires all normal cutter recipes to be established, it is called during the postInit phase

        // We can't change the behavior of the recipeMap setup itself without being overly invasive.
        GTCEFuMiscRecipes.cutterUpdate();
    }

    public static void log(Object message, int type) {
        switch (type) {
            case 1 -> logger.info(message);
            case 2 -> logger.warn(message);
            case 3 -> logger.error(message);
        }
    }
    public static void log(Object message) {
        logger.info(message);
    }
}
