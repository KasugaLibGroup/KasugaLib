package kasuga.lib.core.client.frontend.gui.styles.layout;


public enum AlignType {
    AUTO("auto"),
    BASELINE("baseline"),
    CENTER("center"),
    FLEX_END("flex_end"),
    FLEX_START("flex_start"),
    SPACE_AROUND("space_around"),
    SPACE_BETWEEN("space_between"),
    STRETCH("stretch"),
    INVALID("invalid")
    ;
    AlignType(String type) {}

    public static AlignType fromString(String positionType){
        return switch (positionType.toLowerCase()){
            case "auto" -> AUTO;
            case "baseline" -> BASELINE;
            case "center" -> CENTER;
            case "flex_end" -> FLEX_END;
            case "flex_start" -> FLEX_START;
            case "space_around" -> SPACE_AROUND;
            case "space_between" -> SPACE_BETWEEN;
            case "stretch" -> STRETCH;
            default -> INVALID;
        };
    }

    @Override
    public String toString() {
        return switch (this){
            case AUTO -> "auto";
            case BASELINE -> "baseline";
            case CENTER -> "center";
            case FLEX_END -> "flex_end";
            case FLEX_START -> "flex_start";
            case SPACE_AROUND -> "space_around";
            case SPACE_BETWEEN -> "space_between";
            case STRETCH -> "stretch";
            case INVALID -> "invalid";
        };
    }
}