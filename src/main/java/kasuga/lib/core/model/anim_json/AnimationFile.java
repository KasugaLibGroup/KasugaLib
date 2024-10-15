package kasuga.lib.core.model.anim_json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.core.util.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class AnimationFile {
    public final String formatVersion;
    private final JsonObject animationsJson;
    private final HashMap<String, Animation> animations;
    public static final HashMap<ResourceLocation, AnimationFile> FILES = new HashMap<>();
    public static final HashSet<ResourceLocation> UNREGISTERED = new HashSet<>();
    public static boolean filesLoaded = false;

    public AnimationFile(JsonObject json) {
        formatVersion = json.get("format_version").getAsString();
        animations = new HashMap<>();
        if (!json.has("animations")) {
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

    public static LazyRecomputable<AnimationFile> fromFile(ResourceLocation location) {
        if (!filesLoaded) {
            UNREGISTERED.add(location);
            return LazyRecomputable.of(() -> FILES.getOrDefault(location, null));
        } else if (FILES.containsKey(location)) {
            return LazyRecomputable.of(() -> FILES.get(location));
        }
        try {
            Resource resource = Resources.getResource(location);
            JsonObject object = JsonParser.parseReader(resource.openAsReader()).getAsJsonObject();
            AnimationFile file = new AnimationFile(object);
            FILES.put(location, file);
            return LazyRecomputable.of(() -> file);
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Failed to open animation file " + location, e);
            return LazyRecomputable.of(() -> null);
        }
    }
}
