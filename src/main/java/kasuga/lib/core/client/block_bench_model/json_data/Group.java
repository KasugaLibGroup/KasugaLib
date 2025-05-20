package kasuga.lib.core.client.block_bench_model.json_data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

@OnlyIn(Dist.CLIENT)
@Getter
public class Group implements IElement {

    private final String name;
    private final Vector3f pivot, rotation;
    private final int previewColorType;
    private final boolean export, mirrorUV,
            isOpen, locked, visibility;
    private final int autoUV;
    private final UUID id;
    private final HashMap<UUID, IElement> children;

    public Group(JsonObject json) throws BlockBenchFile.UnableToLoadFileError {
        name = json.get("name").getAsString();
        try {
            JsonArray pivotArray = json.get("origin").getAsJsonArray();
            pivot = new Vector3f(
                    pivotArray.get(0).getAsFloat(),
                    pivotArray.get(1).getAsFloat(),
                    pivotArray.get(2).getAsFloat()
            );
        } catch (Exception e) {
            throw new BlockBenchFile.UnableToLoadFileError("Unable to load group", e);
        }
        try {
            if (json.has("rotation")) {
                JsonArray rotArray = json.get("rotation").getAsJsonArray();
                rotation = new Vector3f(
                        rotArray.get(0).getAsFloat(),
                        rotArray.get(1).getAsFloat(),
                        rotArray.get(2).getAsFloat()
                );
            } else {
                rotation = new Vector3f();
            }
        } catch (Exception e) {
            throw new BlockBenchFile.UnableToLoadFileError("Unable to load group", e);
        }
        previewColorType = json.get("color").getAsInt();
        export = json.get("export").getAsBoolean();
        mirrorUV = json.get("mirror_uv").getAsBoolean();
        isOpen = json.get("isOpen").getAsBoolean();
        locked = json.get("locked").getAsBoolean();
        visibility = json.get("visibility").getAsBoolean();
        autoUV = json.get("autouv").getAsInt();
        id = UUID.fromString(json.get("uuid").getAsString());
        children = new HashMap<>();
    }
}
