package kasuga.lib.core.create.graph;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class EdgeExtraPayloadRegistry {
    protected static Map<ResourceLocation, EdgeExtraPayloadType<?>> registry = new HashMap<>();
    protected static Map<EdgeExtraPayloadType<?>, ResourceLocation> reverseRegistry = new HashMap<>();

    public static <T extends EdgeExtraPayload> void register(ResourceLocation id, EdgeExtraPayloadType<T> type) {
        registry.put(id, type);
        reverseRegistry.put(type, id);
    }

    public static <T extends EdgeExtraPayload> EdgeExtraPayloadType<T> get(ResourceLocation id) {
        return (EdgeExtraPayloadType<T>) registry.get(id);
    }

    public static ResourceLocation getId(EdgeExtraPayloadType<?> type) {
        return reverseRegistry.get(type);
    }
}
