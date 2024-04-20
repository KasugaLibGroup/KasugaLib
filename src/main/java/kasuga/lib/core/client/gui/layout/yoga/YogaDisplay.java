package kasuga.lib.core.client.gui.layout.yoga;

import static org.lwjgl.util.yoga.Yoga.*;

public enum YogaDisplay {
    NONE(YGDisplayNone),
    FLEX(YGDisplayFlex)
    ;

    private final int value;

    YogaDisplay(int value) {
        this.value = value;
    }

    int getValue(){
        return value;
    }
}
