package kasuga.lib.core.model.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import kasuga.lib.core.client.render.texture.Vec2f;
import net.minecraft.core.Direction;

public class UnbakedUV {
    private final Direction direction;
    private final Vec2f uv, uvSize;
    public UnbakedUV(String direction, JsonObject json, float uvWidth, float uvHeight) {
        this.direction = Direction.byName(direction);
        JsonArray uvJson = json.getAsJsonArray("uv");
        uv = new Vec2f(
                uvJson.get(0).getAsFloat() / uvWidth,
                uvJson.get(1).getAsFloat() / uvHeight
        );

        JsonArray uvSizeJson = json.getAsJsonArray("uv_size");
        uvSize = new Vec2f(
                uvSizeJson.get(0).getAsFloat() / uvWidth,
                uvSizeJson.get(1).getAsFloat() / uvHeight
        );
        if (uvSize.x() < 0) {
            uv.setX(uv.x() + uvSize.x());
            uvSize.setX(-uvSize.x());
        }
        if (uvSize.y() < 0) {
            uv.setY(uv.y() + uvSize.y());
            uvSize.setY(-uvSize.y());
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public Vec2f getUv() {
        return uv;
    }

    public Vec2f getUvSize() {
        return uvSize;
    }
}
