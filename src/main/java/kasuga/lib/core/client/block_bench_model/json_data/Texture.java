package kasuga.lib.core.client.block_bench_model.json_data;

import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Texture {

    private final String path, relativePath, name,
            folder, id, namespace, group,
            renderMode, renderSides, frameOrderType,
            frameOrder, syncToProject;

    private final int width, height,
            uvWidth, uvHeight, frameTime;

    private final UUID uuid;

    private final boolean particle, useAsDefault,
            layersEnabled, frameInterpolate,
            visible, internal, saved;

    private final TextureSource source;

    public Texture(JsonObject json) {
        path = json.get("path").getAsString();
        relativePath = json.get("relative_path").getAsString();
        name = json.get("name").getAsString();
        folder = json.get("folder").getAsString();
        namespace = json.get("namespace").getAsString();
        group = json.get("group").getAsString();
        renderMode = json.get("render_mode").getAsString();
        renderSides = json.get("render_sides").getAsString();
        frameOrderType = json.get("frame_order_type").getAsString();
        frameOrder = json.get("frame_order").getAsString();
        syncToProject = json.get("sync_to_project").getAsString();
        id = json.get("id").getAsString();

        width = json.get("width").getAsInt();
        height = json.get("height").getAsInt();
        uvWidth = json.get("uv_width").getAsInt();
        uvHeight = json.get("uv_height").getAsInt();
        frameTime = json.get("frame_time").getAsInt();

        particle = json.get("particle").getAsBoolean();
        useAsDefault = json.get("use_as_default").getAsBoolean();
        layersEnabled = json.get("layers_enabled").getAsBoolean();
        frameInterpolate = json.get("frame_interpolate").getAsBoolean();
        visible = json.get("visible").getAsBoolean();
        internal = json.get("internal").getAsBoolean();
        saved = json.get("saved").getAsBoolean();

        uuid = UUID.fromString(json.get("uuid").getAsString());

        source = new TextureSource(json.get("source").getAsString());
    }
}
