package kasuga.lib.core.client.frontend.gui.layout.yoga.api;

import static org.lwjgl.util.yoga.Yoga.*;

public enum YogaDisplay {
    NONE(YGDisplayNone),
    FLEX(YGDisplayFlex)
    ;

    private final int value;

    YogaDisplay(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
