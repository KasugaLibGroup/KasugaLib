package kasuga.lib.core.client.gui.enums;

public enum PositionType {
    FIXED,
    ABSOLUTE,
    INVALID;

    @Override
    public String toString() {
        return switch (this) {
            case FIXED -> "fixed";
            case ABSOLUTE -> "absolute";
            case INVALID -> "invalid";
        };
    }

    public static PositionType fromString(String input) {
        return switch (input) {
            case "fixed" -> FIXED;
            case "absolute" -> ABSOLUTE;
            default -> INVALID;
        };
    }
}
