package kasuga.lib.core.client.frontend.gui.layout.yoga.api;

import static org.lwjgl.util.yoga.Yoga.YGNodeTypeDefault;
import static org.lwjgl.util.yoga.Yoga.YGNodeTypeText;

public enum YogaNodeType {
    DEFAULT(YGNodeTypeDefault),
    TEXT(YGNodeTypeText)
    ;

    private final int value;

    YogaNodeType(int value) {
        this.value = value;
    }

    int getValue(){
        return value;
    }
}
