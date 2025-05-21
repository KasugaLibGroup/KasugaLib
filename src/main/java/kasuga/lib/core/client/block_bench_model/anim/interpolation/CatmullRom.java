package kasuga.lib.core.client.block_bench_model.anim.interpolation;

import kasuga.lib.core.client.block_bench_model.anim.Animator;
import kasuga.lib.core.client.block_bench_model.anim.Channel;
import kasuga.lib.core.client.block_bench_model.anim.KeyFrame;
import kasuga.lib.core.client.model.anim_json.CatmullRomUtils;
import org.joml.Vector3f;

import java.util.List;

public class CatmullRom extends Interpolation {
    public CatmullRom() {
        super("catmullrom");
    }

    @Override
    public Vector3f interpolate(Animator animator, KeyFrame pre, KeyFrame next, float time) {
        Vector3f result = stepOnKeyFrame(pre, next, time);
        if (result != null) return result;
        float percentage = getPercentage(pre, next, time);
        Channel channel = pre.getChannel();
        List<KeyFrame> timeLine = animator.getKeyFrames().get(channel);
        int preIndex = timeLine.indexOf(pre);
        int nextIndex = timeLine.indexOf(next);
        boolean isPreUninterrupted = pre.isUninterrupted();
        boolean isNextUninterrupted = next.isUninterrupted();
        if (preIndex == -1 || nextIndex == -1) {
            return pre.getPostDataPoint();
        }
        if (timeLine.size() == 2 || (!isPreUninterrupted && !isNextUninterrupted)) {
            return Linear.linearInterpolate(pre.getPostDataPoint(),
                    next.getPreDataPoint(), percentage);
        }
        Vector3f[] points;
        if (preIndex == 0 || !isPreUninterrupted) {
            KeyFrame nextNext = timeLine.get(nextIndex + 1);
            points = CatmullRomUtils.last3PointsToCRSPoints(
                    pre.getPostDataPoint(),
                    next.getPreDataPoint(),
                    nextNext.getPreDataPoint()
            );
            result = CatmullRomUtils.applyCRS(points, percentage);
            return result;
        }
        if (nextIndex == timeLine.size() - 1 || !isNextUninterrupted) {
            KeyFrame last = timeLine.get(preIndex - 1);
            points = CatmullRomUtils.first3PointsToCRSPoints(
                    last.getPostDataPoint(),
                    pre.getPreDataPoint(),
                    next.getPreDataPoint()
            );
            result = CatmullRomUtils.applyCRS(points, percentage);
            return result;
        }
        KeyFrame nextNext = timeLine.get(nextIndex + 1);
        KeyFrame last = timeLine.get(preIndex - 1);
        points = CatmullRomUtils.genDefaultCRSPoints(
                last.getPostDataPoint(),
                pre.getPreDataPoint(),
                next.getPreDataPoint(),
                nextNext.getPreDataPoint()
                );
        result = CatmullRomUtils.applyCRS(points, percentage);
        return result;
    }
}
