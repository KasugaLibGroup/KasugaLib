package kasuga.lib.core.client.frontend.gui.styles.layout;


import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaAlign;

public enum AlignType {
    AUTO("auto", YogaAlign.AUTO),
    BASELINE("baseline", YogaAlign.BASELINE),
    CENTER("center", YogaAlign.CENTER),
    FLEX_END("flex_end", YogaAlign.FLEX_END),
    FLEX_START("flex_start", YogaAlign.FLEX_START),
    SPACE_AROUND("space_around", YogaAlign.SPACE_AROUND),
    SPACE_BETWEEN("space_between", YogaAlign.SPACE_BETWEEN),
    STRETCH("stretch", YogaAlign.STRETCH),
    INVALID("invalid",null)
    ;

    private final YogaAlign value;

    AlignType(String type, YogaAlign alignType) {
        this.value = alignType;
    }

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

    public YogaAlign getValue() {
        return value;
    }
}