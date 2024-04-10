package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.KasugaLibStacks;
import kasuga.lib.core.util.Start;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class ClientSetupEvent {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        Start.printLogo(KasugaLib.MAIN_LOGGER);
        for (SimpleRegistry registry : KasugaLib.STACKS.getRegistries().values()) {
            registry.getCahcedMenus().forEach((a, b) -> b.hookMenuAndScreen());
        }
    }
}
