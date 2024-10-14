package kasuga.lib.core.model.anim_json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.util.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.util.HashMap;

public class AnimationFile {
    public final String formatVersion;
    private final JsonObject animationsJson;
    private final HashMap<String, Animation> animations;

    public AnimationFile(JsonObject json) {
        formatVersion = json.get("format_version").getAsString();
        animations = new HashMap<>();
        if (!json.has("animationsJson")) {
            animationsJson = new JsonObject();
            return;
        }
        animationsJson = json.getAsJsonObject("animations");
        parseAnim();
    }

    private void parseAnim() {
        animationsJson.entrySet().forEach(
                entry -> {
                    String name = entry.getKey();
                    JsonElement element = entry.getValue();
                    if (element instanceof JsonObject object) {
                        Animation animation = new Animation(name, object);
                        this.animations.put(name, animation);
                    }
                }
        );
    }

    public Animation getAnimation(String name) {
        return animations.getOrDefault(name, null);
    }

    public boolean hasAnimation(String name) {
        return animations.containsKey(name);
    }

    public static AnimationFile fromFile(ResourceLocation location) {
        try {
            Resource resource = Resources.getResource(location);
            JsonObject object = JsonParser.parseReader(resource.openAsReader()).getAsJsonObject();
            return new AnimationFile(object);
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Failed to open animation file " + location, e);
            return null;
        }
    }
}
