package kasuga.lib.core.model.anim;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class KeyFrame {

    public final String bone;

    public final HashMap<Float, Pose> position, rotation, scale;

    public KeyFrame(String bone, JsonObject json) {
        this.bone = bone;
        this.position = Maps.newHashMap();
        this.rotation = Maps.newHashMap();
        this.scale = Maps.newHashMap();
        deserializePosition(json);
        deserializeRotation(json);
        deserializeScale(json);
    }

    private void deserializeRotation(JsonObject object) {
        if (!object.has("rotation")) return;
        JsonElement element = object.get("rotation");
        if (element instanceof JsonArray array) {
            Pose pose = new Pose(0, array);
            rotation.put(0F, pose);
            return;
        }
        deserializeAndFillMaps(element.getAsJsonObject(), rotation);
    }
    private void deserializePosition(JsonObject object) {
        if (!object.has("position")) return;
        JsonElement element = object.get("position");
        if (element instanceof JsonArray array) {
            Pose pose = new Pose(0, array);
            position.put(0F, pose);
        }
        deserializeAndFillMaps(element.getAsJsonObject(), position);
    }

    private void deserializeScale(JsonObject object) {
        if (!object.has("scale")) return;
        JsonElement element = object.get("scale");
        if (element instanceof JsonArray array) {
            Pose pose = new Pose(0, array);
            scale.put(0F, pose);
        }
        deserializeAndFillMaps(element.getAsJsonObject(), scale);
    }

    private void deserializeAndFillMaps(JsonObject object, Map<Float, Pose> map) {
        object.entrySet().forEach(
                entry -> {
                    float left = Float.parseFloat(entry.getKey());
                    Pose pose = new Pose(left, entry.getValue());
                    map.put(left, pose);
                }
        );
    }
}
