package kasuga.lib.core.client.model.anim_instance;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import kasuga.lib.core.annos.Beta;
import kasuga.lib.core.client.model.BedrockModelLoader;
import kasuga.lib.core.client.model.anim_json.Animation;
import kasuga.lib.core.client.model.anim_json.AnimationFile;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;

@Beta
@OnlyIn(Dist.CLIENT)
public class MultiAnimateTicker implements Ticker {
    private final HashMap<Pair<ResourceLocation, String>, AnimateTicker> tickers;

    public final AnimateTicker.TickerType type;
    private final AnimModel model;

    public MultiAnimateTicker(float playSpeed, AnimateTicker.TickerType type, AnimationInstance... instance) {
        tickers = new HashMap<>();

        if (instance.length > 0) this.model = instance[0].model;
        else this.model = null;

        for (AnimationInstance a : instance) {
            if (a == null) continue;
            if (!a.model.equals(this.model)) {
                this.tickers.clear();
                break;
            }
            tickers.put(Pair.of(a.animation.file.location, a.animation.name),
                    new AnimateTicker(a, this.getType(), playSpeed));
        }
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
    public AnimateTicker.TickerType getType() {
        return this.type;
    }

    public void setStarterTick(ResourceLocation file, String name, int tick) {
        AnimateTicker ticker = getTicker(file, name);
        if (ticker == null) return;
        ticker.setStarterTick(tick);
    }

    public void setPlaySpeed(ResourceLocation file, String name, float speed) {
        AnimateTicker ticker = getTicker(file, name);
        if (ticker == null) return;
        ticker.setPlaySpeed(speed);
    }

    public int getEndTick(ResourceLocation file, String name) {
        AnimateTicker ticker = getTicker(file, name);
        if (ticker == null) return -1;
        return ticker.getTick();
    }

    public void start(ResourceLocation file, String name) {
        AnimateTicker ticker = getTicker(file, name);
        if (ticker == null) return;
        ticker.start();
    }

    public void startAll() {
        tickers.forEach((a, b) -> b.start());
    }

    public void stop(ResourceLocation file, String name) {
        AnimateTicker ticker = getTicker(file, name);
        if (ticker == null) return;
        ticker.stop();
    }

    public void stopAll() {
        tickers.forEach((a, b) -> b.stop());
    }

    public void pause(ResourceLocation file, String name) {
        AnimateTicker ticker = getTicker(file, name);
        if (ticker == null) return;
        ticker.pause();
    }

    public void pauseAll() {
        tickers.forEach((a, b) -> b.pause());
    }

    public boolean isPaused(ResourceLocation file, String name) {
        AnimateTicker ticker = getTicker(file, name);
        return ticker != null && ticker.isPaused();
    }

    public float getPlaySpeed(ResourceLocation file, String name) {
        AnimateTicker ticker = getTicker(file, name);
        if (ticker == null) return -1;
        return ticker.getPlaySpeed();
    }

    public boolean isMoving(ResourceLocation file, String name) {
        AnimateTicker ticker = getTicker(file, name);
        return ticker != null && ticker.isMoving();
    }

    public void setMoving(ResourceLocation file, String name, boolean moving) {
        AnimateTicker ticker = getTicker(file, name);
        if (ticker == null) return;
        ticker.setMoving(moving);
    }

    public void tickAndRender(PoseStack pose, MultiBufferSource source, int light, int overlay, float partial) {
        mergeAnimation(partial);
        model.render(pose, source, light, overlay);
    }

    public void mergeAnimation(float partial) {
        HashMap<String, Triple<Vector3f, Vector3f, Vector3f>> result = Maps.newHashMap();
        this.tickers.forEach((name, ticker) -> ticker.animation.mergeAnimation(result, ticker.tickToSec(partial)));
        this.model.applyAnimation(result);
    }

    public void tick(int tick) {
        this.tickers.forEach((name, ticker) -> ticker.tick(tick));
    }

    public AnimateTicker getTicker(ResourceLocation file, String name) {
        return this.tickers.getOrDefault(Pair.of(file, name), null);
    }

    public static LazyRecomputable<MultiAnimateTicker> getTickerInstance(
            ResourceLocation modelLoc, RenderType type, AnimateTicker.TickerType ticker,
            int frameRate, float playSpeed, Pair<ResourceLocation, String>... anims) {
        return new LazyRecomputable<>(() -> {
            AnimationInstance[] instances = new AnimationInstance[anims.length];
            int counter = 0;
            AnimModel model = BedrockModelLoader.getModel(modelLoc, type);
            if (model == null) return null;
            for (Pair<ResourceLocation, String> a : anims) {
                AnimationFile file = AnimationFile.fromFile(a.getFirst()).get();
                if (file == null) return null;
                Animation anim = file.getAnimation(a.getSecond());
                if (anim == null) return null;
                instances[counter] = anim.getInstance(model, frameRate);
                counter++;
            }
            return new MultiAnimateTicker(playSpeed, ticker, instances);
        });
    }
}
