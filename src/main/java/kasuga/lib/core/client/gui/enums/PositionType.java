package kasuga.lib.core.client.gui.enums;

public enum PositionType {
    FIXED,
    ABSOLUTE,
    STATIC,
    INVALID;

    @Override
    public String toString() {
        return switch (this) {
            case STATIC -> "static";
            case FIXED -> "fixed";
            case ABSOLUTE -> "absolute";
            case INVALID -> "invalid";
        };
    }

    public static PositionType fromString(String input) {
        return switch (input) {
            case "static" -> STATIC;
            case "fixed" -> FIXED;
            case "absolute" -> ABSOLUTE;
            default -> INVALID;
        };
    }
}
