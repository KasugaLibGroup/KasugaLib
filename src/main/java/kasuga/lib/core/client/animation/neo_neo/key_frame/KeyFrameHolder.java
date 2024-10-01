package kasuga.lib.core.client.animation.neo_neo.key_frame;

import kasuga.lib.core.client.animation.neo_neo.base.Movement;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Set;

public class KeyFrameHolder<T extends Movement> {
    private final HashMap<ResourceLocation, KeyFrame<? extends T>> holder;

    public KeyFrameHolder() {this.holder = new HashMap<>();}

    public KeyFrameHolder(Pair<ResourceLocation, KeyFrame<? extends T>>... frames) {
        this();
        for (Pair<ResourceLocation, KeyFrame<? extends T>> p : frames) {
            holder.put(p.getFirst(), p.getSecond());
        }
    }

    public void add(ResourceLocation location, KeyFrame<? extends T> frame) {
        holder.put(location, frame);
    }

    public KeyFrame<? extends T> get(ResourceLocation location) {
        return holder.getOrDefault(location, null);
    }

    public KeyFrame<? extends T> createInstance(Float time, ResourceLocation location) {
        return createInstance(time, Vec3.ZERO, location);
    }

    public KeyFrame<? extends T> createInstance(Float time, Vec3 data, ResourceLocation location) {
        KeyFrame<? extends T> result = get(location).clone();
        result.setTime(time);
        result.setData(data);
        return result;
    }

    public KeyFrame<? extends T> copyFrom(KeyFrame<? extends T> org, Vec3 newData, float newTime) {
        KeyFrame<? extends T> neo = org.clone();
        neo.setData(newData);
        neo.setTime(newTime);
        return neo;
    }

    public Set<ResourceLocation> getKeys() {
        return holder.keySet();
    }

    public HashMap<ResourceLocation, KeyFrame<? extends T>> getHolder() {
        return holder;
    }

    public boolean contains(ResourceLocation resourceLocation) {
        return holder.containsKey(resourceLocation);
    }

    public boolean remove(ResourceLocation location) {
        if (!holder.containsKey(location)) return false;
        holder.remove(location);
        return true;
    }

    public int size() {
        return holder.size();
    }

    public boolean isEmpty() {
        return holder.isEmpty();
    }
}
