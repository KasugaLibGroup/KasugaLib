package kasuga.lib.core.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;

public class GeometryDescription {
    private final String identifier;
    private final float textureWidth, textureHeight, visibleBoundsWidth, visibleBoundsHeight;
    private final Vector3f visibleBoundsOffset;

    public GeometryDescription(JsonObject json) {
        this.identifier = json.get("identifier").getAsString();
        this.textureWidth = json.get("texture_width").getAsFloat();
        this.textureHeight = json.get("texture_height").getAsFloat();
        this.visibleBoundsWidth = json.get("visible_bounds_width").getAsFloat();
        this.visibleBoundsHeight = json.get("visible_bounds_height").getAsFloat();
        JsonArray array = json.getAsJsonArray("visible_bounds_offset");
        visibleBoundsOffset = new Vector3f(
                array.get(0).getAsFloat(),
                array.get(1).getAsFloat(),
                array.get(2).getAsFloat()
        );
    }

    public float getTextureHeight() {
        return textureHeight;
    }

    public float getTextureWidth() {
        return textureWidth;
    }

    public float getVisibleBoundsHeight() {
        return visibleBoundsHeight;
    }

    public float getVisibleBoundsWidth() {
        return visibleBoundsWidth;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Vector3f getVisibleBoundsOffset() {
        return visibleBoundsOffset;
    }
}
