package kasuga.lib.core.client.block_bench_model.anim.instance;

import kasuga.lib.core.client.block_bench_model.anim_model.AnimBlockBenchModel;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Getter
public class Ticker {

    private final AnimationInstance animation;
    private final AnimBlockBenchModel model;

    private boolean start = false;

    private int tick;
    private int anim_tick;
    private float lengthTick, startDelayTick, loopDelayTick;

    @Setter
    private float speed;

    public Ticker(AnimBlockBenchModel model, AnimationInstance animation) {
        this.animation = animation;
        this.model = model;
        tick = 0;
        anim_tick = 0;
        refreshParamTicks();
        speed = 1f;
    }

    public void refreshParamTicks() {
        lengthTick = animation.getLength() * 20f;
        startDelayTick = animation.getStartDelay() * 20f;
        loopDelayTick = animation.getLoopDelay() * 20f;
    }

    public void tick() {
        if (!start) return;
        if (speed == 0) speed = 1;
        if (tick >= (lengthTick + loopDelayTick) / speed) {
            switch (getAnimation().getLoopMode()) {
                case HOLD -> {
                    return;
                }
                case LOOP -> {
                    tick = 0;
                    anim_tick = 0;
                }
                case ONCE -> {
                    tick = 0;
                    anim_tick = 0;
                    start = false;
                }
            }
        }
        tick++;
        if (tick < startDelayTick / speed ||
            (tick >= lengthTick / speed &&
                    tick < (lengthTick + loopDelayTick) / speed)) {
            return;
        }
        anim_tick++;
    }

    public void stop() {
        start = false;
        tick = 0;
        anim_tick = 0;
    }

    public void pause() {
        start = false;
    }

    public void start() {
        start = true;
    }

    public float toSecond(float partialTick) {
        return AnimationInstance.tickToSecond(anim_tick, partialTick);
    }

    public void applyToModel(float partialTick) {
        float sec = toSecond(partialTick) * speed;
        animation.updateTransformations(sec);
        model.applyAnimation(this);
    }
}
