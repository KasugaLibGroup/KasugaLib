package kasuga.lib.core.client.model.anim_instance;

import kasuga.lib.core.client.animation.neo_neo.VectorIOUtil;
import kasuga.lib.core.client.model.anim_json.CatmullRomUtils;
import kasuga.lib.core.client.model.anim_json.KeyFrame;
import kasuga.lib.core.client.model.anim_json.Pose;
import kasuga.lib.core.client.model.anim_model.AnimBone;
import kasuga.lib.core.util.data_type.Pair;
import org.joml.Vector3f;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static kasuga.lib.core.client.model.anim_instance.AnimationInstance.read4Bytes;
import static kasuga.lib.core.client.model.anim_instance.AnimationInstance.write4Bytes;

@InstanceOf(value = KeyFrame.class)
public class KeyFrameInstance {
    public final KeyFrame keyFrame;

    public final AnimBone bone;

    public final AnimationInstance animation;
    private final ArrayList<Vector3f> position, rotation, scale;
    private final float posStartSec, posEndSec;
    private final float rotStartSec, rotEndSec;
    private final float scaleStartSec, scaleEndSec;
    private final Vector3f positionStart, positionApproach,
                            rotationStart, rotationApproach,
                            scaleStart, scaleApproach;
    public static final Vector3f ONE = new Vector3f(1, 1, 1);


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

        compile(positionFrames, rotationFrames, scaleFrames);

        if (positionFrames.isEmpty()) {
            positionStart = new Vector3f();
            positionApproach = new Vector3f();
        } else {
            if (posStartSec <= 0) positionStart = this.position.get(0);
            else positionStart = new Vector3f();
            positionApproach = new Vector3f(positionFrames.get(positionFrames.size() - 1).getValue().getPost());
            positionApproach.mul(1 / 16f);
        }

        if (rotationFrames.isEmpty()) {
            rotationStart = new Vector3f();
            rotationApproach = new Vector3f();
        } else {
            if (rotStartSec <= 0) rotationStart = this.rotation.get(0);
            else rotationStart = new Vector3f();
            rotationApproach = rotationFrames.get(rotationFrames.size() - 1).getValue().getPost();
        }

        if (scaleFrames.isEmpty()) {
            scaleStart = new Vector3f(ONE);
            scaleApproach = new Vector3f(ONE);
        } else {
            if (scaleStartSec <= 0) scaleStart = this.scale.get(0);
            else scaleStart = new Vector3f(ONE);
            scaleApproach = scaleFrames.get(scaleFrames.size() - 1).getValue().getPost();
        }
    }

    private void compile(
            List<Map.Entry<Float, Pose>> position,
            List<Map.Entry<Float, Pose>> rotation,
            List<Map.Entry<Float, Pose>> scale) {
            singleCompile(position, this.position, animation.getStep());
            this.position.forEach(vec -> vec.mul(1 / 16f));
            singleCompile(rotation, this.rotation, animation.getStep());
            singleCompile(scale, this.scale, animation.getStep());
    }

    private void singleCompile(List<Map.Entry<Float, Pose>> in, List<Vector3f> out, float step) {
        if (in.isEmpty()) return;

        Map.Entry<Float, Pose> first, second;
        int length = in.size() - 1;
        float time1, time2;

        float recentTime = -1;
        for (int i = 0; i < length; i++) {
            first = in.get(i);
            second = in.get(i + 1);
            time1 = first.getKey();
            time2 = second.getKey();
            if (recentTime < 0) recentTime = time1;

            // deal with step
            if (first.getValue().hasPre()) {
                Pair<Vector3f, Float> result = interpolationStep(first.getValue(), recentTime, step);
                out.add(result.getFirst());
                recentTime = result.getSecond();
                continue;
            } else if (first.getValue().isCatmullRom() || second.getValue().isCatmullRom()) {
                // deal with catmull-rom spline
                Vector3f[] controlPoints;

                if (length < 2) {
                    // could not get catmull-rom, turn to linear (2 points)
                    Pair<Vector3f[], Float> result =
                            interpolationLinear(first.getValue().getPost(), second.getValue().getPost(),
                                    time1, time2, step, recentTime);
                    recentTime = result.getSecond();
                    out.addAll(Arrays.asList(result.getFirst()));
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
                // apply catmull-rom spline
                Pair<Vector3f[], Float> points = interpolationCatmullRom(controlPoints, time1, time2, step, recentTime);
                recentTime = points.getSecond();
                out.addAll(Arrays.asList(points.getFirst()));
                continue;
            } else {
                // linear
                Pair<Vector3f[], Float> points =
                        interpolationLinear(first.getValue().getPost(), second.getValue().getPost(),
                                time1, time2, step, recentTime);
                recentTime = points.getSecond();
                out.addAll(Arrays.asList(points.getFirst()));
            }
        }
        // deal with the last frame.
        Pose pose = in.get(in.size() - 1).getValue();
        if (pose.hasPre()) {
            Pair<Vector3f, Float> result = interpolationStep(pose, recentTime, step);
            out.add(result.getFirst());
        } else {
            out.add(new Vector3f(pose.getPost()));
        }
    }

    public static Pair<Vector3f[], Float> interpolationLinear(Vector3f first, Vector3f last,
                                                              float start, float end, float step, float recentTime) {
        float length = end - start;
        int size = (int) ((end - recentTime) / step);
        float timeOffset = (recentTime - start) / length;
        Vector3f[] result = new Vector3f[size];
        Vector3f offset = new Vector3f(last);
        offset.sub(first);
        for (int i = 0; i < size; i++) {
            float time = (float) i / (float) size + timeOffset;
            Vector3f o = new Vector3f(offset);
            o.mul(time);
            o.add(first);
            result[i] = o;
        }
        return Pair.of(result, recentTime + (float) size * step);
    }

    public static Pair<Vector3f[], Float> interpolationCatmullRom(Vector3f[] controlPoints, float start,
                                                                  float end, float step, float recentTime) {
        float length = end - start;
        int size = (int) ((end - recentTime) / step);
        Vector3f[] result = new Vector3f[size];
        float offset = (recentTime - start) / length;
        for (int i = 0; i < size; i++) {
            result[i] = CatmullRomUtils.applyCRS(controlPoints, (float) i / (float) size + offset);
        }
        return Pair.of(result, recentTime + (float) size * step);
    }

    public static Pair<Vector3f, Float> interpolationStep(Pose pose, float start, float step) {
        return Pair.of(new Vector3f(pose.getPost()), start + step);
    }

    public static Vector3f getVecAsLeft(Pose pose) {
        return pose.getPost();
    }

    public static Vector3f getVecAsRight(Pose pose) {
        return pose.hasPre() ? pose.getPre() : pose.getPost();
    }

    public Vector3f getPosition(float sec) {
        if (sec < this.posStartSec) return positionStart;
        if (sec >= animation.length) return switch (animation.loop) {
            case NONE -> new Vector3f();
            case HOLD_ON_LAST_FRAME -> positionApproach;
            case LOOP -> getPosition(sec % animation.length);
        };
        if (sec >= posEndSec) {
            return positionApproach;
        }
        if (position.size() < 2) {
            return position.get(0);
        }
        int index = (int) ((sec - posStartSec) * this.animation.frameRate);
        if (index >= position.size() - 1) {
            return position.get(position.size() - 1);
        }
        float time = (float) index * animation.getStep();
        float percentage = (sec - posStartSec - time) / animation.getStep();
        return slerp(position.get(index), position.get(index + 1), percentage);
    }

    public Vector3f getRotation(float sec) {
        if (sec < this.rotStartSec) return rotationStart;
        if (sec >= animation.length) return switch (animation.loop) {
            case NONE -> new Vector3f();
            case HOLD_ON_LAST_FRAME -> rotationApproach;
            case LOOP -> getRotation(sec % animation.length);
        };
        if (sec >= rotEndSec) {
            return rotationApproach;
        }
        if (rotation.size() < 2) {
            return rotation.get(0);
        }
        int index = (int) ((sec - rotStartSec) * this.animation.frameRate);
        if (index >= rotation.size() - 1) {
            return rotation.get(rotation.size() - 1);
        }
        float time = (float) index * animation.getStep();
        float percentage = (sec - rotStartSec - time) / animation.getStep();
        return slerp(rotation.get(index), rotation.get(index + 1), percentage);
    }

    public Vector3f getScale(float sec) {
        if (sec < this.scaleStartSec) return scaleStart;
        if (sec >= animation.length) return switch (animation.loop) {
            case NONE -> ONE;
            case HOLD_ON_LAST_FRAME -> scaleApproach;
            case LOOP -> getScale(sec % animation.length);
        };
        if (sec >= scaleEndSec) {
            return scaleApproach;
        }
        if (scale.size() < 2) {
            return scale.get(0);
        }
        int index = (int) ((sec - scaleStartSec) * this.animation.frameRate);
        if (index >= scale.size() - 1) {
            return scale.get(scale.size() - 1);
        }
        float time = (float) index * animation.getStep();
        float percentage = (sec - scaleStartSec - time) / animation.getStep();
        return slerp(scale.get(index), scale.get(index + 1), percentage);
    }

    public static Vector3f slerp(Vector3f first, Vector3f second, float percentage) {
        Vector3f result = new Vector3f(second);
        result.sub(first);
        result.mul(percentage);
        result.add(first);
        return result;
    }

    public void applyToBone(float sec) {
        if (posStartSec >= 0 && posEndSec >= 0) bone.setOffset(getPosition(sec));
        if (rotStartSec >= 0 && rotEndSec >= 0) bone.setAnimRot(getRotation(sec));
        if (scaleStartSec >= 0 && scaleEndSec >= 0) bone.setScale(getScale(sec));
    }

    public boolean canBeRemoved() {
        return posStartSec == -1 && posEndSec == -1 &&
                rotStartSec == -1 && rotEndSec == -1 &&
                scaleStartSec == -1 && scaleEndSec == -1;
    }

    public void writeToCache(ByteArrayOutputStream stream) throws IOException {
        String frameName = this.keyFrame.bone;
        byte[] fnb = frameName.getBytes(StandardCharsets.UTF_8);
        write4Bytes(fnb.length, stream);
        stream.write(fnb);

        write4Bytes(Float.floatToIntBits(posStartSec), stream);
        write4Bytes(Float.floatToIntBits(posEndSec), stream);
        write4Bytes(Float.floatToIntBits(rotStartSec), stream);
        write4Bytes(Float.floatToIntBits(rotEndSec), stream);
        write4Bytes(Float.floatToIntBits(scaleStartSec), stream);
        write4Bytes(Float.floatToIntBits(scaleEndSec), stream);

        VectorIOUtil.writeVec3fToStream(this.positionStart, stream);
        VectorIOUtil.writeVec3fToStream(this.positionApproach, stream);
        VectorIOUtil.writeVec3fToStream(this.rotationStart, stream);
        VectorIOUtil.writeVec3fToStream(this.rotationApproach, stream);
        VectorIOUtil.writeVec3fToStream(this.scaleStart, stream);
        VectorIOUtil.writeVec3fToStream(this.scaleApproach, stream);

        write4Bytes(this.position.size(), stream);
        write4Bytes(this.rotation.size(), stream);
        write4Bytes(this.scale.size(), stream);

        for (Vector3f vector3f : this.position) {
            VectorIOUtil.writeVec3fToStream(vector3f, stream);
        }
        for (Vector3f vector3f : this.rotation) {
            VectorIOUtil.writeVec3fToStream(vector3f, stream);
        }
        for (Vector3f v : this.scale) {
            VectorIOUtil.writeVec3fToStream(v, stream);
        }
    }

    public KeyFrameInstance(AnimationInstance instance, ByteArrayInputStream stream) throws IOException {
        this.animation = instance;
        int fnbLength = read4Bytes(stream);
        byte[] fnb = stream.readNBytes(fnbLength);
        String n = new String(fnb, StandardCharsets.UTF_8);
        this.keyFrame = instance.animation.getFrame(n);
        this.bone = (AnimBone) instance.model.getChild(n);

        this.posStartSec = Float.intBitsToFloat(read4Bytes(stream));
        this.posEndSec = Float.intBitsToFloat(read4Bytes(stream));
        this.rotStartSec = Float.intBitsToFloat(read4Bytes(stream));
        this.rotEndSec = Float.intBitsToFloat(read4Bytes(stream));
        this.scaleStartSec = Float.intBitsToFloat(read4Bytes(stream));
        this.scaleEndSec = Float.intBitsToFloat(read4Bytes(stream));

        this.positionStart = VectorIOUtil.getVec3fFromStream(stream);
        this.positionApproach = VectorIOUtil.getVec3fFromStream(stream);
        this.rotationStart = VectorIOUtil.getVec3fFromStream(stream);
        this.rotationApproach = VectorIOUtil.getVec3fFromStream(stream);
        this.scaleStart = VectorIOUtil.getVec3fFromStream(stream);
        this.scaleApproach = VectorIOUtil.getVec3fFromStream(stream);

        int posSize = read4Bytes(stream);
        int rotSize = read4Bytes(stream);
        int scaleSize = read4Bytes(stream);
        this.position = new ArrayList<>(posSize);
        this.rotation = new ArrayList<>(rotSize);
        this.scale = new ArrayList<>(scaleSize);

        for (int i = 0; i < posSize; i++) {
            position.add(VectorIOUtil.getVec3fFromStream(stream));
        }
        for (int i = 0; i < rotSize; i++) {
            rotation.add(VectorIOUtil.getVec3fFromStream(stream));
        }
        for (int i = 0; i < scaleSize; i++) {
            scale.add(VectorIOUtil.getVec3fFromStream(stream));
        }
    }
}
