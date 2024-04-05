package kasuga.lib.core.client.animation.data.anchor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import interpreter.compute.data.Namespace;
import interpreter.compute.data.functions.Function;
import interpreter.compute.infrastructure.Formula;
import kasuga.lib.core.client.animation.data.Animation;
import kasuga.lib.core.client.animation.infrastructure.AnimAssignable;
import kasuga.lib.core.client.animation.infrastructure.AnimationElement;
import kasuga.lib.core.client.animation.infrastructure.IAnchor;
import kasuga.lib.core.client.render.PoseContext;
import kasuga.lib.core.util.data_type.Pair;

import java.util.ArrayList;

public class Anchor extends AnimationElement implements AnimAssignable, IAnchor {
    private final Namespace namespace;
    private AnchorInvoker invoker = null;
    private IAnchor.AnchorContext anchorContext = ZERO;
    private final Animation animation;
    private IAnchor parent = null;
    private final ArrayList<Pair<Formula, PoseContext.ActionType>> actions;
    public Anchor(Animation animation, Namespace namespace, String key) {
        super(key);
        this.namespace = namespace;
        this.animation = animation;
        actions = new ArrayList<>();
        updateVars();
    }

    public static Anchor decode(String key, Namespace namespace, Animation animation, JsonObject json) {
        Anchor result = new Anchor(animation, namespace, key);
        if (json.has("parent"))
            result.invoker = () -> result.setParent(json.get("parent").getAsString());
        if (json.has("action")) {
            JsonArray actions = json.getAsJsonArray("action");
            for (JsonElement str : actions) result.decodeFormula(str.getAsString());
        }
        result.init();
        return result;
    }

    public void invoke() {
        if (invoker == null) return;
        invoker.invoke();
    }

    public void setParent(String parentCodec) {
        if (animation.containsBoneMovement(parentCodec)) {
            this.parent = animation.getBonesGroup().getMovement(parentCodec);
        } else if (animation.containsAnchor(parentCodec)) {
            this.parent = animation.getAnchors().getAnchor(parentCodec);
        }
    }

    public void decodeFormula(String code) {
        Formula formula = namespace.decodeFormula(code);
        if(formula instanceof Function f) {
            String codec = f.getCodec();
            if (f.paramCount() >= 1) {
                switch (codec) {
                    case "translate" -> actions.add(Pair.of(f, PoseContext.ActionType.TRANSLATE));
                    case "x_rot", "x_rot_rad" -> actions.add(Pair.of(f, PoseContext.ActionType.X_ROT));
                    case "y_rot", "y_rot_rad" -> actions.add(Pair.of(f, PoseContext.ActionType.Y_ROT));
                    case "z_rot", "z_rot_rad" -> actions.add(Pair.of(f, PoseContext.ActionType.Z_ROT));
                    case "scale" -> actions.add(Pair.of(f, PoseContext.ActionType.SCALE));
                }
            }
        }
    }

    public void clearFormula() {
        this.actions.clear();
    }

    public void removeFormula(int index) {
        actions.remove(index);
    }

    public float getX() {
        return anchorContext.x();
    }

    public float getY() {
        return anchorContext.y();
    }

    public float getZ() {
        return anchorContext.z();
    }

    public float getXRot() {
        return anchorContext.x_rot();
    }

    public float getYRot() {
        return anchorContext.y_rot();
    }

    public float getZRot() {
        return anchorContext.z_rot();
    }

    public float getXScale() {
        return anchorContext.x_scale();
    }

    public float getYScale() {
        return anchorContext.y_scale();
    }

    public float getZScale() {
        return anchorContext.z_scale();
    }

    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public Namespace getNamespace() {
        return namespace;
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void assign(String codec, float value) {
        namespace.assign(codec, value);
    }

    @Override
    public void init() {
        updateVars();
    }

    public void updateVars() {
        namespace.assign(key() + ".x", getX());
        namespace.assign(key() + ".y", getY());
        namespace.assign(key() + ".z", getZ());
        namespace.assign(key() + ".x_rot", getXRot());
        namespace.assign(key() + ".y_rot", getYRot());
        namespace.assign(key() + ".z_rot", getZRot());
        namespace.assign(key() + ".x_scale", getXScale());
        namespace.assign(key() + ".y_scale", getYScale());
        namespace.assign(key() + ".z_scale", getZScale());
    }

    @Override
    public AnchorContext getMovement() {
        return innerMove();
    }

    @Override
    public ArrayList<Pair<Formula, PoseContext.ActionType>> getMoves() {
        return actions;
    }

    @Override
    public Animation getAnimation() {
        return animation;
    }

    @Override
    public IAnchor getParent() {
        return parent;
    }
}
