package kasuga.lib.core.client.gui.layout.yoga;

import static org.lwjgl.util.yoga.Yoga.*;

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
