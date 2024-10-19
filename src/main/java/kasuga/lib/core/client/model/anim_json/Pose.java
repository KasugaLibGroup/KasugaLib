package kasuga.lib.core.client.model.anim_json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kasuga.lib.core.client.model.model_json.Geometry;
import org.joml.Vector3f;

public class Pose {

    public final float time;
    private final Vector3f pre, post;
    private final boolean hasPre;
    public final String lerpMode;

    public Pose(float time, JsonElement element) {
        this.time = time;
        if (element instanceof JsonArray array) {
            post = Geometry.readVec3fFromJsonArray(array);
            pre = new Vector3f();
            hasPre = false;
            lerpMode = null;
        } else if (element instanceof JsonObject object) {
            if (object.has("post") &&
                    object.get("post") instanceof JsonArray array)
                post = Geometry.readVec3fFromJsonArray(array);
            else post = new Vector3f();

            if (object.has("pre") &&
                    object.get("pre") instanceof JsonArray array)
                pre = Geometry.readVec3fFromJsonArray(array);
            else pre = new Vector3f();
            hasPre = object.has("pre");

            lerpMode = object.has("lerp_mode") ?
                    object.getAsJsonPrimitive("lerp_mode").getAsString() :
                    null;
        } else {
            pre = new Vector3f();
            post = new Vector3f();
            hasPre = false;
            lerpMode = null;
        }
    }

    public String getLerpMode() {
        return lerpMode;
    }

    public boolean isCatmullRom() {
        return lerpMode != null && lerpMode.equals("catmullrom");
    }

    public float getTime() {
        return time;
    }

    public Vector3f getPost() {
        return post;
    }

    public Vector3f getPre() {
        return pre;
    }

    public boolean hasPre() {
        return hasPre;
    }
}
