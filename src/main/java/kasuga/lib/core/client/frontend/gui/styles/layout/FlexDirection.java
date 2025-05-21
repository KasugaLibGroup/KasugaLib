package kasuga.lib.core.client.frontend.gui.styles.layout;


public enum FlexDirection {
    ROW("row"),
    COLUMN("column"),

    ROW_REVERSE("row_reverse"),
    COLUMN_REVERSE("column_reverse"),
    INVALID("column")
    ;


    FlexDirection(String type) {}

    public static FlexDirection fromString(String flexDirection){
        return switch (flexDirection.toLowerCase()){
            case "row" -> ROW;
            case "column" -> COLUMN;
            case "row_reverse" -> ROW_REVERSE;
            case "column_reverse" -> COLUMN_REVERSE;
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
}