package kasuga.lib.core.events.server;

import kasuga.lib.KasugaLib;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ServerResourceListener {
    public static void onServerStarting(ServerStartingEvent event){
        // event.getServer().getResourceManager().listPacks().collect(Collectors.toList())
    }

    public static void onServerStopping(ServerStoppingEvent event){
    }
}
