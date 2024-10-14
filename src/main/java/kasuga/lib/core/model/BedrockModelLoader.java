package kasuga.lib.core.model;

import com.google.common.collect.Maps;
import com.google.gson.*;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.model.anim_model.AnimModel;
import kasuga.lib.core.model.model_json.Geometry;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BedrockModelLoader implements IGeometryLoader<UnbakedBedrockModel>, ResourceManagerReloadListener, ItemTransformProvider {

    public static BedrockModelLoader INSTANCE = new BedrockModelLoader();
    private ResourceManager manager;
    private final HashMap<ResourceLocation, UnbakedBedrockModel> unbakedCache;

    public BedrockModelLoader() {
        this.unbakedCache = Maps.newHashMap();
    }
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.manager = resourceManager;
        // this.unbakedCache.clear();
    }

    @Override
    public UnbakedBedrockModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        ResourceLocation ml = new ResourceLocation(jsonObject.get("model").getAsString());
        boolean flipV = jsonObject.has("flip_v") && jsonObject.get("flip_v").getAsBoolean();
        if (unbakedCache.containsKey(ml)) return unbakedCache.get(ml);

        UnbakedBedrockModel model = new UnbakedBedrockModel(new ResourceLocation(ml.getNamespace(), "models/" + ml.getPath() + ".geo.json"),
                new ResourceLocation(jsonObject.get("texture").getAsString()), flipV);
        unbakedCache.put(ml, model);
        return model;
    }

    @Override
    public HashMap<ItemTransforms.TransformType, ItemTransform> generate(JsonObject jsonObject, Type type, JsonDeserializationContext context) {
        ResourceLocation ml = new ResourceLocation(jsonObject.get("model").getAsString());
        JsonArray geoJson;
        try {
            Resource resource = Resources.getResource(new ResourceLocation(ml.getNamespace(), "models/" + ml.getPath() + ".geo.json"));
            JsonObject geo = JsonParser.parseReader(resource.openAsReader()).getAsJsonObject();
            geoJson = geo.getAsJsonArray("minecraft:geometry");
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Failed to read Model: ", e);
            return null;
        }
        if (geoJson == null) {
            KasugaLib.MAIN_LOGGER.error("Failed to parse Model: " + ml);
            return null;
        }
        HashMap<ItemTransforms.TransformType, ItemTransform> result = Maps.newHashMap();
        for (JsonElement geometry : geoJson.getAsJsonArray()) {
            result.putAll(Geometry.parseTransforms(geometry.getAsJsonObject()));
        }
        return result;
    }

    public static List<AnimModel> getModels(ResourceLocation location, RenderType type) {
        UnbakedBedrockModel unbaked = INSTANCE.unbakedCache.getOrDefault(location, null);
        if (unbaked == null) return null;
        List<Geometry> geometry = unbaked.getGeometries();
        ArrayList<AnimModel> result = new ArrayList<>(geometry.size());
        geometry.forEach(g -> result.add(g.getAnimationModel(type)));
        return result;
    }

    public static AnimModel getModel(ResourceLocation location, RenderType type) {
        UnbakedBedrockModel unbaked = INSTANCE.unbakedCache.getOrDefault(location, null);
        if (unbaked == null) return null;
        List<Geometry> geometries = unbaked.getGeometries();
        if (geometries.isEmpty()) return null;
        Geometry geometry = geometries.get(0);
        return geometry.getAnimationModel(type);
    }
}
