package kasuga.lib.core.client.frontend.gui.layout.yoga.api;
import static org.lwjgl.util.yoga.Yoga.*;

public enum YogaPositionType {
    ABSOLUTE(YGPositionTypeAbsolute),
    RELATIVE(YGPositionTypeRelative),
    ;

    private final int value;

    YogaPositionType(int value) {
        this.value = value;
    }


    public int getValue(){
        return value;
    }
}
