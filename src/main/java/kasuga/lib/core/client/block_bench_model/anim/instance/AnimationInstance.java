package kasuga.lib.core.client.block_bench_model.anim.instance;

import interpreter.compute.data.Namespace;
import kasuga.lib.core.client.block_bench_model.anim.Animation;
import kasuga.lib.core.client.block_bench_model.anim.Animator;
import kasuga.lib.core.client.block_bench_model.anim.KeyFrame;
import kasuga.lib.core.client.block_bench_model.anim.LoopMode;
import kasuga.lib.core.util.data_type.Pair;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Getter
public class AnimationInstance {

    private final Animation animation;

    private final HashMap<UUID, AnimatorInstance> animators;

    public AnimationInstance(Animation animation) {
        this.animation = animation;
        animators = new HashMap<>();
        compileAnimators();
        assign();
    }

    public void updateTransformations(float sec) {
        animators.forEach(
                (id, animator) -> {
                    animator.updateTransform(sec);
                }
        );
    }

    public Namespace getNamespace() {
        return animation.getNamespace();
    }

    public void compileAnimators() {
        for (Map.Entry<UUID, Animator> entry : animation.getAnimators().entrySet()) {
            AnimatorInstance animator = new AnimatorInstance(this, entry.getValue());
            animators.put(entry.getKey(), animator);
        }
    }

    @SafeVarargs
    public final void assign(Pair<String, Float>... assignments) {
        for (Pair<String, Float> assignment : assignments) {
            getNamespace().assign(assignment.getFirst(), assignment.getSecond());
        }
        for (Map.Entry<UUID, AnimatorInstance> entry : animators.entrySet()) {
            entry.getValue().compileStaticFrames();
        }
    }

    public static float tickToSecond(int tick, float partial) {
        return ((float) tick + partial) / 20f;
    }

    public float getStartDelay() {
        return animation.getStartDelaySup().get();
    }

    public float getLoopDelay() {
        return animation.getLoopDelaySup().get();
    }

    public float getLength() {
        return animation.getLength();
    }

    public LoopMode getLoopMode() {
        return animation.getLoopMode();
    }
}
