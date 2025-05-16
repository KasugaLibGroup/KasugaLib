package kasuga.lib.core.client.block_bench_model.anim.instance;

import com.mojang.math.Vector3f;
import interpreter.compute.data.Namespace;
import kasuga.lib.core.client.block_bench_model.anim.Animator;
import kasuga.lib.core.client.block_bench_model.anim.Channel;
import kasuga.lib.core.client.block_bench_model.anim.KeyFrame;
import kasuga.lib.core.client.block_bench_model.anim.interpolation.Interpolation;
import kasuga.lib.core.client.block_bench_model.anim.interpolation.InterpolationType;
import kasuga.lib.core.client.block_bench_model.anim_model.ModelTransform;
import kasuga.lib.core.util.data_type.Pair;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Getter
public class AnimatorInstance {

    private final UUID id;
    private final AnimationInstance animation;
    private final HashMap<Channel, List<Pair<Float, Supplier<Vector3f>>>> frameSupplier;
    private final HashMap<Channel, List<Pair<Float, Vector3f>>> currentStaticFrame;
    private final Animator animator;
    private ModelTransform currentTransform;

    public AnimatorInstance(AnimationInstance animation, Animator animator) {
        frameSupplier = new HashMap<>();
        this.currentStaticFrame = new HashMap<>();
        id = animator.getId();
        this.animation = animation;
        this.animator = animator;
        currentTransform = new ModelTransform();
        compileSupplier();
    }

    public void compileSupplier() {
        for (Map.Entry<Channel, ArrayList<KeyFrame>> entry :
                animator.getKeyFrames().entrySet()) {
            frameSupplier.put(entry.getKey(), compileFrames(entry.getValue()));
        }
    }

    public Namespace getNamespace() {
        return animation.getNamespace();
    }

    public float getLength() {
        return animation.getAnimation().getLength();
    }

    public int getSnapping() {
        return animation.getAnimation().getSnapping();
    }

    public void compileStaticFrames() {
        currentStaticFrame.clear();
        for (Map.Entry<Channel, List<Pair<Float, Supplier<Vector3f>>>> entry : frameSupplier.entrySet()) {
            ArrayList<Pair<Float, Vector3f>> timeLine = new ArrayList<>();
            currentStaticFrame.put(entry.getKey(), timeLine);
            for (Pair<Float, Supplier<Vector3f>> pair : entry.getValue()) {
                getNamespace().assign("time", pair.getFirst());
                timeLine.add(Pair.of(pair.getFirst(), pair.getSecond().get()));
            }
        }
    }

    public List<Pair<Float, Supplier<Vector3f>>> compileFrames(List<KeyFrame> keyFrames) {
        if (keyFrames.isEmpty() ||
                getLength() <= 0) return new ArrayList<>();
        int size = (int) Math.ceil(getLength() * (float) getSnapping());
        float step = getLength() / (float) size;
        List<Pair<Float, Supplier<Vector3f>>> result = new ArrayList<>(size);
        KeyFrame cachePreFrame = keyFrames.get(0),
                cacheNextFrame = keyFrames.size() > 1 ? keyFrames.get(1) : null;
        int cacheKeyFrameIndex = 1;
        float time;
        for (float i = 0; i < size; i++) {
            time = i * step;
            getNamespace().assign("time", time);
            if (cacheNextFrame == null) {
                result.add(
                        Pair.of(time, cachePreFrame.getPostDataPointSup())
                );
                continue;
            } else if (time > cacheNextFrame.getTime()) {
                cachePreFrame = cacheNextFrame;
                result.add(
                        Pair.of(cachePreFrame.getTime(), cachePreFrame.getPreDataPointSup())
                );
                cacheKeyFrameIndex++;
                cacheNextFrame = keyFrames.size() > cacheKeyFrameIndex ?
                        keyFrames.get(cacheKeyFrameIndex) : null;
                if (cacheNextFrame == null) {
                    result.add(
                            Pair.of(time, cachePreFrame.getPostDataPointSup())
                    );
                    continue;
                }
            }
            InterpolationType interpolationType = InterpolationType.getMinPriority(
                    cachePreFrame.getInterpolation(),
                    cacheNextFrame.getInterpolation()
            );
            Interpolation interpolation = interpolationType.getInterpolation();
            final KeyFrame pre = cachePreFrame;
            final KeyFrame next = cacheNextFrame;
            final float t = time;
            result.add(
                    Pair.of(time, () -> interpolation.interpolate(animator, pre, next, t))
            );
        }
        return result;
    }

    public Vector3f getValue(float time, Channel channel) {
        if (!currentStaticFrame.containsKey(channel)) {
            return new Vector3f();
        }
        List<Pair<Float, Vector3f>> timeLine = currentStaticFrame.get(channel);
        float step = getLength() / (float) timeLine.size();
        int index = Math.round(time / step);
        if (index >= timeLine.size()) index = timeLine.size() - 1;
        Pair<Float, Vector3f> currentPair = timeLine.get(index);
        Pair<Float, Vector3f> nextPair = index + 1 < timeLine.size() ?
                timeLine.get(index + 1) : null;
        if (nextPair == null) {
            return currentPair.getSecond();
        }
        while(!(currentPair.getFirst() <= time && nextPair.getFirst() >= time)) {
            if (nextPair.getFirst() < time) {
                index++;
                currentPair = nextPair;
                nextPair = index + 1 < timeLine.size() ?
                        timeLine.get(index + 1) : null;
                if (nextPair == null) return currentPair.getSecond();
            } else {
                index--;
                nextPair = currentPair;
                currentPair = index >= 0 ? timeLine.get(index) : null;
                if (currentPair == null) return new Vector3f();
            }
        }
        Vector3f current = currentPair.getSecond();
        Vector3f next = nextPair.getSecond();
        Vector3f result = new Vector3f();
        result.add(next);
        result.sub(current);
        float percentage = (time - currentPair.getFirst()) /
                (nextPair.getFirst() - currentPair.getFirst());
        result.mul(percentage);
        result.add(current);
        return result;
    }

    public void updateTransform(int tick, float partialTick) {
        updateTransform(AnimationInstance.tickToSecond(tick, partialTick));
    }

    public void updateTransform(float time) {
        for (Channel channel : currentStaticFrame.keySet()) {
            this.currentTransform.setValue(channel, getValue(time, channel));
        }
    }
}
