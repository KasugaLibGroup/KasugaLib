package kasuga.lib.core.events.client;

import kasuga.lib.core.model.BedrockModelLoader;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GeometryEvent {

    @SubscribeEvent
    public static void registerGeometry(ModelEvent.RegisterGeometryLoaders event) {
        event.register("bedrock_model", BedrockModelLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(BedrockModelLoader.INSTANCE);
    }
}
