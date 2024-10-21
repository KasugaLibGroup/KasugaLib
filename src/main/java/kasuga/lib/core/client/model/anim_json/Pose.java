package kasuga.lib.core.client.model.anim_json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.model.model_json.Geometry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Pose {

    public final float time;
    private final Vector3f pre, post;
    private final boolean hasPre;
    public final String lerpMode;

    public Pose(float time, JsonElement element) {
        this.time = time;
        if (element instanceof JsonArray array) {
            post = Geometry.readVec3fFromJsonArray(array);
            pre = Vector3f.ZERO.copy();
            hasPre = false;
            lerpMode = null;
        } else if (element instanceof JsonObject object) {
            if (object.has("post") &&
                    object.get("post") instanceof JsonArray array)
                post = Geometry.readVec3fFromJsonArray(array);
            else post = Vector3f.ZERO.copy();

            if (object.has("pre") &&
                    object.get("pre") instanceof JsonArray array)
                pre = Geometry.readVec3fFromJsonArray(array);
            else pre = Vector3f.ZERO.copy();
            hasPre = object.has("pre");

            lerpMode = object.has("lerp_mode") ?
                    object.getAsJsonPrimitive("lerp_mode").getAsString() :
                    null;
        } else {
            pre = Vector3f.ZERO.copy();
            post = Vector3f.ZERO.copy();
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
