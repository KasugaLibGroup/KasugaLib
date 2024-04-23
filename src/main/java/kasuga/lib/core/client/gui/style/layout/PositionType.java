package kasuga.lib.core.client.gui.style.layout;

import kasuga.lib.core.client.gui.layout.yoga.YogaPositionType;

public enum PositionType {
    STATIC("static",YogaPositionType.STATIC),
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
            case "static"->STATIC;
            case "absolute"->ABSOLUTE;
            case "relative"->RELATIVE;
            default -> INVALID;
        };
    }

    @Override
    public String toString() {
        return switch (this){
            case STATIC -> "static";
            case ABSOLUTE -> "absolute";
            case RELATIVE -> "relative";
            case INVALID -> "invalid";
        };
    }
}
