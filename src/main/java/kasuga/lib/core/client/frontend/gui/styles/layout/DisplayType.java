package kasuga.lib.core.client.frontend.gui.styles.layout;


public enum DisplayType {
    FLEX("flex"),
    UNSET("unset"),
    INVALID("invalid")
    ;

    DisplayType(String type) {}

    public static DisplayType fromString(String positionType){
        return switch (positionType.toLowerCase()){
            case "flex"->FLEX;
            case "unset"->UNSET;
            default -> INVALID;
        };
    }

    @Override
    public String toString() {
        return switch (this){
            case FLEX -> "flex";
            case UNSET -> "unset";
            case INVALID -> "invalid";
        };
    }
}