package kasuga.lib.core.client.gui.enums;

public enum DisplayType {
    BLOCK,
    INLINE_BLOCK;

    @Override
    public String toString() {
        return switch (this) {
            case BLOCK -> "block";
            case INLINE_BLOCK -> "inline_block";
        };
    }

    public static DisplayType fromString(String input) {
        return switch (input) {
            case "block" -> BLOCK;
            case "inline_block" -> INLINE_BLOCK;
            default -> BLOCK;
        };
    }
}
