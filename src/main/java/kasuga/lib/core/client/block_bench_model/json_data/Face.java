package kasuga.lib.core.client.block_bench_model.json_data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.model.model_json.UVCorner;
import kasuga.lib.core.client.render.texture.Vec2f;
import kasuga.lib.core.util.data_type.Pair;
import lombok.Getter;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
@Getter
public class Face {

    private final HashMap<String, Vec2f> uvs;
    private final List<String> vertices;
    private final Integer textureIndex;
    private final Element parent;
    private Vector3f normal;

    public Face(Element parent, JsonObject json) throws BlockBenchFile.UnableToLoadFileError {
        uvs = new HashMap<>();
        this.parent = parent;
        vertices = new ArrayList<>();
        normal = new Vector3f();
        try {
            JsonArray verticesArray = json.get("vertices").getAsJsonArray();
            verticesArray.forEach(element -> vertices.add(element.getAsString()));
        } catch (Exception e) {
            throw new BlockBenchFile.UnableToLoadFileError("Unable to load uv vertices.", e);
        }
        try {
            JsonObject uvObj = json.get("uv").getAsJsonObject();
            for (String key : uvObj.keySet()) {
                if (!this.vertices.contains(key)) continue;
                JsonArray uvArray = uvObj.get(key).getAsJsonArray();
                Vec2f uvVertex = new Vec2f(
                        uvArray.get(0).getAsFloat(),
                        uvArray.get(1).getAsFloat()
                );
                uvs.put(key, uvVertex);
            }
        } catch (Exception e) {
            throw new BlockBenchFile.UnableToLoadFileError("Unable to load uv.", e);
        }
        textureIndex = json.has("texture") ? json.get("texture").getAsInt() : null;
    }

    public Face(Element parent, Direction direction, JsonObject json,
                Vector3f minCorner, Vector3f maxCorner,
                HashMap<String, Vector3f> vertices) throws BlockBenchFile.UnableToLoadFileError {
        textureIndex = json.has("texture") ? json.get("texture").getAsInt() : null;
        uvs = new HashMap<>();
        this.vertices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.vertices.add(null);
        }
        this.parent = parent;
        Vec2f uvMin, uvMax;
        try {
            JsonArray uvArray = json.get("uv").getAsJsonArray();
            uvMin = new Vec2f(uvArray.get(0).getAsFloat(), uvArray.get(1).getAsFloat());
            uvMax = new Vec2f(uvArray.get(2).getAsFloat(), uvArray.get(3).getAsFloat());
        } catch (Exception e) {
            throw new BlockBenchFile.UnableToLoadFileError("Unable to load uvs.", e);
        }
        for (int i = 0; i < 4; i++) {
            FaceInfo.VertexInfo vertexInfo = FaceInfo.fromFacing(direction).getVertexInfo(i);
            float x = vertexInfo.xFace == FaceInfo.Constants.MAX_X ? maxCorner.x() : minCorner.x();
            float y = vertexInfo.yFace == FaceInfo.Constants.MAX_Y ? maxCorner.y() : minCorner.y();
            float z = vertexInfo.zFace == FaceInfo.Constants.MAX_Z ? maxCorner.z() : minCorner.z();
            Vector3f vertex = new Vector3f(x, y, z);

            UVCorner corner = UVCorner.getCorner(vertexInfo, direction);
            float uvx = UVCorner.isLeft(corner) ? uvMin.x() : uvMax.x();
            float uvy = UVCorner.isTop(corner) ? uvMin.y() : uvMax.y();
            Vec2f uvVertex = new Vec2f(uvx, uvy);

            for (Map.Entry<String, Vector3f> entry : vertices.entrySet()) {
                String key = entry.getKey();
                Vector3f value = entry.getValue();
                if (value.equals(vertex)) {
                    uvs.put(key, uvVertex);
                    this.vertices.set(getUVCornerIndex(direction, corner), key);
                    break;
                }
            }
        }
        if (this.vertices.contains(null))
            throw new BlockBenchFile.UnableToLoadFileError("contains null uv corner!",
                    new IllegalArgumentException("Vertex corner must not be null"));
    }

    private int getUVCornerIndex(Direction direction, UVCorner corner) {
        int result;
        if (UVCorner.isTop(corner)) {
            result = UVCorner.isLeft(corner) ? 0 : 3;
        } else {
            result = UVCorner.isLeft(corner) ? 1 : 2;
        }
        return direction == Direction.DOWN ? (3 - result) : result;
    }

    public void sortVertices(HashMap<String, Vector3f> vertices) {
        List<String> result = getSortedVertices(this.uvs, vertices, this.vertices);
        if (!result.equals(this.vertices)) {
            this.vertices.clear();
            this.vertices.addAll(result);
        }
    }

    public List<String> getSortedVertices(HashMap<String, Vec2f> meshVertices,
                                          HashMap<String, Vector3f> vertices,
                                          List<String> vertexSequence) {
        if (vertexSequence.size() < 4) return vertexSequence;
        List<Pair<String, Vector3f>> vertexList = new ArrayList<>(vertexSequence.size());
        for (String vertex : vertexSequence) {
            vertexList.add(Pair.of(vertex, vertices.get(vertex)));
        }
        if (test(vertexList.get(1).getSecond(),
                vertexList.get(2).getSecond(),
                vertexList.get(0).getSecond(),
                vertexList.get(3).getSecond())
        ) {
            return new ArrayList<>(){{
                add(vertexList.get(2).getFirst());
                add(vertexList.get(0).getFirst());
                add(vertexList.get(1).getFirst());
                add(vertexList.get(3).getFirst());
            }};
        } else if (test(
                vertexList.get(0).getSecond(),
                vertexList.get(1).getSecond(),
                vertexList.get(2).getSecond(),
                vertexList.get(3).getSecond()
        )) {
            return new ArrayList<>(){{
                add(vertexList.get(0).getFirst());
                add(vertexList.get(2).getFirst());
                add(vertexList.get(1).getFirst());
                add(vertexList.get(3).getFirst());
            }};
        }
        return vertexSequence;
    }

    public void updateNormal(HashMap<String, Vector3f> vertices) {
        if (this.vertices.size() < 3) return;
        List<Vector3f> myVertices = new ArrayList<>(this.vertices.size());
        this.vertices.forEach(vertex -> myVertices.add(vertices.get(vertex)));
        Vector3f firstEdge = new Vector3f();
        firstEdge.add(myVertices.get(1));
        firstEdge.sub(myVertices.get(0));
        Vector3f lastEdge = new Vector3f();
        lastEdge.add(myVertices.get(0));
        lastEdge.sub(myVertices.get(myVertices.size() - 1));
        Vector3f normal = new Vector3f();
        normal.add(firstEdge);
        normal.cross(lastEdge);
        normal.normalize();
        this.normal = normal;
    }

    public void updateNormal(Direction direction) {
        switch (direction) {
            case UP -> {
                this.normal = new Vector3f(0, 1, 0);
            }
            case DOWN -> {
                this.normal = new Vector3f(0, -1, 0);
            }
            case NORTH -> {
                this.normal = new Vector3f(0, 0, 1);
            }
            case SOUTH -> {
                this.normal = new Vector3f(0, 0, -1);
            }
            case WEST -> {
                this.normal = new Vector3f(-1, 0, 0);
            }
            case EAST -> {
                this.normal = new Vector3f(1, 0, 0);
            }
        }
    }

    private boolean test(Vector3f base1, Vector3f base2, Vector3f top, Vector3f check) {
        Vector3f lineDir = new Vector3f();
        lineDir.add(base2);
        lineDir.sub(base1);
        Vector3f toTop = new Vector3f();
        toTop.add(top);
        toTop.sub(base1);
        float t = toTop.dot(lineDir) / lineDir.dot(lineDir);

        Vector3f projection = new Vector3f(
                base1.x() + lineDir.x() * t,
                base1.y() + lineDir.y() * t,
                    base1.z() + lineDir.z() * t
        );
        Vector3f normal = new Vector3f();
        normal.add(projection);
        normal.sub(top);

        float a = normal.x();
        float b = normal.y();
        float c = normal.z();
        float d = - (a * base2.x() + b * base2.y() + c * base2.z());

        double distance = a * check.x() + b * check.y() + c * check.z() + d;
        return distance > 0;
    }



    public boolean hasTexture() {
        return textureIndex != null;
    }

    public Optional<Integer> getTextureIndex() {
        return Optional.ofNullable(textureIndex);
    }
}
