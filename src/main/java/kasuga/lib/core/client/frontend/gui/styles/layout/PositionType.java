package kasuga.lib.core.client.frontend.gui.styles.layout;


public enum PositionType {
    STATIC("static"),
    ABSOLUTE("absolute"),
    RELATIVE("relative"),
    INVALID("invalid")
    ;


    PositionType(String type) {}

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