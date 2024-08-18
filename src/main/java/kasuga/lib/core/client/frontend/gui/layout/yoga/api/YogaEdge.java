package kasuga.lib.core.client.frontend.gui.layout.yoga.api;

import static org.lwjgl.util.yoga.Yoga.*;

public enum YogaEdge {
    LEFT(YGEdgeLeft),
    TOP(YGEdgeTop),
    RIGHT(YGEdgeRight),
    BOTTOM(YGEdgeBottom),
    START(YGEdgeStart),
    END(YGEdgeEnd),
    HORIZONTAL(YGEdgeHorizontal),
    VERTICAL(YGEdgeVertical),
    ALL(YGEdgeAll)
    ;

    private final int value;

    YogaEdge(int value) {
        this.value = value;
    }

    int getValue(){
        return value;
    }
}
