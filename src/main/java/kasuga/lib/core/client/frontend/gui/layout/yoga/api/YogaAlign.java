package kasuga.lib.core.client.frontend.gui.layout.yoga.api;
import static org.lwjgl.util.yoga.Yoga.*;

public enum YogaAlign {

    AUTO(YGAlignAuto),
    FLEX_START(YGAlignFlexStart),
    CENTER(YGAlignCenter),
    FLEX_END(YGAlignFlexEnd),
    STRETCH(YGAlignStretch),
    BASELINE(YGAlignBaseline),
    SPACE_BETWEEN(YGAlignSpaceBetween),
    SPACE_AROUND(YGAlignSpaceAround)
    ;
    private final int value;

    YogaAlign(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
