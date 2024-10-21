package kasuga.lib.core.client.model.anim_json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.core.util.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

@OnlyIn(Dist.CLIENT)
public class AnimationFile {
    public final String formatVersion;

    public final ResourceLocation location;
    private final JsonObject animationsJson;
    private final HashMap<String, Animation> animations;
    public static final HashMap<ResourceLocation, AnimationFile> FILES = new HashMap<>();
    public static final HashSet<ResourceLocation> UNREGISTERED = new HashSet<>();
    public static boolean filesLoaded = false;

    public AnimationFile(ResourceLocation location, JsonObject json) {
        this.location = location;
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
                        Animation animation = new Animation(name, object, this);
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
        final ResourceLocation location1 =
                new ResourceLocation(location.getNamespace(), "animations/" + location.getPath() + ".animation.json");
        if (!filesLoaded) {
            UNREGISTERED.add(location);
            return LazyRecomputable.of(() -> FILES.getOrDefault(location, null));
        } else if (FILES.containsKey(location)) {
            return LazyRecomputable.of(() -> FILES.get(location));
        }
        try {
            Resource resource = Resources.getResource(location1);
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());
            JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
            AnimationFile file = new AnimationFile(location1, object);
            FILES.put(location, file);
            return LazyRecomputable.of(() -> file);
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Failed to open animation file " + location1, e);
            return LazyRecomputable.of(() -> null);
        }
    }
}
