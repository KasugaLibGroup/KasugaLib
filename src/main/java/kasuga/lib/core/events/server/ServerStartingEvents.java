package kasuga.lib.core.events.server;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.minecraft.ServerAddon;
import kasuga.lib.core.util.Start;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerStartingEvents {

    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent event) {}

    @SubscribeEvent
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        if(server instanceof DedicatedServer) {
            Start.printLogo();
        }
    }
}
