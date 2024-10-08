package kasuga.lib.core.util;

import kasuga.lib.core.annos.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@Util
public class ComponentHelper {

    public static Component literal(String text) {
        return new TextComponent(text);
    }

    public static Component translatable(String key) {
        return new TranslatableComponent(key);
    }

    public static Component translatable(String key, Object... args) {
        return new TranslatableComponent(key, args);
    }

    public static Component keybind(String name) {
        return new KeybindComponent(name);
    }

    public static Component empty() {
        return new TextComponent("");
    }
}
