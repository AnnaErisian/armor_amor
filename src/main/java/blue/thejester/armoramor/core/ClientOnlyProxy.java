package blue.thejester.armoramor.core;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = blue.thejester.armoramor.ArmorAmor.MODID)
public class ClientOnlyProxy extends CommonProxy {

    @Override
    public boolean isDedicatedServer() {
        return false;
    }

}
