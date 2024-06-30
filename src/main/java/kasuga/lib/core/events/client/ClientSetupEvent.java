package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.resource.adapter.Adapter;
import kasuga.lib.core.addons.resource.types.PackResources;
import kasuga.lib.core.util.Start;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientSetupEvent {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        Start.printLogo(KasugaLib.MAIN_LOGGER);
        for (SimpleRegistry registry : KasugaLib.STACKS.getRegistries().values()) {
            registry.getCahcedMenus().forEach((a, b) -> b.hookMenuAndScreen());
            registry.getCahcedMenus().clear();
        }

        List<PackResources> pack =
                Adapter.adapt(
                        Minecraft.getInstance().getResourceManager().listPacks().collect(Collectors.toList())
                );

        KasugaLib.STACKS.CLIENT_SCRIPT_PACK_LOADER.add(pack);
    }
}
