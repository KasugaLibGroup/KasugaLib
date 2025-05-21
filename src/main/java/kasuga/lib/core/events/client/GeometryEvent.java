package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.block_bench_model.BlockBenchModelLoader;
import kasuga.lib.core.client.model.AnimModelLoader;
import kasuga.lib.core.client.model.BedrockModelLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GeometryEvent {

    @SubscribeEvent
    public static void registerGeometry(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(KasugaLib.MOD_ID, "bedrock_model"), BedrockModelLoader.INSTANCE);
        ModelLoaderRegistry.registerLoader(new ResourceLocation(KasugaLib.MOD_ID, "bedrock_animated"), AnimModelLoader.INSTANCE);
        ModelLoaderRegistry.registerLoader(new ResourceLocation(KasugaLib.MOD_ID, "blockbench_model"), BlockBenchModelLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(BedrockModelLoader.INSTANCE);
        event.registerReloadListener(AnimModelLoader.INSTANCE);
        event.registerReloadListener(BlockBenchModelLoader.INSTANCE);
    }
}
