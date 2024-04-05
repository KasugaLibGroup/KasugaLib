package kasuga.lib.core.client.gui.enums;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public enum ComponentType {
    TRANSLATABLE,
    LITERAL,
    EMPTY;

    @Override
    public String toString() {
        return switch (this) {
            case LITERAL -> "literal";
            case TRANSLATABLE -> "translatable";
            case EMPTY -> "empty";
        };
    }

    public MutableComponent getComponent(String textOrKey) {
        return getComponent(textOrKey, this);
    }

    public static ComponentType fromString(String input) {
        return switch (input) {
            case "literal" -> LITERAL;
            case "translatable" -> TRANSLATABLE;
            default -> EMPTY;
        };
    }

    public static MutableComponent getComponent(String textOrKey, ComponentType type) {
        return switch (type) {
            case TRANSLATABLE -> Component.translatable(textOrKey);
            case LITERAL -> Component.literal(textOrKey);
            default -> Component.empty();
        };
    }

    public static MutableComponent getComponent(String textOrKey, String type) {
        return getComponent(textOrKey, fromString(type));
    }
}
