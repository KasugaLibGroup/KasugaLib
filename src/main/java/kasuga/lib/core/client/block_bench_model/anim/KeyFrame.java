package kasuga.lib.core.client.block_bench_model.anim;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import interpreter.compute.data.Namespace;
import interpreter.compute.infrastructure.Formula;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.block_bench_model.anim.interpolation.InterpolationType;
import kasuga.lib.core.util.data_type.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.UUID;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Getter
public class KeyFrame {

    public static final Supplier<Vector3f> ZERO_DATA = Vector3f::new;

    private final Animation anim;
    private final Animator animator;
    private final Channel channel;
    private final float time;
    private final InterpolationType interpolation;
    private final UUID id;
    @Setter
    private boolean isBezierLinked;

    @NotNull
    @Setter
    private Supplier<Vector3f> preDataPointSup, postDataPointSup;

    @Nullable
    @Setter
    private Supplier<Pair<Vector3f, Vector3f>> bezierLeftSup, bezierRightSup;

    public KeyFrame(Animation animation, Animator animator, Channel channel,
                    UUID id, InterpolationType interpolation, float time) {
        this.anim = animation;
        this.animator = animator;
        this.channel = channel;
        preDataPointSup = ZERO_DATA;
        postDataPointSup = ZERO_DATA;
        this.time = time;
        this.interpolation = interpolation;
        this.id = id;
        bezierLeftSup = null;
        bezierRightSup = null;
        isBezierLinked = false;
    }

    public Vector3f getPreDataPoint() {
        anim.getNamespace().assign("time", time);
        anim.runEvaluation();
        return preDataPointSup.get();
    }

    public Vector3f getPostDataPoint() {
        anim.getNamespace().assign("time", time);
        anim.runEvaluation();
        return postDataPointSup.get();
    }

    public void compileDataPoints(JsonArray dataArray) {
        if (dataArray.isEmpty()) return;
        this.preDataPointSup = compileSinglePoint(dataArray.get(0).getAsJsonObject());
        if (dataArray.size() > 1) {
            this.postDataPointSup = compileSinglePoint(dataArray.get(1).getAsJsonObject());
        } else {
            this.postDataPointSup = this.preDataPointSup;
        }
    }

    public void compileBezierPoints(JsonObject jsonObject) {
        if (interpolation != InterpolationType.BEZIER) return;
        if (jsonObject.has("bezier_left_time") &&
            jsonObject.has("bezier_left_value")) {
            JsonArray leftTime = jsonObject.getAsJsonArray("bezier_left_time");
            JsonArray leftValue = jsonObject.getAsJsonArray("bezier_left_value");
            Supplier<Vector3f> leftTimeSup = compileSinglePoint(leftTime);
            Supplier<Vector3f> leftValueSup = compileSinglePoint(leftValue);
            this.bezierLeftSup = () -> Pair.of(
                    leftTimeSup.get(),
                    leftValueSup.get()
            );
        }
        if (jsonObject.has("bezier_right_time") &&
            jsonObject.has("bezier_right_value")) {
            JsonArray rightTime = jsonObject.getAsJsonArray("bezier_right_time");
            JsonArray rightValue = jsonObject.getAsJsonArray("bezier_right_value");
            Supplier<Vector3f> rightTimeSup = compileSinglePoint(rightTime);
            Supplier<Vector3f> rightValueSup = compileSinglePoint(rightValue);
            this.bezierLeftSup = () -> Pair.of(
                    rightTimeSup.get(),
                    rightValueSup.get()
            );
        }
    }

    public Supplier<Vector3f> compileSinglePoint(JsonArray array) {
        final String x = array.get(0).getAsString();
        final String y = array.get(1).getAsString();
        final String z = array.get(2).getAsString();
        return compileSinglePoint(x, y, z);
    }

    public Supplier<Vector3f> compileSinglePoint(String x, String y, String z) {
        final Supplier<Float> xSup = compileSingleData(anim.getNamespace(), x);
        final Supplier<Float> ySup = compileSingleData(anim.getNamespace(), y);
        final Supplier<Float> zSup = compileSingleData(anim.getNamespace(), z);
        return () -> new Vector3f(xSup.get() * ((channel == Channel.POSITION) ? 1/16f : 1f),
                ySup.get() * ((channel == Channel.POSITION) ? 1/16f : 1f),
                zSup.get()  * ((channel == Channel.POSITION) ? 1/16f : 1f));
    }

    public Supplier<Vector3f> compileSinglePoint(JsonObject point) {
        String x = point.get("x").getAsString();
        String y = point.get("y").getAsString();
        String z = point.get("z").getAsString();
        return compileSinglePoint(x, y, z);
    }

    public static Supplier<Float> compileSingleData(Namespace namespace, String s) {
        if (s.contains("\n")) s = s.substring(0, s.indexOf("\n"));
        s = s.replace(" ", "");
        final String str = s;
        if (isDirectFloat(s)) {
            return () -> Float.parseFloat(str);
        } else {
            try {
                Formula formula = namespace.decodeFormula(str);
                return () -> {
                    float result = formula.getResult();
                    if (Float.isNaN(result)) return 0f;
                    return result;
                };
            } catch (Exception e) {
                KasugaLib.MAIN_LOGGER.error("Failed to compile animation data", e);
                return () -> 0f;
            }
        }
    }

    public static boolean isDirectFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean hasBezierData() {
        return interpolation == InterpolationType.BEZIER;
    }

    public boolean hasBezierLeft() {
        return hasBezierData() && bezierLeftSup != null;
    }

    public boolean hasBezierRight() {
        return hasBezierData() && bezierRightSup != null;
    }

    public boolean isUninterrupted() {
        if (preDataPointSup == postDataPointSup) return true;
        return preDataPointSup.get().equals(postDataPointSup.get());
    }
}
