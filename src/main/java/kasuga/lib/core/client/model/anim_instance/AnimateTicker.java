package kasuga.lib.core.client.model.anim_instance;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.model.BedrockModelLoader;
import kasuga.lib.core.client.model.anim_json.Animation;
import kasuga.lib.core.client.model.anim_json.AnimationFile;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import kasuga.lib.core.util.LazyRecomputable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class AnimateTicker {
    private float playSpeed;

    public final AnimationInstance animation;

    public final TickerType type;
    private float recent;
    private int starterTick, tick, endTick;
    private boolean moving;

    public AnimateTicker(AnimationInstance instance, TickerType type, float playSpeed) {
        this.playSpeed = playSpeed;
        this.animation = instance;
        this.type = type;
        submit();
    }

    public void submit() {
        AnimateTickerManager.INSTANCE.putTickerIn(this);
    }

    public void unload() {
        AnimateTickerManager.INSTANCE.removeTicker(this);
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public void setStarterTick(int tick) {
        this.starterTick = tick;
        this.recent = 0f;
        this.endTick = getEndTick();
    }

    public void setPlaySpeed(float speed) {
        this.playSpeed = speed;
        this.endTick = getEndTick();
    }

    public int getEndTick() {
        return (int) Math.ceil(starterTick + animation.length * 20f);
    }

    public int getTick() {
        return tick;
    }

    public void start() {
        setStarterTick(AnimateTickerManager.INSTANCE.getTick(type));
        this.recent = 0f;
        this.moving = true;
        this.tick = starterTick;
        System.out.println("start time: " + System.currentTimeMillis());
    }

    public void stop() {
        this.moving = false;
        System.out.println("stop time: " + System.currentTimeMillis());
    }

    public float getPlaySpeed() {
        return playSpeed;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public float tickToSec(float partial) {
        int offset = tick - starterTick;
        float result = ((float) offset + partial) / 20f * (playSpeed / 100f);
        if (result < 0) result += animation.length;
        this.recent = result;
        return result;
    }

    public void tickAndRender(PoseStack pose, MultiBufferSource source, int light, int overlay, float partial) {
        float sec = moving ? tickToSec(partial) : recent;
        animation.applyAndRender(pose, source, light, overlay, sec);
    }

    protected void tick(int tick) {
        if (!moving) return;
        this.tick = tick;
        if (tick >= endTick) {
            switch (animation.loop) {
                case LOOP -> this.tick = starterTick;
                case NONE -> {
                    this.tick = starterTick;
                    stop();
                }
                case HOLD_ON_LAST_FRAME -> {
                    stop();
                }
            }
        }
        tickToSec(0);
    }

    public static LazyRecomputable<AnimateTicker> getTickerInstance(
            ResourceLocation modelLoc, ResourceLocation animFile,
            RenderType type, String animName, TickerType ticker,
            int frameRate, float playSpeed) {
        return new LazyRecomputable<>(() -> {
            AnimModel model = BedrockModelLoader.getModel(modelLoc, type);
            if (model == null) return null;
            AnimationFile file = AnimationFile.fromFile(animFile).get();
            if (file == null) return null;
            Animation anim = file.getAnimation(animName);
            if (anim == null) return null;
            AnimationInstance instance = anim.getInstance(model, frameRate);
            return new AnimateTicker(instance, ticker, playSpeed);
        });
    }

    public static enum TickerType {
        RENDER, LOGICAL;
    }
}
