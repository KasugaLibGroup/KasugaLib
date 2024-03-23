package kasuga.lib.core.client.animation.data.bones;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import interpreter.compute.data.Namespace;
import interpreter.compute.data.functions.Function;
import interpreter.compute.infrastructure.Formula;
import interpreter.logic.data.LogicalBool;
import interpreter.logic.infrastructure.LogicalData;
import kasuga.lib.core.client.animation.infrastructure.AnimAssignable;
import kasuga.lib.core.client.animation.infrastructure.AnimationElement;
import kasuga.lib.core.client.animation.infrastructure.Condition;
import kasuga.lib.core.client.render.PoseContext;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;


@OnlyIn(Dist.CLIENT)
public class BoneMovement extends AnimationElement implements AnimAssignable {
    String bone;
    private final Namespace namespace;
    private final ArrayList<Pair<Formula, PoseContext.Action>> actions;
    @Nonnull
    Condition condition, shouldDisplay;
    public BoneMovement(String key, String bone, Namespace namespace) {
        super(key);
        this.bone = bone;
        this.namespace = namespace;
        condition = Condition.defaultFalse(key, namespace);
        shouldDisplay = Condition.defaultTrue(key, namespace);
        this.actions = new ArrayList<>();
    }

    public BoneMovement(String key, String bone, String code, Namespace namespace) {
        this(key, bone, namespace);
        decodeFormula(code);
    }

    public void decodeFormula(String code) {
        Formula formula = namespace.decodeFormula(code);
        if(formula instanceof Function f) {
            String codec = f.getCodec();
            PoseContext.Action action = null;
            switch (codec) {
                case "translate" -> {
                    if (f.paramCount() >= 3) {action = translate(f.getParams().get(0), f.getParams().get(1), f.getParams().get(2));}
                }
                case "x_rot" -> {if(f.paramCount() >= 1) {action = xRot(f.getParams().get(0));}}
                case "y_rot" -> {if(f.paramCount() >= 1) {action = yRot(f.getParams().get(0));}}
                case "z_rot" -> {if(f.paramCount() >= 1) {action = zRot(f.getParams().get(0));}}
                case "x_rot_rad" -> {if(f.paramCount() >= 1) {action = xRotRad(f.getParams().get(0));}}
                case "y_rot_rad" -> {if(f.paramCount() >= 1) {action = yRotRad(f.getParams().get(0));}}
                case "z_rot_rad" -> {if(f.paramCount() >= 1) {action = zRotRad(f.getParams().get(0));}}
            }
            if(action != null) {
                actions.add(Pair.of(f, action));
            }
        }
    }

    public @Nullable Pair<Formula, PoseContext.Action> getAction(Formula formula) {
        for(Pair<Formula, PoseContext.Action> pair : actions) {
            if (pair.getFirst().equals(formula)) {return pair;}
        }
        return null;
    }

    public PoseContext.Action[] getAllActions() {
        PoseContext.Action[] result = new PoseContext.Action[this.actions.size()];
        int counter = 0;
        for(Pair<Formula, PoseContext.Action> pair : actions) {
            result[counter] = pair.getSecond();
            counter++;
        }
        return result;
    }

    public Pair<Formula, PoseContext.Action> getAction(int index) {
        return actions.get(index);
    }

    public void setCondition(@Nonnull String logical) {
        this.condition.fromString(Objects.requireNonNull(logical));
    }

    public void setDisplayCondition(@Nonnull String condition) {
        this.shouldDisplay.fromString(Objects.requireNonNull(condition));
    }


    public boolean isAssignable() {
        return isValid() && (namespace.hasInstance() || condition.isAssignable() || shouldDisplay.isAssignable());
    }

    @Override
    public void init() {}

    public static BoneMovement decode(String bone, Namespace namespace, JsonObject object) {
        LogicalData render = LogicalBool.defaultTrue();
        LogicalData condition = LogicalBool.defaultTrue();
        BoneMovement movement = new BoneMovement("bone", bone, namespace);
        if(object.has("render")) {
            render = movement.getCondition(object.get("render"));
        }
        if(object.has("condition")) {
            condition = movement.getCondition(object.get("condition"));
        }
        JsonArray functions = object.getAsJsonArray("action");
        for (JsonElement element : functions) {movement.decodeFormula(element.getAsString());}
        movement.condition = new Condition("condition", namespace, condition);
        movement.shouldDisplay = new Condition("render", namespace, render);
        return movement;
    }

    private LogicalData getCondition(JsonElement element) {
        String def = element.getAsString();
        if(def.equals("true") || def.equals("false")) {
            def = def.equals("true") ? "True" : "False";
        }
        return namespace.decodeLogical(def);
    }

    public void action(PoseStack pose) {
        if(!condition.result()) return;
        for(Pair<Formula, PoseContext.Action> pair : actions) {
            pair.getSecond().action(pose);
        }
    }

    public boolean shouldDisplay() {
        return shouldDisplay.result();
    }

    @Override
    public Namespace getNamespace() {
        return namespace;
    }


    public void assign(String codec, float value) {
        namespace.assign(codec, value);
    }

    public ArrayList<Pair<Formula, PoseContext.Action>> getAction() {
        return actions;
    }

    public boolean shouldAct() {
        return isValid() && condition.result();
    }

    public boolean isValid() {
        return !actions.isEmpty();
    }

    public PoseContext.Action translate(Formula fx, Formula fy, Formula fz){
        return  (pose) -> {pose.translate(fx.getResult(), fy.getResult(), fz.getResult());};
    }
    public PoseContext.Action xRot(Formula fx) {
        return  (pose) -> {pose.mulPose(Axis.XP.rotationDegrees(fx.getResult()));};
    }
    public PoseContext.Action yRot(Formula fy) {
        return  (pose) -> {pose.mulPose(Axis.YP.rotationDegrees(fy.getResult()));};
    }
    public PoseContext.Action zRot(Formula fz) {
        return  (pose) -> {pose.mulPose(Axis.ZP.rotationDegrees(fz.getResult()));};
    }

    public PoseContext.Action xRotRad(Formula fx) {
        return  (pose) -> {pose.mulPose(Axis.XP.rotation(fx.getResult()));};
    }

    public PoseContext.Action yRotRad(Formula fy) {
        return  (pose) -> {pose.mulPose(Axis.YP.rotation(fy.getResult()));};
    }

    public PoseContext.Action zRotRad(Formula fz) {
        return  (pose) -> {pose.mulPose(Axis.ZP.rotation(fz.getResult()));};
    }
}
