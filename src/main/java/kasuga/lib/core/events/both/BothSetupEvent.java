package kasuga.lib.core.events.both;

import kasuga.lib.KasugaLib;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class BothSetupEvent {
    public static void onFMLCommonSetup(FMLCommonSetupEvent event){

    }

    @SubscribeEvent
    public static void RegisterKeyEvent(RegisterKeyMappingsEvent event) {
        KasugaLib.STACKS.getRegistries().forEach(
                (name, registry) -> {
                    registry.key().forEach(
                            ((s, keyBindingReg) -> event.register(keyBindingReg.getMapping()))
                    );
                }
        );
    }
}
