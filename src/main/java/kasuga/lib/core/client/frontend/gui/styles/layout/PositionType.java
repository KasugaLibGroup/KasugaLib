package kasuga.lib.core.client.frontend.gui.styles.layout;


import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaPositionType;

public enum PositionType {
    ABSOLUTE("absolute",YogaPositionType.ABSOLUTE),
    RELATIVE("relative",YogaPositionType.RELATIVE),
    INVALID("invalid",null)
    ;

    private final YogaPositionType value;

    PositionType(String type, YogaPositionType positionType) {
        this.value = positionType;
    }

    public YogaPositionType getValue(){
        return value;
    }

    public static PositionType fromString(String positionType){
        return switch (positionType.toLowerCase()){
            case "absolute"->ABSOLUTE;
            case "relative"->RELATIVE;
            default -> INVALID;
        };
    }

    @Override
    public String toString() {
        return switch (this){
            case ABSOLUTE -> "absolute";
            case RELATIVE -> "relative";
            case INVALID -> "invalid";
        };
    }
}