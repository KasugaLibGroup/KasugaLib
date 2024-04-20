package kasuga.lib.core.client.gui.layout.yoga;
import static org.lwjgl.util.yoga.Yoga.*;

public enum YogaPositionType {
    STATIC(YGPositionTypeStatic),
    ABSOLUTE(YGPositionTypeAbsolute),
    RELATIVE(YGPositionTypeRelative)
    ;

    private final int value;

    YogaPositionType(int value) {
        this.value = value;
    }

    int getValue(){
        return value;
    }
}
