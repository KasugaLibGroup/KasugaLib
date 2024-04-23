package kasuga.lib.core.client.gui.style.layout;

import kasuga.lib.core.client.gui.layout.yoga.YogaDirection;
import kasuga.lib.core.client.gui.layout.yoga.YogaDisplay;
import kasuga.lib.core.client.gui.layout.yoga.YogaFlexDirection;
import kasuga.lib.core.client.gui.layout.yoga.YogaPositionType;

public enum FlexDirection {
    ROW("row", YogaFlexDirection.ROW),
    COLUMN("column", YogaFlexDirection.COLUMN),

    ROW_REVERSE("row_reverse", YogaFlexDirection.ROW_REVERSE),
    COLUMN_REVERSE("column_reverse", YogaFlexDirection.COLUMN_REVERSE),
    INVALID("column", null)
    ;

    private final YogaFlexDirection value;

    FlexDirection(String type, YogaFlexDirection flexDirection) {
        this.value = flexDirection;
    }

    public static FlexDirection fromString(String flexDirection){
        return switch (flexDirection.toLowerCase()){
            case "row" -> ROW;
            case "column" -> COLUMN;
            case "row_reverse" -> ROW_REVERSE;
            case "column_reverse" -> COLUMN_REVERSE;
            case "invalid" -> INVALID;
            default -> INVALID;
        };
    }

    @Override
    public String toString() {
        return switch (this){
            case ROW -> "row";
            case COLUMN -> "column";
            case ROW_REVERSE -> "row_reverse";
            case COLUMN_REVERSE -> "column_reverse";
            case INVALID -> "invalid";
        };
    }

    public YogaFlexDirection getValue(){
        return value;
    }
}
