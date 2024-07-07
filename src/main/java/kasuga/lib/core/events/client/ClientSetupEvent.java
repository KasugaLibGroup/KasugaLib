package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.minecraft.ClientAddon;
import kasuga.lib.core.addons.node.PackageScanner;
import kasuga.lib.core.addons.resource.ResourceAdapter;
import kasuga.lib.core.addons.resource.ResourceProvider;
import kasuga.lib.core.client.frontend.commands.MetroModuleLoader;
import kasuga.lib.core.client.frontend.gui.GuiEngine;
import kasuga.lib.core.util.Start;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AddReloadListenerEvent;
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
        KasugaLib.STACKS.JAVASCRIPT.setupClient();
        KasugaLib.STACKS.GUI.ifPresent(GuiEngine::init);
        ClientAddon.init();
        MetroModuleLoader.init();
    }
}
