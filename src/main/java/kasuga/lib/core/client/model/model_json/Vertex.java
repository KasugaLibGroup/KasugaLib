package kasuga.lib.core.client.model.model_json;

import kasuga.lib.core.client.render.texture.Vec2f;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class Vertex {
    public final Vector3f position;

    public final Vec2f uv;

    public Vertex(Vector3f position, Vec2f uv) {
        this.position = position;
        this.uv = uv;
    }

    public Vertex(Cube cube, UnbakedUV uv, int index) {
        FaceInfo.VertexInfo vertexInfo = FaceInfo.fromFacing(uv.getDirection()).getVertexInfo(index);
        Vector3f org = new Vector3f(cube.getOrigin()),
                max = new Vector3f(cube.getSize());
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
        Vector3f result = new Vector3f(this.position);
        result.add(translate);
        return new Vertex(result, uv);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 4; i++) {
            FaceInfo.VertexInfo vertexInfo = FaceInfo.fromFacing(Direction.UP).getVertexInfo(i);
            boolean x = vertexInfo.xFace == FaceInfo.Constants.MAX_X;
            boolean y = vertexInfo.yFace == FaceInfo.Constants.MAX_Y;
            boolean z = vertexInfo.zFace == FaceInfo.Constants.MAX_Z;
            System.out.println("index=" + i + ", x_max=" + x + ", y_max=" + y + ", z_max=" + z);
        }
    }

    public Vertex applyRotation(Vector3f pivot, Vector3f position, List<Quaternionf> quaternions) {
        Vector3f result = new Vector3f(this.position);
        result.sub(pivot);
        quaternions.forEach(quaternionf -> quaternionf.transform(result));
        result.add(position);
        return new Vertex(result, this.uv);
    }

    public Vertex applyRotation(Vector3f pivot, Quaternionf quaternion) {
        Vector3f result = new Vector3f(this.position);
        result.sub(pivot);
        quaternion.transform(result);
        result.add(pivot);
        return new Vertex(result, this.uv);
    }

    public static boolean nearlyEquals(float num, float num2, float diff) {
        return num > num2 - diff && num < num2 + diff;
    }

    public Vertex applyScale(Vector3f pivot, Vector3f scale) {
        if (scale == null) return this;
        Vector3f result = new Vector3f(this.position);
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
