package kasuga.lib.core.util;

import kasuga.lib.core.annos.Util;
import net.minecraft.network.chat.Component;

@Util
public class ComponentHelper {

    public static Component literal(String text) {
        return Component.literal(text);
    }

    public static Component translatable(String key) {
        return Component.translatable(key);
    }

    public static Component translatable(String key, Object... args) {
        return Component.translatable(key, args);
    }

    public static Component keybind(String name) {
        return Component.keybind(name);
    }

    public static Component empty() {
        return Component.empty();
    }
}
