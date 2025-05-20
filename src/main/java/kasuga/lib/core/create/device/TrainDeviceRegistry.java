package kasuga.lib.core.create.device;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class TrainDeviceRegistry {
    public static final HashMap<ResourceLocation, TrainDeviceSystemType<?>> REGISTRY = new HashMap<>();
    public static final HashMap<TrainDeviceSystemType<?>, ResourceLocation> REVERSE_REGISTRY = new HashMap<>();

    public static void register(ResourceLocation location, TrainDeviceSystemType<?> type) {
        if (REGISTRY.containsKey(location)) {
            throw new IllegalArgumentException("Duplicate registration for " + location);
        }
        if (REVERSE_REGISTRY.containsKey(type)) {
            throw new IllegalArgumentException("Duplicate registration for " + type);
        }
        REGISTRY.put(location, type);
        REVERSE_REGISTRY.put(type, location);
    }

    public static TrainDeviceSystemType<?> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static ResourceLocation getKey(TrainDeviceSystemType<?> type) {
        return REVERSE_REGISTRY.get(type);
    }
}
