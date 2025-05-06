package kasuga.lib.core.client.block_bench_model.json_data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

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
        name = object.get("name").getAsString();
        previewColorType = object.get("color").getAsInt();
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
            JsonArray rotationArray = object.getAsJsonArray("rotation");
            rotation = new Vector3f(
                    rotationArray.get(0).getAsFloat(),
                    rotationArray.get(1).getAsFloat(),
                    rotationArray.get(2).getAsFloat()
            );
        } catch (Exception e) {
            throw new BlockBenchFile.UnableToLoadFileError("Unable to load rotation", e);
        }
        export = object.has("export") && object.get("export").getAsBoolean();
        visibility = object.has("visibility") && object.get("visibility").getAsBoolean();
        locked = object.has("locked") && object.get("locked").getAsBoolean();
        allowMirrorRendering = object.has("allow_mirror_rendering") && object.get("allow_mirror_rendering").getAsBoolean();
        renderOrder = object.get("render_order").getAsString();
        type = object.get("type").getAsString();
        id = UUID.fromString(object.get("uuid").getAsString());

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
        faces = new HashMap<>();
        try {
            JsonObject facesObj = object.getAsJsonObject("faces");
            for (String key : facesObj.keySet()) {
                JsonObject faceObj = facesObj.getAsJsonObject(key);
                Face face = new Face(this, faceObj);
                faces.put(key, face);
            }
        } catch (Exception e) {
            throw new BlockBenchFile.UnableToLoadFileError("Unable to load faces", e);
        }
    }
}
