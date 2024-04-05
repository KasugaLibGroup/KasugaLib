package kasuga.lib.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.ArrayList;
import java.util.List;

public class PoseContext {
    private final ArrayList<Action> list;
    private boolean lock, autoClear = true;
    protected PoseContext() {
        list = new ArrayList<>();
    }

    protected PoseContext(Action... acts) {
        this();
        list.addAll(List.of(acts));
    }

    public static PoseContext of(Action... acts) {
        return new PoseContext(acts);
    }

    public static PoseContext of() {
        return new PoseContext();
    }

    public PoseContext addAct(Action act) {
        this.list.add(act);
        return this;
    }

    public PoseContext addAct(int index, Action act) {
        this.list.add(index, act);
        return this;
    }

    public void rotateX(float x_rotation) {
        addAct(stack -> stack.mulPose(Axis.XP.rotationDegrees(x_rotation)));
    }

    public void rotateY(float y_rotation) {
        addAct(stack -> stack.mulPose(Axis.YP.rotationDegrees(y_rotation)));
    }

    public void rotateZ(float z_rotation) {
        addAct(stack -> stack.mulPose(Axis.ZP.rotationDegrees(z_rotation)));
    }

    public void translate(double x, double y, double z) {
        addAct(stack -> stack.translate(x, y, z));
    }

    public void scale(float x, float y, float z) {
        addAct(stack -> stack.scale(x, y, z));
    }

    public void setAutoClear(boolean autoClear) {
        this.autoClear = autoClear;
    }

    public boolean isAutoClear() {
        return autoClear;
    }

    public void apply(PoseStack stack) {
        if(lock) return;
        for(Action act : list) {
            act.action(stack);
        }
        if(autoClear) list.clear();
    }

    public void setLock(boolean locked) {
        this.lock = locked;
    }

    public boolean isLocked() {
        return lock;
    }

    public interface Action {
        void action(PoseStack stack);
    }

    public enum ActionType {
        TRANSLATE, X_ROT, Y_ROT, Z_ROT, SCALE, POINT_TO;

        @Override
        public String toString() {
            return switch (this) {
                case TRANSLATE -> "translate";
                case X_ROT -> "x_rot";
                case Y_ROT -> "y_rot";
                case Z_ROT -> "z_rot";
                case SCALE -> "scale";
                case POINT_TO -> "point_to";
            };
        }

        public static ActionType fromString(String codec) {
            return switch (codec) {
                case "translate" -> TRANSLATE;
                case "x_rot" -> X_ROT;
                case "y_rot" -> Y_ROT;
                case "z_rot" -> Z_ROT;
                case "scale" -> SCALE;
                case "point_to" -> POINT_TO;
                default -> throw new IllegalStateException("Unexpected value: " + codec);
            };
        }
    }
}
