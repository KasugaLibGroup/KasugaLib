package kasuga.lib.core.events.server;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.minecraft.ServerAddon;
import kasuga.lib.core.util.Start;
import kasuga.lib.core.webserver.KasugaHttpServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.event.server.*;
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
        KasugaLib.server = server;
        KasugaHttpServer.onServerStart(event.getServer());
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent event) {
        KasugaLib.server = null;
        KasugaHttpServer.onServerStop();
    }
}
