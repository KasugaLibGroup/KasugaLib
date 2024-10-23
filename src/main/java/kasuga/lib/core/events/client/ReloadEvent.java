package kasuga.lib.core.events.client;

import kasuga.lib.core.addons.minecraft.ClientReloadManager;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

public class ReloadEvent {
    protected static ClientReloadManager reloadManager = new ClientReloadManager();
    public static void onReload(RegisterClientReloadListenersEvent event){
        event.registerReloadListener(reloadManager);
    }
}
