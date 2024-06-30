package kasuga.lib.core.events.server;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.resource.adapter.Adapter;
import kasuga.lib.core.addons.resource.types.PackResources;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ServerResourceListener {
    public static void onServerStarting(ServerStartingEvent event){
        List<PackResources> pack =
                Adapter.adapt(
                        event.getServer().getResourceManager().listPacks().collect(Collectors.toList())
                );

        KasugaLib.STACKS.SERVER_SCRIPT_PACK_LOADER.add(pack);
    }

    public static void onServerStopping(ServerStoppingEvent event){
        KasugaLib.STACKS.SERVER_SCRIPT_PACK_LOADER.clear();
    }
}
