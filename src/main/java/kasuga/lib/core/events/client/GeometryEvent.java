package kasuga.lib.core.events.client;

import kasuga.lib.core.model.AnimModelLoader;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GeometryEvent {

    @SubscribeEvent
    public static void registerGeometry(ModelEvent.RegisterGeometryLoaders event) {
        event.register("be_anim", AnimModelLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(AnimModelLoader.INSTANCE);
    }
}
