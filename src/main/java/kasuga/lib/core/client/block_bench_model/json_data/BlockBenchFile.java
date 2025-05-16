package kasuga.lib.core.client.block_bench_model.json_data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.block_bench_model.anim.Animation;
import kasuga.lib.core.client.render.texture.Vec2f;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Getter
public class BlockBenchFile {

    private final BlockBenchMeta meta;

    private final String name, modelIdentifier, variablePlaceholders;
    private final Vector3f visibleBox;
    private final Vec2f resolution;
    private final HashMap<UUID, Element> elements;
    private final List<Texture> textures;
    private final List<Animation> animations;
    private final Outline outline;

    public BlockBenchFile(JsonObject json) throws UnableToLoadFileError {
        elements = new HashMap<>();
        textures = new ArrayList<>();
        animations = new ArrayList<>();
        meta = new BlockBenchMeta(json.getAsJsonObject("meta"));
        this.name = json.getAsJsonPrimitive("name").getAsString();
        this.modelIdentifier = json.getAsJsonPrimitive("model_identifier").getAsString();
        variablePlaceholders = json.getAsJsonPrimitive("variable_placeholders").getAsString();
        try {
            JsonArray visibleBoxArray = json.getAsJsonArray("visible_box");
            visibleBox = new Vector3f(
                    visibleBoxArray.get(0).getAsFloat(),
                    visibleBoxArray.get(1).getAsFloat(),
                    visibleBoxArray.get(2).getAsFloat()
            );
        } catch (Exception e) {
            throw new UnableToLoadFileError("Unable to parse visible box", e);
        }
        try {
            JsonObject jsonObject = json.getAsJsonObject("resolution");
            resolution = new Vec2f(
                    jsonObject.get("width").getAsFloat(),
                    jsonObject.get("height").getAsFloat());
        } catch (Exception e) {
            throw new UnableToLoadFileError("Unable to parse resolution", e);
        }
        try {
            JsonArray elementArray = json.getAsJsonArray("elements");
            for (JsonElement ele : elementArray) {
                Element element = new Element(this, ele.getAsJsonObject());
                elements.put(element.getId(), element);
            }
        } catch (Exception e) {
            throw new UnableToLoadFileError("Unable to parse elements", e);
        }
        try {
            JsonArray outlineArray = json.getAsJsonArray("outliner");
            outline = new Outline(this, outlineArray);
        } catch (Exception e) {
            throw new UnableToLoadFileError("Unable to parse outliner", e);
        }
        try {
            JsonArray texturesArray = json.getAsJsonArray("textures");
            for (JsonElement ele : texturesArray) {
                Texture texture = new Texture(ele.getAsJsonObject());
                textures.add(texture);
            }
        } catch (Exception e) {
            throw new UnableToLoadFileError("Unable to parse texture", e);
        }
        if (!json.has("animations")) return;
        JsonArray animationsArray = json.getAsJsonArray("animations");
        String animVarPlaceHolder = json.has("animation_variable_placeholders") ?
                json.get("animation_variable_placeholders").getAsString() : null;
        for (JsonElement ele : animationsArray) {
            try {
                JsonObject animationObj = ele.getAsJsonObject();
                Animation animation = new Animation(this, animationObj);
                animation.setAnimVarPlaceholders(animVarPlaceHolder);
                animations.add(animation);
            } catch (Exception e) {
                throw new UnableToLoadFileError("Unable to parse animation", e);
            }
        }
    }

    public static void main(String[] args) throws UnableToLoadFileError, IOException {
    }


    public static class UnableToLoadFileError extends RuntimeException {

        private final Throwable cause;
        private final String message;

        public UnableToLoadFileError(String message, Throwable cause) {
            this.cause = cause;
            this.message = message;
        }

        @Override
        public void printStackTrace() {
            if (cause != null) cause.printStackTrace();
            KasugaLib.MAIN_LOGGER.error(message);
            super.printStackTrace();
        }
    }
}
