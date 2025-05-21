package kasuga.lib.core.events.client;

import kasuga.lib.core.client.block_bench_model.BlockBenchModelLoader;
import kasuga.lib.core.client.model.AnimModelLoader;
import kasuga.lib.core.client.model.BedrockModelLoader;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GeometryEvent {

    @SubscribeEvent
    public static void registerGeometry(ModelEvent.RegisterGeometryLoaders event) {
        event.register("bedrock_model", BedrockModelLoader.INSTANCE);
        event.register("bedrock_animated", AnimModelLoader.INSTANCE);
        event.register("blockbench_model", BlockBenchModelLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(BedrockModelLoader.INSTANCE);
        event.registerReloadListener(AnimModelLoader.INSTANCE);
        event.registerReloadListener(BlockBenchModelLoader.INSTANCE);
    }
}
