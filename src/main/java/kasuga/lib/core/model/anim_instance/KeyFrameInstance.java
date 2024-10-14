package kasuga.lib.core.model.anim_instance;

import com.mojang.math.Vector3f;
import kasuga.lib.core.model.anim_json.CatmullRomUtils;
import kasuga.lib.core.model.anim_json.KeyFrame;
import kasuga.lib.core.model.anim_json.Pose;
import kasuga.lib.core.model.anim_model.AnimBone;

import java.util.*;

@InstanceOf(value = KeyFrame.class)
public class KeyFrameInstance {
    public final KeyFrame keyFrame;
    public final AnimBone bone;
    public final AnimationInstance animation;
    private final ArrayList<Vector3f> position, rotation, scale;
    private final float posStartSec, posEndSec;
    private final float rotStartSec, rotEndSec;
    private final float scaleStartSec, scaleEndSec;
    public KeyFrameInstance(KeyFrame keyFrame, AnimBone bone, AnimationInstance animation) {
        this.keyFrame = keyFrame;
        this.bone = bone;
        this.animation = animation;
        this.position = new ArrayList<>(animation.getStepCount());
        this.rotation = new ArrayList<>(animation.getStepCount());
        this.scale = new ArrayList<>(animation.getStepCount());

        List<Map.Entry<Float, Pose>> positionFrames = keyFrame.sortPositions();
        List<Map.Entry<Float, Pose>> rotationFrames = keyFrame.sortRotations();
        List<Map.Entry<Float, Pose>> scaleFrames = keyFrame.sortScale();

        if (positionFrames.isEmpty()) {
            posStartSec = -1f;
            posEndSec = -1f;
        } else if (positionFrames.size() == 1) {
            posStartSec = Math.max(0f, Math.min(animation.length, positionFrames.get(0).getKey()));
            posEndSec = posStartSec;
        } else {
            posStartSec = Math.max(0f, positionFrames.get(0).getKey());
            posEndSec = Math.min(animation.length, positionFrames.get(positionFrames.size() - 1).getKey());
        }

        if (rotationFrames.isEmpty()) {
            rotStartSec = -1f;
            rotEndSec = -1f;
        } else if (rotationFrames.size() == 1) {
            rotStartSec = Math.max(0f, Math.min(animation.length, rotationFrames.get(0).getKey()));
            rotEndSec = rotStartSec;
        } else {
            rotStartSec = Math.max(0f, rotationFrames.get(0).getKey());
            rotEndSec = Math.min(animation.length, rotationFrames.get(rotationFrames.size() - 1).getKey());
        }

        if (scaleFrames.size() == 0) {
            scaleStartSec = -1f;
            scaleEndSec = -1f;
        } else if (scaleFrames.size() == 1) {
            scaleStartSec = Math.max(0f, Math.min(animation.length, scaleFrames.get(0).getKey()));
            scaleEndSec = scaleStartSec;
        } else {
            scaleStartSec = Math.max(0f, scaleFrames.get(0).getKey());
            scaleEndSec = Math.min(animation.length, scaleFrames.get(scaleFrames.size() - 1).getKey());
        }

    }

    private void compile(
            List<Map.Entry<Float, Pose>> position,
            List<Map.Entry<Float, Pose>> rotation,
            List<Map.Entry<Float, Pose>> scale) {

    }

    private void singleCompile(List<Map.Entry<Float, Pose>> in, List<Vector3f> out,
                               float start, float end, float step) {
        Map.Entry<Float, Pose> first, second;
        int length = in.size() - 1;
        float time1, time2;
        if (in.isEmpty()) return;
        float beforeStart = start - in.get(0).getKey();
        float afterEnd = end - in.get(in.size() - 1).getKey();
        if (beforeStart > 0) {
            int size = (int) (beforeStart / step);
        }
        if (in.size() == 1) {
            out.add(interpolationStep(in.get(0).getValue()));
            return;
        }
        for (int i = 0; i < length; i++) {
            first = in.get(i);
            second = in.get(i + 1);
            time1 = first.getKey();
            time2 = second.getKey();

            // deal with step
            if (first.getValue().hasPre()) {
                out.add(interpolationStep(first.getValue()));
                continue;
            } else if (first.getValue().isCatmullRom() || second.getValue().isCatmullRom()) {
                // deal with catmull-rom spline
                Vector3f[] controlPoints;
                // could not get catmull-rom, turn to linear (2 points)
                if (length < 2) {
                    controlPoints = interpolationLinear(first.getValue().getPost(), second.getValue().getPost(), time1, time2, step);
                    out.addAll(Arrays.asList(controlPoints));
                    continue;
                } else if (i == length - 1) {
                    // last 3 points
                    controlPoints = CatmullRomUtils.last3PointsToCRSPoints(in.get(i - 1).getValue().getPost(),
                            first.getValue().getPost(), getVecAsRight(second.getValue()));
                } else if (i == 0) {
                    // first 3 points
                    controlPoints = CatmullRomUtils.first3PointsToCRSPoints(first.getValue().getPost(),
                            second.getValue().getPost(), getVecAsRight(in.get(i + 2).getValue()));
                } else {
                    // 4 points
                    controlPoints = CatmullRomUtils.genDefaultCRSPoints(
                            in.get(i - 1).getValue().getPost(),
                            first.getValue().getPost(),
                            second.getValue().getPost(),
                            getVecAsRight(in.get(i + 2).getValue())
                            );
                }
                Vector3f[] points = interpolationCatmullRom(controlPoints, time1, time2, step);
                out.addAll(Arrays.asList(points));
                continue;
            } else {
                Vector3f[] points = interpolationLinear(first.getValue().getPost(), second.getValue().getPost(), time1, time2, step);
                out.addAll(Arrays.asList(points));
            }
        }
        Pose pose = in.get(in.size() - 1).getValue();
        if (pose.hasPre()) {out.add(interpolationStep(pose));}
    }

    public static Vector3f[] interpolationLinear(Vector3f first, Vector3f last,
                                                 float start, float end, float step) {
        float length = end - start;
        int size = (int) (length / step);
        Vector3f[] result = new Vector3f[size];
        Vector3f offset = last.copy();
        offset.sub(first);
        for (int i = 0; i < size; i++) {
            float time = (float) i / (float) size;
            Vector3f o = offset.copy();
            o.mul(time);
            result[i] = o;
        }
        return result;
    }

    public static Vector3f[] interpolationCatmullRom(Vector3f[] controlPoints, float start, float end, float step) {
        float length = end - start;
        int size = (int) (length / step);
        Vector3f[] result = new Vector3f[size];
        for (int i = 0; i < size; i++) {
            result[i] = CatmullRomUtils.applyCRS(controlPoints, (float) i / (float) size);
        }
        return result;
    }

    public static Vector3f interpolationStep(Pose pose) {
        return pose.getPost();
    }

    public static Vector3f getVecAsLeft(Pose pose) {
        return pose.getPost();
    }

    public static Vector3f getVecAsRight(Pose pose) {
        return pose.hasPre() ? pose.getPre() : pose.getPost();
    }
}
