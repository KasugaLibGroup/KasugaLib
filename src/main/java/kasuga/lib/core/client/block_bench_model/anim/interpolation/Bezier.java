package kasuga.lib.core.client.block_bench_model.anim.interpolation;

import kasuga.lib.core.client.block_bench_model.anim.Animator;
import kasuga.lib.core.client.block_bench_model.anim.KeyFrame;
import kasuga.lib.core.client.render.texture.Vec2f;
import kasuga.lib.core.util.data_type.Pair;
import org.joml.Vector3f;

import java.util.Arrays;

public class Bezier extends Interpolation {

    public Bezier() {
        super("bezier");
    }

    @Override
    public Vector3f interpolate(Animator animator, KeyFrame pre, KeyFrame next, float time) {
        Vector3f result = stepOnKeyFrame(pre, next, time);
        if (result != null) return result;
        float percentage = getPercentage(pre, next, time);
        Vec2f[][] data = get3DBezierCtrlPoints(animator, pre, next);
        result = get3DPercentagePoint(data, percentage);
        return result;
    }

    public Vector3f get3DPercentagePoint(Vec2f[][] data, float percentage) {
        Vector3f result;
        if (data.length > 3) {
            result = new Vector3f(
                    getBezierPointAt(percentage, data[0][0], data[3][0], data[1][0], data[2][0]).y(),
                    getBezierPointAt(percentage, data[0][1], data[3][1], data[1][1], data[2][1]).y(),
                    getBezierPointAt(percentage, data[0][2], data[3][2], data[1][2], data[2][2]).y()
            );
        } else if (data.length == 3) {
            result = new Vector3f(
                    getBezierPointAt(percentage, data[0][0], data[2][0], data[1][0]).y(),
                    getBezierPointAt(percentage, data[0][1], data[2][1], data[1][1]).y(),
                    getBezierPointAt(percentage, data[0][2], data[2][2], data[1][2]).y()
            );
        } else if (data.length == 2) {
            result = new Vector3f(
                    getBezierPointAt(percentage, data[0][0], data[1][0]).y(),
                    getBezierPointAt(percentage, data[0][1], data[1][1]).y(),
                    getBezierPointAt(percentage, data[0][2], data[1][2]).y()
            );
        } else {
            return new Vector3f();
        }
        return result;
    }

    public Vec2f[][] get3DBezierCtrlPoints(Animator animator, KeyFrame pre, KeyFrame next) {
        Pair<Vector3f, Vector3f> leftCtrlPoint = pre.hasBezierRight() ? pre.getBezierRightSup().get() : null;
        Pair<Vector3f, Vector3f> rightCtrlPoint = next.hasBezierLeft() ? next.getBezierLeftSup().get() : null;
        if (leftCtrlPoint != null) {
            Vector3f leftTime = leftCtrlPoint.getFirst();
            Vector3f leftData = leftCtrlPoint.getSecond();
            leftData = new Vector3f(
                    leftData.x(),
                    leftData.y(),
                    leftData.z()
            );
            leftData.add(pre.getPreDataPoint());
            leftTime = new Vector3f(
                    leftTime.x() + pre.getTime(),
                    leftTime.y() + pre.getTime(),
                    leftTime.z() + pre.getTime()
            );
            leftCtrlPoint = Pair.of(leftTime, leftData);
        }
        if (rightCtrlPoint != null) {
            Vector3f rightTime = rightCtrlPoint.getFirst();
            Vector3f rightData = rightCtrlPoint.getSecond();
            rightData = new Vector3f(
                    rightData.x(),
                    rightData.y(),
                    rightData.z()
            );
            updateEvaluations(animator, next);
            rightData.add(next.getPostDataPoint());
            rightTime = new Vector3f(
                    rightTime.x() + next.getTime(),
                    rightTime.y() + next.getTime(),
                    rightTime.z() + next.getTime()
            );
            rightCtrlPoint = Pair.of(rightTime, rightData);
        }
        Vec2f[][] data;
        Vector3f preTime = new Vector3f(
                pre.getTime(),
                pre.getTime(),
                pre.getTime()
        );
        Vector3f nextTime = new Vector3f(
                next.getTime(),
                next.getTime(),
                next.getTime()
        );
        Pair<Vector3f, Vector3f> prePair = Pair.of(
                preTime, pre.getPostDataPoint()
        );
        Pair<Vector3f, Vector3f> nextPair = Pair.of(
                nextTime, next.getPreDataPoint()
        );
        if (leftCtrlPoint == null && rightCtrlPoint == null) {
            data = new Vec2f[2][3];
            data[0] = fillData(prePair);
            data[1] = fillData(nextPair);
        } else if (leftCtrlPoint == null || rightCtrlPoint == null) {
            data = new Vec2f[3][3];
            data[0] = fillData(prePair);
            if (leftCtrlPoint != null) {
                data[1] = fillData(leftCtrlPoint);
            } else {
                data[1] = fillData(rightCtrlPoint);
            }
            data[2] = fillData(nextPair);
        } else {
            data = new Vec2f[4][3];
            data[0] = fillData(prePair);
            data[1] = fillData(leftCtrlPoint);
            data[2] = fillData(rightCtrlPoint);
            data[3] = fillData(nextPair);
        }
        return data;
    }

    private Vec2f[] fillData(Pair<Vector3f, Vector3f> pair) {
        Vec2f[]  data = new Vec2f[3];
        Vector3f time = pair.getFirst();
        Vector3f pos = pair.getSecond();
        data[0] = new Vec2f(time.x(), pos.x());
        data[1] = new Vec2f(time.y(), pos.y());
        data[2] = new Vec2f(time.z(), pos.z());
        return data;
    }

    public Vec2f getBezierPointAt(float percentage, Vec2f start, Vec2f end, Vec2f... controlPoints) {
        if (controlPoints == null || controlPoints.length == 0) {
            return end.subtract(start).scale(percentage).add(start);
        } else if (controlPoints.length == 1) {
            Vec2f vector1 = controlPoints[0].subtract(start)
                    .scale(percentage).add(start);
            Vec2f vector2 = end.subtract(controlPoints[0])
                    .scale(percentage).add(controlPoints[0]);
            return getBezierPointAt(percentage, vector1, vector2);
        } else {
            Vec2f neoStart = controlPoints[0].subtract(start).scale(percentage).add(start);
            Vec2f neoEnd = controlPoints[controlPoints.length - 1].subtract(end)
                    .scale(percentage).add(controlPoints[controlPoints.length - 1]);
            Vec2f[] neoControlPoints = new Vec2f[controlPoints.length - 1];
            for (int i = 0; i < neoControlPoints.length - 1; i++) {
                neoControlPoints[i] = controlPoints[i + 1].subtract(controlPoints[i])
                        .scale(percentage).add(controlPoints[i]);
            }
            return getBezierPointAt(percentage, neoStart, neoEnd, neoControlPoints);
        }
    }
}
