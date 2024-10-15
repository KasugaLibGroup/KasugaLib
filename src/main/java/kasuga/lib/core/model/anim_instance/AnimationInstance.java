package kasuga.lib.core.model.anim_instance;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.model.BedrockRenderable;
import kasuga.lib.core.model.anim_json.Animation;
import kasuga.lib.core.model.anim_json.KeyFrame;
import kasuga.lib.core.model.anim_json.LoopMode;
import kasuga.lib.core.model.anim_model.AnimBone;
import kasuga.lib.core.model.anim_model.AnimModel;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@InstanceOf(value = Animation.class)
public class AnimationInstance {

    public final AnimModel model;

    public final Animation animation;
    public final float frameRate, length;
    public final LoopMode loop;
    private final int stepCount;
    private final float step;
    private final HashMap<String, AnimBone> bones;
    private final HashMap<String, KeyFrameInstance> frames;
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
}
