package kasuga.lib.core.client.model.anim_instance;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.model.anim_json.Animation;
import kasuga.lib.core.client.model.BedrockRenderable;
import kasuga.lib.core.client.model.anim_json.LoopMode;
import kasuga.lib.core.client.model.anim_model.AnimBone;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Triple;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@InstanceOf(value = Animation.class)
public class AnimationInstance {

    public final AnimModel model;

    public final Animation animation;
    public final float length;
    public final LoopMode loop;
    public final int frameRate;
    private final int stepCount;
    private final float step;
    private final HashMap<String, AnimBone> bones;
    private final HashMap<String, KeyFrameInstance> frames;
    public static final int VERSION = -1766176;
    public AnimationInstance(Animation animation, AnimModel model, int frameRate) {
        this.animation = animation;
        this.model = model;
        this.frameRate = frameRate;
        this.length = animation.getAnimationLength();
        this.loop = animation.getLoop();
        stepCount = length <= 0 ? 1 : (int) Math.ceil(length * frameRate);
        this.step = this.length / (float) stepCount;

        bones = new HashMap<>();
        frames = new HashMap<>();

        if (verify()) compile();
    }

    public AnimationInstance(AnimModel animModel, Animation animation, ByteArrayInputStream stream) throws IOException {
        int version = read4Bytes(stream);
        if (version != VERSION) {
            throw new IOException("Invalid version number: " + version);
        }

        this.animation = animation;
        this.model = animModel;

        int aibLength = read4Bytes(stream);
        int fileLocLength = read4Bytes(stream);
        String animIdentifier = new String(stream.readNBytes(aibLength), StandardCharsets.UTF_8);
        int mibLength = read4Bytes(stream);
        int modelLocLength = read4Bytes(stream);
        String modelIdentifier = new String(stream.readNBytes(mibLength), StandardCharsets.UTF_8);

        String fileLoc = animIdentifier.substring(0, fileLocLength);
        String animName = animIdentifier.substring(fileLocLength + 1);
        String modelLoc = modelIdentifier.substring(0, modelLocLength);
        String geometryName = modelIdentifier.substring(modelLocLength + 1);

        if (!verifyFileName(fileLoc, animName, modelLoc, geometryName)) {
            StringBuilder builder = new StringBuilder("Incompatible files: \n")
                    .append("Recent files: ").append(fileLoc).append(" -> ")
                    .append(animName).append(" & ").append(modelLoc).append(" -> ")
                    .append(geometryName)
                    .append("Needed: ").append(animation.file.location).append(" -> ")
                    .append(animation.name).append(" & ").append(model.geometry.getModel().modelLocation)
                    .append(" -> ").append(model.geometry.getDescription().getIdentifier())
                    .append(".\nPlease check your files.");
            throw new IOException(builder.toString());
        }

        this.length = Float.intBitsToFloat(read4Bytes(stream));
        this.frameRate = read4Bytes(stream);
        this.step = Float.intBitsToFloat(read4Bytes(stream));
        this.stepCount = Float.floatToRawIntBits(read4Bytes(stream));
        this.loop = LoopMode.fromIndex(stream.read());

        int boneSize = read4Bytes(stream);
        this.bones = new HashMap<>();
        for (int i = 0; i < boneSize; i++) {
            int x = read4Bytes(stream);
            byte[] b = stream.readNBytes(x);
            String bone = new String(b, StandardCharsets.UTF_8);
            BedrockRenderable renderable = model.getChild(bone);
            if (!(renderable instanceof AnimBone ab)) continue;
            bones.put(bone, ab);
        }

        int frameSize = read4Bytes(stream);
        this.frames = new HashMap<>();
        for (int i = 0; i < frameSize; i++) {
            KeyFrameInstance kfi = new KeyFrameInstance(this, stream);
            this.frames.put(kfi.keyFrame.bone, kfi);
        }
    }

    public boolean verify() {
        Map<String, BedrockRenderable> bones = model.children;
        HashSet<String> animKeyFrameSet = new HashSet<>(animation.getFrames().keySet());
        for (Map.Entry<String, BedrockRenderable> entry : bones.entrySet()) {
            if (!(entry.getValue() instanceof AnimBone bone)) continue;
            animKeyFrameSet.remove(entry.getKey());
            this.bones.put(entry.getKey(), bone);
        }
        boolean flag = animKeyFrameSet.isEmpty();
        if (!flag) this.bones.clear();
        return flag;
    }

    public int getStepCount() {
        return stepCount;
    }

    public float getStep() {
        return step;
    }

    public void compile() {
        animation.getFrames().forEach((name, frame) -> {
            KeyFrameInstance kfi = new KeyFrameInstance(frame, bones.get(name), this);
            if (kfi.canBeRemoved()) return;
            frames.put(name, kfi);
        });
    }

    public void applyAndRender(PoseStack pose, MultiBufferSource buffer, int light, int overlay, float sec) {
        frames.forEach((name, frame) -> {frame.applyToBone(sec);});
        model.render(pose, buffer, light, overlay);
    }

    public void mergeAnimation(HashMap<String, Triple<Vector3f, Vector3f, Vector3f>> vectors, float sec) {
        this.frames.forEach((name, frame) -> {
            Triple<Vector3f, Vector3f, Vector3f> v = frame.getVectors(sec);
            Triple<Vector3f, Vector3f, Vector3f> cached = vectors.getOrDefault(name, null);
            if (cached == null) {
                vectors.put(name, v);
                return;
            }
            cached.getLeft().add(v.getLeft());
            cached.getMiddle().add(v.getMiddle());
            cached.getRight().mul(v.getRight().x(), v.getRight().y(), v.getRight().z());
        });
    }

    public void writeToCache(ByteArrayOutputStream stream) throws IOException {
        // identifier
        String animIdentifier = animation.file.location.toString() + ":" + animation.name;
        String modelIdentifier = this.model.geometry.getModel().modelLocation + ":" +
                this.model.geometry.getDescription().getIdentifier();

        // basic data
        write4Bytes(VERSION, stream);
        byte[] aib = animIdentifier.getBytes(StandardCharsets.UTF_8);
        byte[] mib = modelIdentifier.getBytes(StandardCharsets.UTF_8);
        write4Bytes(aib.length, stream);
        write4Bytes(animation.file.location.toString().length(), stream);
        stream.write(aib);
        write4Bytes(mib.length, stream);
        write4Bytes(this.model.geometry.getModel().modelLocation.toString().length(), stream);
        stream.write(mib);
        write4Bytes(Float.floatToIntBits(this.length), stream);
        write4Bytes(this.frameRate, stream);
        write4Bytes(Float.floatToIntBits(this.step), stream);
        write4Bytes(Float.floatToIntBits(this.stepCount), stream);
        stream.write(loop.getIndex());

        // bones
        write4Bytes(bones.size(), stream);
        for (String key : bones.keySet()) {
            byte[] b = key.getBytes(StandardCharsets.UTF_8);
            write4Bytes(b.length, stream);
            stream.write(b);
        }
        // key frames
        write4Bytes(frames.size(), stream);
        for (KeyFrameInstance frame : frames.values()) {
            frame.writeToCache(stream);
        }
    }

    public boolean writeToFile(File file) {
        try {
            if (!file.isFile() && !file.createNewFile()) {
                KasugaLib.MAIN_LOGGER.error("Failed to save animation " + animation.name + " to file " +
                        file.getPath() + ", could not create that file.");
                return false;
            }
            try (FileOutputStream stream = new FileOutputStream(file)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                this.writeToCache(baos);
                baos.writeTo(stream);
                baos.flush();
                stream.flush();
                baos.close();
            }
            return true;
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Encountered error while write anim cache to file " + file, e);
            return false;
        }
    }

    private boolean verifyFileName(String fileLoc, String animName, String modelLoc, String geometryName) {
        return fileLoc.equals(this.animation.file.location.toString()) &&
                animName.equals(this.animation.name) &&
                modelLoc.equals(this.model.geometry.getModel().modelLocation.toString()) &&
                geometryName.equals(this.model.geometry.getDescription().getIdentifier());
    }

    public static int read4Bytes(InputStream stream) throws IOException {
        int result = 0;
        result += stream.read();
        result += (stream.read() << 8);
        result += (stream.read() << 16);
        result += (stream.read() << 24);
        return result;
    }

    public static void write4Bytes(int in, OutputStream stream) throws IOException {
        stream.write(in & 0xff);
        stream.write((in >> 8) & 0xff);
        stream.write((in >> 16) & 0xff);
        stream.write((in >>> 24));
    }
}
