package kasuga.lib.core.client.model.model_json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import kasuga.lib.core.client.render.texture.Vec2f;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnbakedUV {
    private final Direction direction;
    private final Vec2f uv, uvSize;
    private final boolean mirrorX, mirrorY, visible, emissive;

    public UnbakedUV(Direction direction, JsonObject json, float uvWidth, float uvHeight,
                     boolean mirrorX, boolean mirrorY, boolean visible, boolean emissive) {
        this.direction = direction;
        this.mirrorX = mirrorX;
        this.mirrorY = mirrorY;
        this.visible = visible;
        this.emissive = emissive;
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
        dealWithMirrors();
    }

    public UnbakedUV(Direction direction, Vec2f uv, Vec2f uvSize, float uvWidth, float uvHeight,
                     boolean mirrorX, boolean mirrorY, boolean visible, boolean emissive) {
        this.direction = direction;
        this.mirrorX = mirrorX;
        this.mirrorY = mirrorY;
        this.visible = visible;
        this.emissive = emissive;
        this.uv = uv;
        this.uvSize = uvSize;
        dealWithMirrors();
    }

    private void dealWithMirrors() {
        if (mirrorX) {
            float cache = uv.x() + uvSize.x();
            uvSize.setX(-uvSize.x());
            uv.setX(cache);
        }
        if (mirrorY) {
            float cache = uv.y() + uvSize.y();
            uvSize.setY(uvSize.y());
            uv.setY(cache);
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

    public boolean isMirrorX() {
        return mirrorX;
    }

    public boolean isMirrorY() {
        return mirrorY;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isEmissive() {
        return emissive;
    }
}
