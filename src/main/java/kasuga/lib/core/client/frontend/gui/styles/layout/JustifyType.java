package kasuga.lib.core.client.frontend.gui.styles.layout;


import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaJustify;

public enum JustifyType {
    FLEX_START("flex_start", YogaJustify.FLEX_START),
    CENTER("center", YogaJustify.CENTER),
    FLEX_END("flex_end", YogaJustify.FLEX_END),
    SPACE_BETWEEN("space_between", YogaJustify.SPACE_BETWEEN),
    SPACE_AROUND("space_around", YogaJustify.SPACE_AROUND),
    SPACE_EVENLY("space_evenly", YogaJustify.SPACE_EVENLY),
    INVALID("invalid", null),
    ;

    private final YogaJustify value;

    JustifyType(String type, YogaJustify justify) {
        this.value = justify;
    }

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

    public YogaJustify getValue() {
        return value;
    }
}