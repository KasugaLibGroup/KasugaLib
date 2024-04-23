package kasuga.lib.core.client.gui.style.layout;

import kasuga.lib.core.client.gui.layout.yoga.YogaDisplay;
import kasuga.lib.core.client.gui.layout.yoga.YogaPositionType;

public enum DisplayType {
    FLEX("flex", YogaDisplay.FLEX),
    UNSET("unset",YogaDisplay.NONE),
    INVALID("invalid",null)
    ;

    private final YogaDisplay value;

    DisplayType(String type, YogaDisplay yogaDisplayType) {
        this.value = yogaDisplayType;
    }

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

    public YogaDisplay getValue(){
        return value;
    }
}
