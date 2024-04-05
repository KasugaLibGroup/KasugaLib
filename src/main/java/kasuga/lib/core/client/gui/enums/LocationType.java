package kasuga.lib.core.client.gui.enums;

public enum LocationType {
    ABSOLUTE,
    RELATIVE,
    INVALID;

    @Override
    public String toString() {
        return switch (this) {
            case ABSOLUTE -> "absolute";
            case RELATIVE -> "relative";
            case INVALID -> "invalid";
        };
    }

    public static LocationType fromString(String input) {
        return switch (input) {
            case "absolute" -> ABSOLUTE;
            case "relative" -> RELATIVE;
            default -> INVALID;
        };
    }
}
