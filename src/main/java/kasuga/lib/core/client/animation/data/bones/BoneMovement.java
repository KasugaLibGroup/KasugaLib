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
import kasuga.lib.core.client.animation.data.Animation;
import kasuga.lib.core.client.animation.infrastructure.AnimAssignable;
import kasuga.lib.core.client.animation.infrastructure.AnimationElement;
import kasuga.lib.core.client.animation.infrastructure.Condition;
import kasuga.lib.core.client.animation.infrastructure.IAnchor;
import kasuga.lib.core.client.render.PoseContext;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;


@OnlyIn(Dist.CLIENT)
public class BoneMovement extends AnimationElement implements AnimAssignable, IAnchor {
    String bone;
    private final Namespace namespace;
    private AnchorContext context = ZERO;
    private AnchorInvoker invoker = null;
    private final ArrayList<Pair<Formula, PoseContext.Action>> actions;
    private final ArrayList<Pair<Formula, PoseContext.ActionType>> types;
    private final Animation animation;
    private IAnchor parent = null;
    @Nonnull
    Condition condition, shouldDisplay;
    public BoneMovement(String key, String bone, Namespace namespace, Animation animation) {
        super(key);
        this.bone = bone;
        this.animation = animation;
        this.namespace = namespace;
        condition = Condition.defaultFalse(key, namespace);
        shouldDisplay = Condition.defaultTrue(key, namespace);
        this.actions = new ArrayList<>();
        this.types = new ArrayList<>();
    }

    public BoneMovement(String key, String bone, String code, Namespace namespace, Animation animation) {
        this(key, bone, namespace, animation);
        decodeFormula(code);
    }

    public void decodeFormula(String code) {
        Formula formula = namespace.decodeFormula(code);
        if(formula instanceof Function f) {
            String codec = f.getCodec();
            PoseContext.Action action = null;
            switch (codec) {
                case "translate" -> {if (f.paramCount() == 1) {
                        action = translate(animation.getAsAnchor(f.getParams().get(0).toString()));
                        types.add(Pair.of(f, PoseContext.ActionType.TRANSLATE));
                    } else if (f.paramCount() >= 3) {
                        action = translate(f.getParams().get(0), f.getParams().get(1), f.getParams().get(2));
                        types.add(Pair.of(f, PoseContext.ActionType.TRANSLATE));
                }}
                case "x_rot" -> {if(f.paramCount() >= 1) {
                        action = xRot(f.getParams().get(0));
                        types.add(Pair.of(f, PoseContext.ActionType.X_ROT));
                }}
                case "y_rot" -> {if(f.paramCount() >= 1) {
                        action = yRot(f.getParams().get(0));
                        types.add(Pair.of(f, PoseContext.ActionType.Y_ROT));
                }}
                case "z_rot" -> {if(f.paramCount() >= 1) {
                    action = zRot(f.getParams().get(0));
                    types.add(Pair.of(f, PoseContext.ActionType.Z_ROT));
                }}
                case "x_rot_rad" -> {if(f.paramCount() >= 1) {
                    action = xRotRad(f.getParams().get(0));
                    types.add(Pair.of(f, PoseContext.ActionType.X_ROT));
                }}
                case "y_rot_rad" -> {if(f.paramCount() >= 1) {
                    action = yRotRad(f.getParams().get(0));
                    types.add(Pair.of(f, PoseContext.ActionType.Y_ROT));
                }}
                case "z_rot_rad" -> {if(f.paramCount() >= 1) {
                    action = zRotRad(f.getParams().get(0));
                    types.add(Pair.of(f, PoseContext.ActionType.Z_ROT));
                }}
                case "scale" -> {if(f.paramCount() == 1) {
                    action = scale(animation.getAsAnchor(f.getParams().get(0).toString()));
                    types.add(Pair.of(f, PoseContext.ActionType.SCALE));
                    } else if (f.paramCount() >= 3) {
                    action = scale(f.getParams().get(0), f.getParams().get(1), f.getParams().get(2));
                    types.add(Pair.of(f, PoseContext.ActionType.SCALE));
                }}
                case "point_to" -> {if(f.paramCount() == 1) {
                    action = pointTo(animation.getAsAnchor(f.getParams().get(0).toString()));
                    types.add(Pair.of(f, PoseContext.ActionType.POINT_TO));
                    } else if (f.paramCount() >= 3) {
                    action = pointTo(f.getParams().get(0), f.getParams().get(1), f.getParams().get(2));
                    types.add(Pair.of(f, PoseContext.ActionType.POINT_TO));
                }}
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

    public void setParent(IAnchor parent) {
        this.parent = parent;
    }

    public ArrayList<Pair<Formula, PoseContext.ActionType>> getMoves() {
        return types;
    }

    @Override
    public Animation getAnimation() {
        return animation;
    }

    @Override
    public void updateVars() {
        namespace.assign(key() + ".x", context.x());
        namespace.assign(key() + ".y", context.y());
        namespace.assign(key() + ".z", context.z());
        namespace.assign(key() + ".x_rot", context.x_rot());
        namespace.assign(key() + ".y_rot", context.y_rot());
        namespace.assign(key() + ".z_rot", context.z_rot());
        namespace.assign(key() + ".x_scale", context.x_scale());
        namespace.assign(key() + ".y_scale", context.y_scale());
        namespace.assign(key() + ".z_scale", context.z_scale());
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

    public static BoneMovement decode(String bone, Namespace namespace, Animation animation, JsonObject object) {
        LogicalData render = LogicalBool.defaultTrue();
        LogicalData condition = LogicalBool.defaultTrue();
        BoneMovement movement = new BoneMovement("bone", bone, namespace, animation);
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
        if (bone.contains(".")) {
            String[] path = bone.split("\\.");
            if (path.length > 1) {
                String p = path[path.length - 2];
                movement.invoker = () -> {
                    if (animation.containsAnchor(p))
                        movement.setParent(animation.getAsAnchor(p));
                    else if (animation.containsAsAnchor("this"))
                        movement.setParent(animation.getAsAnchor("this"));
                };
            }
        } else if (!bone.equals("this")) {
            movement.invoker = () -> {
                if (animation.containsAsAnchor("this"))
                    movement.setParent(animation.getAsAnchor("this"));
            };
        }
        return movement;
    }

    public void invoke() {
        if (invoker == null) return;
        invoker.invoke();
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

    public IAnchor getParent() {
        return parent;
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

    public PoseContext.Action translate(IAnchor anchor) {
        return (pose) -> {
            IAnchor.AnchorContext context = anchor.getMovement();
            pose.translate(context.x(), context.y(), context.z());
        };
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

    public PoseContext.Action scale(Formula fx, Formula fy, Formula fz) {
        return (pose) -> {pose.scale(fx.getResult(), fy.getResult(), fz.getResult());};
    }

    public PoseContext.Action scale(IAnchor anchor) {
        return (pose) -> {
            IAnchor.AnchorContext context = anchor.getMovement();
            IAnchor.AnchorContext context1 = getMovement();
            pose.scale(context.x_scale() / context1.x_scale(),
                    context.y_scale() / context1.x_scale(),
                    context.z_scale() / context1.z_scale());
        };
    }

    public PoseContext.Action pointTo(IAnchor anchor) {
        return (pose) -> {
            IAnchor.AnchorContext context1 = getMovement();
            IAnchor.AnchorContext context2 = anchor.getMovement();
            pose.mulPose(Vector3f.XP.rotationDegrees(context2.x_rot() - context1.x_rot()));
            pose.mulPose(Vector3f.YP.rotationDegrees(context2.y_rot() - context1.y_rot()));
            pose.mulPose(Vector3f.ZP.rotationDegrees(context2.z_rot() - context1.z_rot()));
        };
    }

    public PoseContext.Action pointTo(Formula px, Formula py, Formula pz) {
        return (pose) -> {
            pose.mulPose(Vector3f.XP.rotationDegrees(px.getResult()));
            pose.mulPose(Vector3f.YP.rotationDegrees(py.getResult()));
            pose.mulPose(Vector3f.ZP.rotationDegrees(pz.getResult()));
        };
    }

    @Override
    public AnchorContext getMovement() {
        this.context = innerMove();
        return context;
    }

    public static float cos(float deg) {
        return (float) Math.cos(Math.toRadians(deg));
    }

    public static float sin(float deg) {
        return (float) Math.sin(Math.toRadians(deg));
    }
}
