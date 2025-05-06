package kasuga.lib.core.client.block_bench_model.json_data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.render.texture.Vec2f;
import lombok.Getter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Getter
public class Face {

    private final HashMap<String, Vec2f> uvs;
    private final List<String> vertices;
    private final Integer textureIndex;
    private final Element parent;

    public Face(Element parent, JsonObject json) throws BlockBenchFile.UnableToLoadFileError {
        uvs = new HashMap<>();
        this.parent = parent;
        vertices = new ArrayList<>();
        try {
            JsonObject uvObj = json.get("uv").getAsJsonObject();
            for (String key : uvObj.keySet()) {
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
        try {
            JsonArray verticesArray = json.get("vertices").getAsJsonArray();
            verticesArray.forEach(element -> vertices.add(element.getAsString()));

            // seems it is by line, so we should flip the last two elements.
            if (vertices.size() > 3) {
                String element2 = vertices.get(2);
                vertices.set(2, vertices.get(3));
                vertices.set(3, element2);
            }
        } catch (Exception e) {
            throw new BlockBenchFile.UnableToLoadFileError("Unable to load uv vertices.", e);
        }
        textureIndex = json.has("texture") ? json.get("texture").getAsInt() : null;
    }

    public void getBakedFace(TextureAtlasSprite sprite, Vector3f offset) {

    }

    public boolean hasTexture() {
        return textureIndex != null;
    }

    public Optional<Integer> getTextureIndex() {
        return Optional.ofNullable(textureIndex);
    }
}
