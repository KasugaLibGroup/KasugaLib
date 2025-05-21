package kasuga.lib.core.client.block_bench_model.anim.instance;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.block_bench_model.anim.Animation;
import kasuga.lib.core.client.block_bench_model.anim_model.AnimBlockBenchModel;
import kasuga.lib.core.util.data_type.Pair;
import lombok.Getter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
@Getter
public class AnimationController {

    private final HashMap<UUID, Ticker> animations;
    private final AnimBlockBenchModel model;
    public static final HashSet<AnimationController> CONTROLLERS = new HashSet<>();

    public AnimationController(AnimBlockBenchModel model) {
        this.model = model;
        this.animations = new HashMap<>();
        for (Animation animation : model.getModel().getFile().getAnimations()) {
            AnimationInstance instance = new AnimationInstance(animation);
            this.animations.put(animation.getUuid(), new Ticker(model, instance));
        }
        CONTROLLERS.add(this);
    }

    @Override
    protected void finalize() throws Throwable {
        CONTROLLERS.remove(this);
        super.finalize();
    }

    public Optional<Ticker> findAnimation(String name) {
        for (Ticker ticker : this.animations.values()) {
            if (ticker.getAnimation().getAnimation().getName().equals(name)) {
                return Optional.of(ticker);
            }
        }
        return Optional.empty();
    }

    public Optional<UUID> findIdOf(String name) {
        for (Map.Entry<UUID, Ticker> entry : this.animations.entrySet()) {
            if (entry.getValue().getAnimation().getAnimation().getName().equals(name)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public void start(UUID id) {
        Optional<Ticker> tickerOpt = getAnimation(id);
        tickerOpt.ifPresent(Ticker::start);
    }

    public void stop(UUID id) {
        Optional<Ticker> tickerOpt = getAnimation(id);
        tickerOpt.ifPresent(Ticker::stop);
    }

    public void pause(UUID id) {
        Optional<Ticker> tickerOpt = getAnimation(id);
        tickerOpt.ifPresent(Ticker::pause);
    }

    public void start(String name) {
        Optional<Ticker> tickerOpt = findAnimation(name);
        tickerOpt.ifPresent(Ticker::start);
    }

    public void stop(String name) {
        Optional<Ticker> tickerOpt = findAnimation(name);
        tickerOpt.ifPresent(Ticker::stop);
    }

    public void pause(String name) {
        Optional<Ticker> tickerOpt = findAnimation(name);
        tickerOpt.ifPresent(Ticker::pause);
    }


    public void setSpeed(String name, float speed) {
        Optional<Ticker> ticker = findAnimation(name);
        ticker.ifPresent(value -> value.setSpeed(speed));
    }

    public void setSpeed(UUID id, float speed) {
        Optional<Ticker> ticker = getAnimation(id);
        ticker.ifPresent(value -> value.setSpeed(speed));
    }

    @SafeVarargs
    public final void assign(Pair<String, Float>... assignment) {
        animations.forEach(
                (id, animation) -> {
                    animation.getAnimation().assign(assignment);
                }
        );
    }

    public Optional<Ticker> getAnimation(UUID uuid) {
        return Optional.ofNullable(this.animations.getOrDefault(uuid, null));
    }

    public void tick() {
        animations.forEach((uuid, ticker) -> ticker.tick());
    }

    public void applyToModel(float partialTick) {
        model.clearAnimation();
        animations.forEach((uuid, ticker) -> {
            if (!ticker.isStart()) return;
            ticker.applyToModel(partialTick);
        });
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer,
                       int light, int overlay, float partialTick) {
        applyToModel(partialTick);
        model.render(poseStack, buffer, light, overlay);
    }
}
