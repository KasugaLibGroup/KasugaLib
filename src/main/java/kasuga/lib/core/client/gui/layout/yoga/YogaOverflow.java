package kasuga.lib.core.client.gui.layout.yoga;


import static org.lwjgl.util.yoga.Yoga.*;

public enum YogaOverflow {
    HIDDEN(YGOverflowHidden),
    SCROLL(YGOverflowScroll),
    VISIBLE(YGOverflowVisible)
    ;

    private final int value;

    YogaOverflow(int value) {
        this.value = value;
    }

    int getValue(){
        return value;
    }
}
