package kasuga.lib.core.events.both;

import kasuga.lib.KasugaLib;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class BothSetupEvent {
    public static void onFMLCommonSetup(FMLCommonSetupEvent event){

    }
    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event){
        KasugaLib.STACKS.getRegistries().forEach(
                (name, registry) -> {
                    registry.key().forEach(
                            (s, keyBindingReg) -> ClientRegistry.registerKeyBinding(keyBindingReg.getMapping())
                    );
                }
        );
    }
}
