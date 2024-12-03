package kasuga.lib.core.events.server;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.minecraft.ServerAddon;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ServerResourceListener {
    public static void onServerStarting(ServerStartingEvent event){
        KasugaLib.STACKS.JAVASCRIPT.setupServer();
        KasugaLib.STACKS.MENU.initRegistry();
        ServerAddon.load(event.getServer());
    }

    public static void onServerStopping(ServerStoppingEvent event){
        ServerAddon.unload();
        KasugaLib.STACKS.JAVASCRIPT.destoryServer();
    }
}
