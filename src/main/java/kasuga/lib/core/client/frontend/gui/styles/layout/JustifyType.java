package kasuga.lib.core.client.frontend.gui.styles.layout;


public enum JustifyType {
    FLEX_START("flex_start"),
    CENTER("center"),
    FLEX_END("flex_end"),
    SPACE_BETWEEN("space_between"),
    SPACE_AROUND("space_around"),
    SPACE_EVENLY("space_evenly"),
    INVALID("invalid"),
    ;


    JustifyType(String type) {}

    public static JustifyType fromString(String positionType){
        return switch (positionType.toLowerCase()){
            case "flex_start" -> FLEX_START;
            case "center" -> CENTER;
            case "flex_end" -> FLEX_END;
            case "space_between" -> SPACE_BETWEEN;
            case "space_around" -> SPACE_AROUND;
            case "space_evenly" -> SPACE_EVENLY;
            default -> INVALID;
        };
    }

    @Override
    public String toString() {
        return switch (this){
            case FLEX_START -> "flex_start";
            case CENTER -> "center";
            case FLEX_END -> "flex_end";
            case SPACE_BETWEEN -> "space_between";
            case SPACE_AROUND -> "space_around";
            case SPACE_EVENLY -> "space_evenly";
            case INVALID -> "invalid";
        };
    }
}