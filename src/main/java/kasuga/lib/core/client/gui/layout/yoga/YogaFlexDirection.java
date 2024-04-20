package kasuga.lib.core.client.gui.layout.yoga;

import static org.lwjgl.util.yoga.Yoga.*;

public enum YogaFlexDirection {
    COLUMN(YGFlexDirectionColumn),
    ROW(YGFlexDirectionRow),
    COLUMN_REVERSE(YGFlexDirectionColumnReverse),
    ROW_REVERSE(YGFlexDirectionRowReverse),
    ;
    private final int value;

    YogaFlexDirection(int value) {
        this.value = value;
    }

    int getValue(){
        return value;
    }
}
