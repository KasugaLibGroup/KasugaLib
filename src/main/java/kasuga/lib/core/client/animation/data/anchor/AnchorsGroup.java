package kasuga.lib.core.client.animation.data.anchor;

import com.google.gson.JsonObject;
import interpreter.compute.data.Namespace;
import kasuga.lib.core.client.animation.data.Animation;

import java.util.HashMap;

public class AnchorsGroup {
    private final HashMap<String, Anchor> anchors;
    private final Namespace namespace;
    private final Animation animation;

    public AnchorsGroup(Animation animation, Namespace namespace) {
        this.anchors = new HashMap<>();
        this.namespace = namespace;
        this.animation = animation;
    }

    public boolean containsAnchor(String name) {
        return anchors.containsKey(name);
    }

    public Anchor getAnchor(String name) {
        return anchors.getOrDefault(name, null);
    }

    public void addAnchor(Anchor anchor) {
        anchors.put(anchor.key(), anchor);
    }

    public void removeAnchor(String name) {
        anchors.remove(name);
    }

    public void decodeAnchors(JsonObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            JsonObject object = jsonObject.getAsJsonObject(key);
            anchors.put(key, Anchor.decode(key, namespace, animation, object));
        }
    }

    public void init() {
        anchors.values().forEach(Anchor::invoke);
    }
}
