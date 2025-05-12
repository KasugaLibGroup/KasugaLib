package kasuga.lib.core.client.block_bench_model.json_data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import kasuga.lib.core.util.data_type.Pair;
import lombok.Getter;
import net.minecraft.core.Direction;

import java.util.*;

@Getter
public class Element implements IElement {

    private final String name;
    private final int previewColorType;
    private final Vector3f pivot;
    private final Vector3f rotation;
    private final boolean export, visibility,
            locked, allowMirrorRendering;
    private final String renderOrder, type;
    private final UUID id;
    private final HashMap<String, Vector3f> vertices;
    private final HashMap<String, Face> faces;
    private final BlockBenchFile file;

    public Element(BlockBenchFile file, JsonObject object) throws BlockBenchFile.UnableToLoadFileError {
        this.file = file;
        type = object.get("type").getAsString();
        name = object.get("name").getAsString();
        previewColorType = object.has("color") ? object.get("color").getAsInt() : 0;
        faces = new HashMap<>();
        try {
            JsonArray pivotArray = object.getAsJsonArray("origin");
            pivot = new Vector3f(
                    pivotArray.get(0).getAsFloat(),
                    pivotArray.get(1).getAsFloat(),
                    pivotArray.get(2).getAsFloat()
            );
        } catch (Exception e) {
            throw new BlockBenchFile.UnableToLoadFileError("Unable to load origin", e);
        }
        try {
            if (object.has("rotation")) {
                JsonArray rotationArray = object.getAsJsonArray("rotation");
                rotation = new Vector3f(
                        rotationArray.get(0).getAsFloat(),
                        rotationArray.get(1).getAsFloat(),
                        rotationArray.get(2).getAsFloat()
                );
            } else {
                rotation = new Vector3f();
            }
        } catch (Exception e) {
            throw new BlockBenchFile.UnableToLoadFileError("Unable to load rotation", e);
        }
        export = object.has("export") && object.get("export").getAsBoolean();
        visibility = object.has("visibility") && object.get("visibility").getAsBoolean();
        locked = object.has("locked") && object.get("locked").getAsBoolean();
        allowMirrorRendering = object.has("allow_mirror_rendering") && object.get("allow_mirror_rendering").getAsBoolean();
        renderOrder = object.get("render_order").getAsString();
        id = UUID.fromString(object.get("uuid").getAsString());
        if (type.equals("mesh")) {
            // load vertices;
            vertices = new HashMap<>();
            try {
                JsonObject verticesObj = object.getAsJsonObject("vertices");
                for (String key : verticesObj.keySet()) {
                    JsonArray vertexArray = verticesObj.getAsJsonArray(key);
                    Vector3f vertex = new Vector3f(
                            vertexArray.get(0).getAsFloat(),
                            vertexArray.get(1).getAsFloat(),
                            vertexArray.get(2).getAsFloat()
                    );
                    vertices.put(key, vertex);
                }
            } catch (Exception e) {
                throw new BlockBenchFile.UnableToLoadFileError("Unable to load vertices", e);
            }

            // load faces
            try {
                JsonObject facesObj = object.getAsJsonObject("faces");
                for (String key : facesObj.keySet()) {
                    JsonObject faceObj = facesObj.getAsJsonObject(key);
                    Face face = new Face(this, faceObj);
                    face.sortVertices(this.vertices);
                    face.updateNormal(this.vertices);
                    faces.put(key, face);
                }
            } catch (Exception e) {
                throw new BlockBenchFile.UnableToLoadFileError("Unable to load faces", e);
            }
        } else {
            // load cube definition
            Vector3f from, to;
            try {
                JsonArray fromArray = object.getAsJsonArray("from");
                JsonArray toArray = object.getAsJsonArray("to");
                from = new Vector3f(
                        fromArray.get(0).getAsFloat(),
                        fromArray.get(1).getAsFloat(),
                        fromArray.get(2).getAsFloat()
                );
                from.sub(pivot);
                to = new Vector3f(
                        toArray.get(0).getAsFloat(),
                        toArray.get(1).getAsFloat(),
                        toArray.get(2).getAsFloat()
                );
                to.sub(pivot);
            } catch (Exception e) {
                throw new BlockBenchFile.UnableToLoadFileError("Unable to load vertices", e);
            }
            this.vertices = getVertices(from, to);
            JsonObject faceArray = object.getAsJsonObject("faces");
            for (Map.Entry<String, JsonElement> faceEntry : faceArray.entrySet()) {
                Direction direction = Direction.byName(faceEntry.getKey());
                if (direction == null) continue;
                Face face = new Face(this, direction, faceEntry.getValue().getAsJsonObject(),
                        from, to, vertices);
                face.updateNormal(direction);
                this.faces.put(faceEntry.getKey(), face);
            }
        }
    }

    public HashMap<String, Vector3f> getVertices(Vector3f from, Vector3f to) {
        Vector3f minCorner = new Vector3f(Math.min(from.x(), to.x()), Math.min(from.y(), to.y()), Math.min(from.z(), to.z()));
        Vector3f maxCorner = new Vector3f(Math.max(from.x(), to.x()), Math.max(from.y(), to.y()), Math.max(from.z(), to.z()));
        HashMap<String, Vector3f> result = new HashMap<>();
        int randSeed = 42, len = 4;
        result.put(getRandomVertexName(randSeed++, len, result.keySet()), minCorner);
        result.put(getRandomVertexName(randSeed++, len, result.keySet()), maxCorner);

        result.put(getRandomVertexName(randSeed++, len, result.keySet()),
                    new Vector3f(maxCorner.x(), minCorner.y(), minCorner.z())
        );
        result.put(getRandomVertexName(randSeed++, len, result.keySet()),
                new Vector3f(minCorner.x(), maxCorner.y(), minCorner.z())
        );
        result.put(getRandomVertexName(randSeed++, len, result.keySet()),
                new Vector3f(minCorner.x(), minCorner.y(), maxCorner.z())
        );
        result.put(getRandomVertexName(randSeed++, len, result.keySet()),
                new Vector3f(maxCorner.x(), maxCorner.y(), minCorner.z())
        );
        result.put(getRandomVertexName(randSeed++, len, result.keySet()),
                new Vector3f(maxCorner.x(), minCorner.y(), maxCorner.z())
        );
        result.put(getRandomVertexName(randSeed, len, result.keySet()),
                new Vector3f(minCorner.x(), maxCorner.y(), maxCorner.z())
        );
        return result;
    }

    public String getRandomVertexName(int randomSeed, int length, Collection<String> conflicts) {
        StringBuilder builder;
        do {
            builder = new StringBuilder();
            Random rand = new Random(randomSeed);
            for (int i = 0; i < length; i++) {
                builder.append((char) rand.nextInt(97, 123));
            }
        } while (conflicts.contains(builder.toString()));
        return builder.toString();
    }
}
