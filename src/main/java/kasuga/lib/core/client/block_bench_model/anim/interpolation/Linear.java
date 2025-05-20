package kasuga.lib.core.client.block_bench_model.anim.interpolation;

import kasuga.lib.core.client.block_bench_model.anim.Animator;
import kasuga.lib.core.client.block_bench_model.anim.KeyFrame;
import org.joml.Vector3f;

public class Linear extends Interpolation {

    public Linear() {
        super("linear");
    }

    @Override
    public Vector3f interpolate(Animator animator, KeyFrame pre, KeyFrame next, float time) {
        Vector3f result = stepOnKeyFrame(pre, next, time);
        if (result != null) return result;

        // linear interpolation
        float percentage = getPercentage(pre, next, time);
        return linearInterpolate(
                pre.getPostDataPoint(),
                next.getPreDataPoint(),
                percentage
        );
    }

    public static Vector3f linearInterpolate(Vector3f pre, Vector3f next, float percentage) {
        Vector3f result = new Vector3f();
        result.add(next);
        result.sub(pre);
        result.mul(percentage);
        result.add(pre);
        return result;
    }
}
