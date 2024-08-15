package kasuga.lib.core.client.frontend.gui.layout.yoga.api;

import static org.lwjgl.util.yoga.Yoga.*;

public enum YogaDirection {
    LTR(YGDirectionLTR),
    RTL(YGDirectionRTL),
    INHERIT(YGDirectionInherit)
    ;

    private final int value;

    YogaDirection(int value) {
        this.value = value;
    }

    int getValue(){
        return value;
    }
}
