package kasuga.lib.core.client.model.anim_json;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
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
            return;
        }
        deserializeAndFillMaps(element.getAsJsonObject(), position);
    }

    private void deserializeScale(JsonObject object) {
        if (!object.has("scale")) return;
        JsonElement element = object.get("scale");
        if (element instanceof JsonArray array) {
            Pose pose = new Pose(0, array);
            scale.put(0F, pose);
            return;
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

    public List<Map.Entry<Float, Pose>> sortPositions() {
        return sortMap(position);
    }

    public List<Map.Entry<Float, Pose>> sortRotations() {
        return sortMap(rotation);
    }

    public List<Map.Entry<Float, Pose>> sortScale() {
        return sortMap(scale);
    }

    public static List<Map.Entry<Float, Pose>> sortMap(Map<Float, Pose> map) {
        ArrayList<Map.Entry<Float, Pose>> result = new ArrayList<>(map.size());
        for (Map.Entry<Float, Pose> entry : map.entrySet()) {
            float t = entry.getKey();
            if (result.isEmpty()) {
                result.add(entry);
                continue;
            }
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getKey() > t) {
                    result.add(i, entry);
                    break;
                }
                if (i == result.size() - 1) {
                    result.add(entry);
                    break;
                }
            }
        }
        return result;
    }
}
