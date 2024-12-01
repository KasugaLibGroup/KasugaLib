package kasuga.lib.core.client.model.anim_instance;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.model.AnimModelLoader;
import kasuga.lib.core.client.model.BedrockModelLoader;
import kasuga.lib.core.client.model.anim_json.Animation;
import kasuga.lib.core.client.model.anim_json.AnimationFile;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import kasuga.lib.core.util.LazyRecomputable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AnimateTicker implements Ticker {
    private float playSpeed;

    public final AnimationInstance animation;

    public final TickerType type;
    private float recent;
    private int starterTick, tick, endTick;
    private boolean moving, paused;

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

    @Override
    public TickerType getType() {
        return type;
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
        return (int) Math.ceil((double) this.starterTick + (this.animation.length * 20) * 100f / Math.abs(playSpeed));
    }

    public int getTick() {
        return tick;
    }

    public void start() {
        if (!paused) {
            setStarterTick(AnimateTickerManager.INSTANCE.getTick(type));
            this.recent = 0f;
            this.moving = true;
            this.tick = starterTick;
        } else {
            int t = AnimateTickerManager.INSTANCE.getTick(type);
            setStarterTick(t - tick);
            this.moving = true;
            paused = false;
            this.tick = t;
        }
    }

    public void stop() {
        this.moving = false;
        this.paused = false;
    }

    public void pause() {
        this.paused = true;
        this.moving = false;
    }

    public float getPlaySpeed() {
        return playSpeed;
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public float tickToSec(float partial) {
        float offset = (float) tick - (float) starterTick + partial;
        float percentage = offset / (float) (endTick - starterTick);
        float result = percentage * this.animation.length;
        if (playSpeed < 0) result = this.animation.length - result;
        this.recent = result;
        return result;
    }

    public void tickAndRender(PoseStack pose, MultiBufferSource source, int light, int overlay, float partial) {
        float sec = moving ? tickToSec(partial) : recent;
        animation.applyAndRender(pose, source, light, overlay, sec);
    }

    public void tick(int tick) {
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
            String animName, TickerType ticker,
            int frameRate, float playSpeed) {
        return new LazyRecomputable<>(() -> {
            AnimModel model = AnimModelLoader.INSTANCE.getModel(modelLoc);
            if (model == null) return null;
            AnimationFile file = AnimationFile.fromFile(animFile).get();
            if (file == null) return null;
            Animation anim = file.getAnimation(animName);
            if (anim == null) return null;
            AnimationInstance instance = anim.getInstance(model, frameRate);
            return new AnimateTicker(instance, ticker, playSpeed);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static enum TickerType {
        RENDER, LOGICAL;
    }
}
