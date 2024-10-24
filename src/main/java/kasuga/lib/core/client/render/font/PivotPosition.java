package kasuga.lib.core.client.render.font;

import kasuga.lib.core.client.render.texture.Vec2f;

public enum PivotPosition {

    LEFT_TOP, CENTER_TOP, RIGHT_TOP,
    LEFT_MID, CENTER, RIGHT_MID,
    LEFT_DOWN, CENTER_DOWN, RIGHT_DOWN, CUSTOM;

    public static Vec2f getPivot(PivotPosition position) {
        return switch (position) {
            case LEFT_TOP -> Vec2f.ZERO;
            case CENTER_TOP -> new Vec2f(.5f, 0);
            case RIGHT_TOP -> new Vec2f(1, 0);
            case LEFT_MID -> new Vec2f(0, .5f);
            case CENTER -> new Vec2f(.5f, .5f);
            case RIGHT_MID -> new Vec2f(1, .5f);
            case LEFT_DOWN -> new Vec2f(0, 1);
            case CENTER_DOWN -> new Vec2f(.5f, 1);
            case RIGHT_DOWN -> new Vec2f(1f, 1f);
            default -> null;
        };
    }

    public static PivotPosition fromString(String piviotPositon){
        if(piviotPositon == null)
            return LEFT_TOP;
        switch (piviotPositon){
            case "leftTop":
                return LEFT_TOP;
            case "centerTop":
                return CENTER_TOP;
            case "rightTop":
                return RIGHT_TOP;
            case "leftMid":
                return LEFT_MID;
            case "center":
                return CENTER;
            case "rightMid":
                return RIGHT_MID;
            case "leftDown":
                return LEFT_DOWN;
            case "centerDown":
                return CENTER_DOWN;
            case "rightDown":
                return RIGHT_DOWN;
        }
        return LEFT_TOP;
    }
}
