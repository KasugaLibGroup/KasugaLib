package kasuga.lib.core.client.render.font;

import kasuga.lib.core.client.render.texture.Vec2f;
import net.minecraft.util.StringRepresentable;

public enum PivotPosition {

    LEFT_TOP, MID_TOP, RIGHT_TOP,
    LEFT_MID, CENTER, RIGHT_MID,
    LEFT_DOWN, MID_DOWN, RIGHT_DOWN, CUSTOM;

    public static Vec2f getPivot(PivotPosition position) {
        return switch (position) {
            case LEFT_TOP -> Vec2f.ZERO;
            case MID_TOP -> new Vec2f(.5f, 0);
            case RIGHT_TOP -> new Vec2f(1, 0);
            case LEFT_MID -> new Vec2f(0, .5f);
            case CENTER -> new Vec2f(.5f, .5f);
            case RIGHT_MID -> new Vec2f(1, .5f);
            case LEFT_DOWN -> new Vec2f(0, 1);
            case MID_DOWN -> new Vec2f(.5f, 1);
            case RIGHT_DOWN -> new Vec2f(1f, 1f);
            default -> null;
        };
    }
}
