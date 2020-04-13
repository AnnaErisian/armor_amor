package blue.thejester.armoramor;

import blue.thejester.armoramor.core.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;
import java.util.Set;

@Mod(modid = blue.thejester.armoramor.ArmorAmor.MODID, name = blue.thejester.armoramor.ArmorAmor.NAME, version = blue.thejester.armoramor.ArmorAmor.VERSION)
public class ArmorAmor
{
    public static final String MODID = "armoramor";
    public static final String NAME = "Botany Booster";
    public static final String VERSION = "1.0";

    public static Logger logger;

    public static Set<String> subtilesForCreativeMenu = new LinkedHashSet();

    // The instance of your mod that Forge uses.  Optional.
    @Mod.Instance(blue.thejester.armoramor.ArmorAmor.MODID)
    public static blue.thejester.armoramor.ArmorAmor instance;

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="blue.thejester.armoramor.core.ClientOnlyProxy", serverSide="blue.thejester.armoramor.core.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }
}
