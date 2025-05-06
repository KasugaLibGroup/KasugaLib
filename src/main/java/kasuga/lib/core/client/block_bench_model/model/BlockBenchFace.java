package kasuga.lib.core.client.block_bench_model.model;

import com.mojang.math.Vector3f;
import kasuga.lib.core.client.block_bench_model.json_data.Face;
import kasuga.lib.core.client.block_bench_model.json_data.Texture;
import kasuga.lib.core.client.render.texture.Vec2f;
import kasuga.lib.core.util.data_type.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Getter
public class BlockBenchFace {

    private final Face face;

    @Setter
    private boolean render;
    private final HashMap<String, Vec2f> vertexMapping;
    private final List<String> vertices;
    private Material material = null;
    private Texture texture = null;

    public BlockBenchFace(Face face) {
        this.face = face;
        this.render = true;
        vertexMapping = face.getUvs();
        vertices = face.getVertices();
    }

    public void mapTexture(Texture texture, Material material) {
        this.texture = texture;
        this.material = material;
    }

    public Optional<Integer> getTextureIndex() {
        return face.getTextureIndex();
    }

    public Pair<Vec2f, Vec2f> getUVData(TextureAtlasSprite sprite) {
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        float width = u1 - u0;
        float height = v1 - v0;
        return Pair.of(new Vec2f(u0, v0), new Vec2f(width, height));
    }

    public BakedQuad fillVertices(HashMap<String, Vector3f> vertices, Vec2f resolution,
                                  Function<Material, TextureAtlasSprite> spriteGetter,
                                  int tintIndex, boolean shade) {
        TextureAtlasSprite sprite = spriteGetter.apply(this.material);
        Pair<Vec2f, Vec2f> uvData = getUVData(sprite);
        int[] data = new int[32];
        for (int i = 0; i < 4; i++) {
            String vertexName =  this.vertices.get(
                    (i < this.vertices.size()) ? i : (this.vertices.size() - 1)
            );
            Vector3f pos = vertices.get(vertexName);
            fillVertex(data, i, pos,
                    this.vertexMapping.get(vertexName)
                            .scale(1f / texture.getUvWidth()
                                    , 1f / texture.getUvHeight()
                            ),
                    uvData.getFirst(), uvData.getSecond());
        }
        Direction direction = getDirection(vertices);
        return new BakedQuad(data, tintIndex, direction, sprite, shade);
    }

    public BakedQuad fillVertices(HashMap<String, Vector3f> vertices, Vec2f resolution,
                                  Function<Material, TextureAtlasSprite> spriteGetter) {
        return fillVertices(vertices, resolution, spriteGetter, 0, true);
    }

    public Direction getDirection(HashMap<String, Vector3f> vertices) {
        if (vertices.size() < 3) return Direction.UP;
        Vector3f vec1 = vertices.get(this.vertices.get(0));
        Vector3f vec2 = vertices.get(this.vertices.get(1));
        Vector3f vec3 = vertices.get(this.vertices.get(2));
        vec1 = new Vector3f(vec1.x(), vec1.y(), vec1.z());
        vec2 = new Vector3f(vec2.x(), vec2.y(), vec2.z());
        vec3 = new Vector3f(vec3.x(), vec3.y(), vec3.z());
        vec2.sub(vec1);
        vec3.sub(vec2);
        Vector3f result = new Vector3f(vec2.x(), vec2.y(), vec2.z());
        result.cross(vec3);
        result.mul(-1);
        result.normalize();
        return innerGetDirection(result);
    }

    public Direction innerGetDirection(Vector3f normal) {
        float absX = Math.abs(normal.x());
        float absY = Math.abs(normal.y());
        float absZ = Math.abs(normal.z());

        if (absX > absY && absX > absZ) {
            return normal.x() >= 0 ? Direction.EAST : Direction.WEST;
        } else if (absY > absX && absY > absZ) {
            return normal.y() >= 0 ? Direction.UP : Direction.DOWN;
        } else if (absZ > absX && absZ > absY) {
            return normal.z() >= 0 ? Direction.NORTH : Direction.SOUTH;
        }
        return Direction.UP;
    }

    public void fillVertex(int[] data, int vertexIndex, Vector3f vertex,
                           Vec2f uv, Vec2f u0v0, Vec2f scaleUV) {
        int i = vertexIndex * 8;
        data[i] = Float.floatToRawIntBits(vertex.x());
        data[i + 1] = Float.floatToRawIntBits(vertex.y());
        data[i + 2] = Float.floatToRawIntBits(vertex.z());
        data[i + 3] = -1;
        data[i + 4] = Float.floatToRawIntBits(u0v0.x() + uv.x() * scaleUV.x());
        data[i + 5] = Float.floatToRawIntBits(u0v0.y() + uv.y() * scaleUV.y());
    }
}
