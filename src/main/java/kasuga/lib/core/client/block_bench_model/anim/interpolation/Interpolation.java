package kasuga.lib.core.client.block_bench_model.anim.interpolation;

import com.mojang.math.Vector3f;
import kasuga.lib.core.client.block_bench_model.anim.Animator;
import kasuga.lib.core.client.block_bench_model.anim.KeyFrame;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public abstract class Interpolation {

    private final String name;

    public Interpolation(String name) {
        this.name = name;
    }

    public abstract Vector3f interpolate(Animator animator, KeyFrame pre, KeyFrame next, float time);

    public float getPercentage(KeyFrame pre, KeyFrame next, float time) {
        float len = next.getTime() - pre.getTime();
        float u = time - pre.getTime();
        return u / len;
    }

    public @Nullable Vector3f stepOnKeyFrame(KeyFrame pre, KeyFrame next, float time) {
        float percentage = getPercentage(pre, next, time);
        if (percentage > 0.999f) {
            return next.getPostDataPointSup().get();
        } else if (percentage < 0.001f) {
            return pre.getPreDataPointSup().get();
        }
        return null;
    }
}
