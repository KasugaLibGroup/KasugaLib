package kasuga.lib.core.client.gui.components;

import com.google.gson.JsonObject;

public interface ComponentType<T extends Node> {
    T create(JsonObject attributes);
}
