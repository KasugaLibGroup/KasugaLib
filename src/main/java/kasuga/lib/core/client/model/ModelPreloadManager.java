package kasuga.lib.core.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.model.anim_json.AnimationFile;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ModelPreloadManager {

    public static final ModelPreloadManager INSTANCE = new ModelPreloadManager();
    public static final String PRELOAD_FILE = "anim_model_preload.json";
    public ModelPreloadManager() {}

    public void scan() {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Set<String> namespaces = manager.getNamespaces();
        HashMap<String, JsonObject> objs = new HashMap<>();
        namespaces.forEach(name -> {
            ResourceLocation loc = new ResourceLocation(name, PRELOAD_FILE);
            Optional<Resource> resource = Resources.attemptGetResource(loc);
            if (resource.isEmpty()) return;
            Resource r = resource.get();
            InputStreamReader reader = new InputStreamReader(r.getInputStream());
            JsonElement element = JsonParser.parseReader(reader);
            if (element.isJsonObject()) objs.put(name, element.getAsJsonObject());
        });
        objs.forEach((name, obj) -> {
            if (obj.has("model")) {
                JsonElement element = obj.get("model");
                if (element.isJsonArray()) {
                    for (JsonElement e : element.getAsJsonArray()) {
                        String str = e.getAsString();
                        BedrockModelLoader.fromFile(new ResourceLocation(str));
                    }
                } else if (element.isJsonPrimitive()) {
                    String str = element.getAsString();
                    BedrockModelLoader.fromFile(new ResourceLocation(str));
                }
            }
            if (obj.has("animation")) {
                JsonElement element = obj.get("animation");
                if (element.isJsonArray()) {
                    for (JsonElement e : element.getAsJsonArray()) {
                        String str = e.getAsString();
                        AnimationFile.fromFile(new ResourceLocation(str));
                    }
                } else if (element.isJsonPrimitive()) {
                    String str = element.getAsString();
                    AnimationFile.fromFile(new ResourceLocation(str));
                }
            }
        });
    }
}
