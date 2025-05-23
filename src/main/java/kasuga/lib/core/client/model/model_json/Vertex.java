package kasuga.lib.core.client.model.model_json;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.render.texture.Vec2f;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class Vertex {
    public final Vector3f position;

    public final Vec2f uv;

    public Vertex(Vector3f position, Vec2f uv) {
        this.position = position;
        this.uv = uv;
    }

    public Vertex(Cube cube, UnbakedUV uv, int index) {
        FaceInfo.VertexInfo vertexInfo = FaceInfo.fromFacing(uv.getDirection()).getVertexInfo(index);
        Vector3f org = cube.getOrigin().copy(),
                max = cube.getSize().copy();
        max.add(org);
        org.add(- cube.getInflate(), - cube.getInflate(), - cube.getInflate());
        max.add(cube.getInflate(), cube.getInflate(), cube.getInflate());

        float x = vertexInfo.xFace == FaceInfo.Constants.MAX_X ? max.x() : org.x();
        float y = vertexInfo.yFace == FaceInfo.Constants.MAX_Y ? max.y() : org.y();
        float z = vertexInfo.zFace == FaceInfo.Constants.MAX_Z ? max.z() : org.z();
        position = new Vector3f(x, y, z);

        Vec2f uvOrg = uv.getUv(),
                uvMax = uv.getUv().add(uv.getUvSize());
        UVCorner corner = UVCorner.getCorner(vertexInfo, uv.getDirection());
        float uvx = UVCorner.isLeft(corner) ? uvOrg.x() : uvMax.x();
        float uvy = UVCorner.isTop(corner) ? uvOrg.y() : uvMax.y();
        this.uv = new Vec2f(uvx, uvy);
    }

    public Vertex applyTranslation(Vector3f translate) {
        Vector3f result = this.position.copy();
        result.add(translate);
        return new Vertex(result, uv);
    }

    public Vertex applyRotation(Vector3f pivot, Vector3f position, List<Quaternion> quaternions) {
        Vector3f result = this.position.copy();
        result.sub(pivot);
        quaternions.forEach(result::transform);
        result.add(position);
        return new Vertex(result, this.uv);
    }

    public Vertex applyRotation(Vector3f pivot, Quaternion quaternion) {
        Vector3f result = this.position.copy();
        result.sub(pivot);
        result.transform(quaternion);
        result.add(pivot);
        return new Vertex(result, this.uv);
    }

    public static boolean nearlyEquals(float num, float num2, float diff) {
        return num > num2 - diff && num < num2 + diff;
    }

    public Vertex applyScale(Vector3f pivot, Vector3f scale) {
        if (scale == null) return this;
        Vector3f result = this.position.copy();
        result.sub(pivot);
        result.mul(scale.x(), scale.y(), scale.z());
        result.add(pivot);
        return new Vertex(result, this.uv);
    }

    public void fillVertex(int[] vertexData, int index, float u0, float v0, float scaleU, float scaleV) {
        int i = index * 8;
        vertexData[i] = Float.floatToRawIntBits(this.position.x());
        vertexData[i + 1] = Float.floatToRawIntBits(this.position.y());
        vertexData[i + 2] = Float.floatToRawIntBits(this.position.z());
        vertexData[i + 3] = -1;
        vertexData[i + 4] = Float.floatToRawIntBits(u0 + this.uv.x() * scaleU);
        vertexData[i + 5] = Float.floatToRawIntBits(v0 + this.uv.y() * scaleV);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vec2f getUv() {
        return uv;
    }
}
