package kasuga.lib.core.addons.minecraft;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.resource.ResourceManagerPackageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public class ServerAddon {
    public static ResourceManagerPackageProvider provider = null;
    public static void init(MinecraftServer server){
        load(server);
    }

    public static void load(MinecraftServer server){
        provider = new ResourceManagerPackageProvider(
                server.getResourceManager()
        );

        provider.register(KasugaLib.STACKS.JAVASCRIPT.SERVER_LOADER);
    }

    public static void unload(){
        if(provider == null)
            return;
        provider.unregister(KasugaLib.STACKS.JAVASCRIPT.SERVER_LOADER);
    }
}
