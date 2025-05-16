package kasuga.lib.core.client.block_bench_model.anim.interpolation;

import com.mojang.math.Vector3f;
import kasuga.lib.core.client.block_bench_model.anim.Animator;
import kasuga.lib.core.client.block_bench_model.anim.KeyFrame;

public class Step extends Interpolation {

    public Step() {
        super("step");
    }

    @Override
    public Vector3f interpolate(Animator animator, KeyFrame pre, KeyFrame next, float time) {
        Vector3f result = stepOnKeyFrame(pre, next, time);
        if (result != null) return result;
        return pre.getPostDataPointSup().get();
    }
}
