package kasuga.lib.core.client.gui.layout.yoga;
import static org.lwjgl.util.yoga.Yoga.*;

public enum YogaJustify {
    FLEX_START(YGJustifyFlexStart),
    CENTER(YGJustifyCenter),
    FLEX_END(YGJustifyFlexEnd),
    SPACE_BETWEEN(YGJustifySpaceBetween),
    SPACE_AROUND(YGJustifySpaceAround),
    SPACE_EVENLY(YGJustifySpaceEvenly)
    ;
    private final int value;

    YogaJustify(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
