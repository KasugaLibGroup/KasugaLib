package kasuga.lib.core.events.both;

import kasuga.lib.core.resource.CustomResourceReloadListener;
import net.minecraft.server.commands.DataPackCommand;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ResourcePackEvent {

    @SubscribeEvent
    public static void onResourcePackReload(AddReloadListenerEvent reloadListenerEvent){
        reloadListenerEvent.addListener(CustomResourceReloadListener.INSTANCE);
    }

    @SubscribeEvent
    public static void onClientResourcePackReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(CustomResourceReloadListener.INSTANCE);
    }
}
