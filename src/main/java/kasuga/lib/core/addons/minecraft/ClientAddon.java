package kasuga.lib.core.addons.minecraft;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.resource.ResourceManagerPackageProvider;
import net.minecraft.client.Minecraft;

public class ClientAddon {
    public static ResourceManagerPackageProvider provider = null;

    public static void init(){
       load();
    }
    public static void load(){
        provider = new ResourceManagerPackageProvider(
                Minecraft.getInstance().getResourceManager()
        );

        provider.register(KasugaLib.STACKS.JAVASCRIPT.CLIENT_LOADER);
    }

    public static void unload(){
        if(provider == null)
            return;
        provider.unregister(KasugaLib.STACKS.JAVASCRIPT.CLIENT_LOADER);
    }

    public static void reload(){
        unload();
        load();
    }
}
