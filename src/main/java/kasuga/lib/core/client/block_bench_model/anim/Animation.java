package kasuga.lib.core.client.block_bench_model.anim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import interpreter.Code;
import interpreter.compute.data.Namespace;
import interpreter.compute.infrastructure.Formula;
import kasuga.lib.core.client.block_bench_model.anim_model.AnimFunctions;
import kasuga.lib.core.client.block_bench_model.json_data.BlockBenchFile;
import kasuga.lib.core.util.data_type.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Getter
public class Animation {

    private final BlockBenchFile file;
    private final UUID uuid;
    private final String name;
    private final LoopMode loopMode;
    private final boolean override, selected;
    private final float length;
    private final int snapping;
    private final String animTimeUpdate, blendWeight,
            startDelay, loopDelay;
    private String animVarPlaceholders;
    private final Namespace namespace;

    private final HashMap<UUID, Animator> animators;
    private final List<Pair<String, Formula>> formulas;
    private final Supplier<Float> startDelaySup, loopDelaySup;

    public Animation(BlockBenchFile file, JsonObject json) {
        this(new Namespace(AnimFunctions.animRoot), file, json);
    }

    public Animation(Namespace namespace, BlockBenchFile file, JsonObject json) {
        this.namespace = namespace;
        formulas = new ArrayList<>();
        this.file = file;
        uuid = UUID.fromString(json.get("uuid").getAsString());
        name = json.get("name").getAsString();
        loopMode = LoopMode.get(json.get("loop").getAsString());
        override = json.has("override") && json.get("override").getAsBoolean();
        selected = json.has("selected") && json.get("selected").getAsBoolean();
        length = json.get("length").getAsFloat();
        snapping = json.get("snapping").getAsInt();
        animTimeUpdate = json.get("anim_time_update").getAsString();
        blendWeight = json.get("blend_weight").getAsString();
        startDelay = json.get("start_delay").getAsString();
        loopDelay = json.get("loop_delay").getAsString();
        animators = new HashMap<>();
        if (json.has("animators")) {
            JsonObject animatorObject = json.get("animators").getAsJsonObject();
            compileAnimators(animatorObject);
        }
        startDelaySup = getDataFromStr(getStartDelay());
        loopDelaySup = getDataFromStr(getLoopDelay());
    }

    public Supplier<Float> getDataFromStr(String str) {
        if (str == null || str.isBlank()) return  () -> 0f;
        if (str.contains("\n")) str = str.substring(0, str.indexOf("\n"));
        if (str.isBlank()) return  () -> 0f;
        return KeyFrame.compileSingleData(getNamespace(), str);
    }

    public void setAnimVarPlaceholders(String animVarPlaceholders) {
        if (animVarPlaceholders == null) return;
        this.animVarPlaceholders = animVarPlaceholders;
        compilePlaceHolders();
        runEvaluation();
    }

    private void compilePlaceHolders() {
        this.formulas.clear();
        String[] lines = animVarPlaceholders.split("\n");
        ArrayList<Pair<String, String>> cache = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
            if (line.isBlank()) continue;
            if (!line.contains("=")) continue;
            String[] parts = line.split("=");
            String key = parts[0].replace(" ", "");
            String func = parts[1].replace(" ", "");
            namespace.assign(key, 0f);
            cache.add(Pair.of(key, func));
        }
        for (Pair<String, String> entry : cache) {
            Formula formula = namespace.decodeFormula(entry.getSecond());
            formulas.add(Pair.of(entry.getFirst(), formula));
        }
    }

    public void runEvaluation() {
        for (Pair<String, Formula> pair : formulas) {
            Formula formula = pair.getSecond();
            String var = pair.getFirst();
            namespace.assign(var, formula.getResult());
        }
    }

    public void compileAnimators(JsonObject animatorObject) {
        for (Map.Entry<String, JsonElement> entry : animatorObject.entrySet()) {
            UUID id = UUID.fromString(entry.getKey());
            JsonObject animatorObj = entry.getValue().getAsJsonObject();
            String name = animatorObj.get("name").getAsString();
            String type = animatorObj.get("type").getAsString();
            JsonArray keyFrameArray = animatorObj.get("keyframes").getAsJsonArray();
            Animator animator = new Animator(this, id, name, type);
            animator.compileKeyFrames(keyFrameArray);
            this.animators.put(id, animator);
        }
    }
}
