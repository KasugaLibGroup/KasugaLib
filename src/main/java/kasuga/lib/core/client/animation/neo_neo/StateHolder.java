package kasuga.lib.core.client.animation.neo_neo;

import kasuga.lib.core.client.animation.neo_neo.base.Movement;
import kasuga.lib.core.client.animation.neo_neo.key_frame.KeyFrame;
import kasuga.lib.core.client.animation.neo_neo.key_frame.KeyFrameHolder;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StateHolder <T extends Movement> {
    private String name;
    private final HashMap<String, HashMap<Float, KeyFrame<? extends T>>> holder;
    private final KeyFrameHolder<T> keyFrameBase;

    public StateHolder(KeyFrameHolder<T> base, String name) {
        this.name = name;
        this.holder = new HashMap<>();
        this.keyFrameBase = base;
    }

    public HashMap<String, HashMap<Float, KeyFrame<? extends T>>> getHolder() {
        return holder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addBone(String boneKey) {
        if (!this.holder.containsKey(boneKey))
            this.holder.put(boneKey, new HashMap<>());
    }

    public HashMap<Float, KeyFrame<? extends T>> getKeyFrames(String bone) {
        return holder.getOrDefault(bone, new HashMap<>());
    }

    public boolean containsBone(String bone) {
        return this.holder.containsKey(bone);
    }

    public boolean removeBone(String bone) {
        if (!holder.containsKey(bone)) return false;
        holder.remove(bone);
        return true;
    }

    public void addKeyFrame(String bone, Float time, KeyFrame<? extends T> frame) {
        if (containsBone(bone)) addBone(bone);
        holder.get(bone).put(time, frame);
    }

    public void addKeyFrame(String bone, Float time, ResourceLocation location) {
        if (!keyFrameBase.contains(location)) return;
        addKeyFrame(bone, time, keyFrameBase.get(location));
    }

    public int getFrameCount(String bone) {
        if (!containsBone(bone)) return 0;
        return getKeyFrames(bone).size();
    }

    public int boneCount() {
        return holder.size();
    }

    public boolean isEmpty() {
        return holder.isEmpty();
    }

    public void cutEmptyLeaves() {
        LinkedList<String> bone = new LinkedList<>();
        holder.forEach((a, b) -> {if(b.isEmpty()) bone.add(a);});
        bone.forEach(holder::remove);
    }

    public List<Pair<Float, KeyFrame<? extends T>>> getFramesByTime(String bone) {
        if (!holder.containsKey(bone)) return List.of();
        HashMap<Float, KeyFrame<? extends T>> frames = holder.get(bone);
        ArrayList<Pair<Float, KeyFrame<? extends T>>> result = new ArrayList<>(frames.size());
        frames.forEach((a, b) -> result.add(Pair.of(a, b)));
        boolean shouldSort = true;
        while (shouldSort) {
            shouldSort = false;
            for (int i = 1; i < result.size(); i++) {
                Pair<Float, KeyFrame<? extends T>> cache = result.get(i);
                if (cache.getFirst() < result.get(i - 1).getFirst()) {
                    result.set(i, result.get(i - 1));
                    result.set(i - 1, cache);
                    shouldSort = true;
                }
            }
        }
        return result;
    }
}
