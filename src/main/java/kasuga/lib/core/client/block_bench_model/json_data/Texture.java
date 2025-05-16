package kasuga.lib.core.client.block_bench_model.json_data;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import kasuga.lib.core.client.block_bench_model.model.BlockBenchElement;
import kasuga.lib.core.client.render.texture.Vec2f;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class Texture {

    private final String path, relativePath, name,
            folder, id, namespace, group,
            renderMode, renderSides, frameOrderType,
            frameOrder, syncToProject;

    private final int arrayIndex;
    @Setter
    @NonNull
    private Vec2f scaleFactor;

    @Setter
    private int width, height,
            uvWidth, uvHeight, frameTime;

    private final UUID uuid;

    private final boolean particle, useAsDefault,
            layersEnabled, frameInterpolate,
            visible, internal, saved;

    private final TextureSource source;

    public Texture(int arrayIndex, JsonObject json) {
        this.arrayIndex = arrayIndex;
        scaleFactor = new Vec2f(1, 1);
        path = json.get("path").getAsString();
        relativePath = json.get("relative_path").getAsString();
        name = json.get("name").getAsString();
        folder = json.get("folder").getAsString();
        namespace = json.get("namespace").getAsString();
        group = json.has("group") ?
                json.get("group").getAsString() : "";
        renderMode = json.get("render_mode").getAsString();
        renderSides = json.get("render_sides").getAsString();
        frameOrderType = json.get("frame_order_type").getAsString();
        frameOrder = json.get("frame_order").getAsString();
        syncToProject = json.has("sync_to_project") ?
                json.get("sync_to_project").getAsString() : "";
        id = json.get("id").getAsString();

        width = json.has("width") ?
                json.get("width").getAsInt() : -1;
        height = json.has("height") ?
                json.get("height").getAsInt() : -1;
        uvWidth = json.has("uv_width") ?
                json.get("uv_width").getAsInt() : -1;
        uvHeight = json.has("uv_height") ?
                json.get("uv_height").getAsInt() : -1;
        frameTime = json.has("frame_time") ?
                json.get("frame_time").getAsInt() : 0;

        particle = json.get("particle").getAsBoolean();
        useAsDefault = json.has("use_as_default") &&
                json.get("use_as_default").getAsBoolean();
        layersEnabled = json.has("layers_enabled") &&
                json.get("layers_enabled").getAsBoolean();
        frameInterpolate = json.has("frame_interpolate") &&
                json.get("frame_interpolate").getAsBoolean();
        visible = json.has("visible") &&
                json.get("visible").getAsBoolean();
        internal = json.has("internal") &&
                json.get("internal").getAsBoolean();
        saved = json.has("saved") &&
                json.get("saved").getAsBoolean();

        uuid = UUID.fromString(json.get("uuid").getAsString());

        source = new TextureSource(json.get("source").getAsString());
        if (width < 0 || height < 0 || uvWidth < 0 || uvHeight < 0) {
            try {
                NativeImage image = source.getImage();
                width = image.getWidth();
                height = image.getHeight();
                uvWidth = image.getWidth();
                uvHeight = image.getHeight();
            } catch (IOException e) {
                throw new BlockBenchFile.UnableToLoadFileError("Unable to read texture source.", e);
            }
        }
    }
}
