package kasuga.lib.core.client.model;

import com.google.common.collect.Maps;
import com.google.gson.*;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.model.model_json.UnbakedBedrockModel;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import kasuga.lib.core.client.model.model_json.Geometry;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BedrockModelLoader implements IModelLoader<UnbakedBedrockModel>, ResourceManagerReloadListener, ItemTransformProvider {

    public static BedrockModelLoader INSTANCE = new BedrockModelLoader();
    private ResourceManager manager;
    public static final HashSet<ResourceLocation> UNREGISTERED = new HashSet<>();
    public static final HashSet<Material> ADDITIONAL_MATERIALS = new HashSet<>();
    public static boolean registerFired = false;
    public static final HashMap<ResourceLocation, UnbakedBedrockModel> MODELS = new HashMap<>();
    public static final ResourceLocation MISSING_MODEL_LOCATION = new ResourceLocation(KasugaLib.MOD_ID, "default/missing_model");
    private static final LazyRecomputable<UnbakedBedrockModel> MISSING = new LazyRecomputable<>(() -> MODELS.get(MISSING_MODEL_LOCATION));

    public BedrockModelLoader() {}
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.manager = resourceManager;
        // this.unbakedCache.clear();
    }

    @Override
    public UnbakedBedrockModel read(@Nullable JsonDeserializationContext deserializationContext, JsonObject jsonObject) throws JsonParseException {
        ResourceLocation ml = new ResourceLocation(jsonObject.get("model").getAsString());
        boolean flipV = jsonObject.has("flip_v") && jsonObject.get("flip_v").getAsBoolean();

        UnbakedBedrockModel model = new UnbakedBedrockModel(new ResourceLocation(ml.getNamespace(), "models/" + ml.getPath() + ".geo.json"),
                new ResourceLocation(jsonObject.get("texture").getAsString()), flipV);
        if (jsonObject.has("particle")) {
            String particleStr = jsonObject.get("particle").getAsString();
            ResourceLocation location = new ResourceLocation(particleStr);
            Material material = new Material(TextureAtlas.LOCATION_PARTICLES, location);
            ADDITIONAL_MATERIALS.add(material);
        }
        if (deserializationContext == null) {
            ADDITIONAL_MATERIALS.add(model.getMaterial());
        }
        return model;
    }

    @Override
    public HashMap<ItemTransforms.TransformType, ItemTransform> generate(JsonObject jsonObject, Type type, JsonDeserializationContext context) {
        ResourceLocation ml = new ResourceLocation(jsonObject.get("model").getAsString());
        JsonArray geoJson;
        try {
            Resource resource = Resources.getResource(new ResourceLocation(ml.getNamespace(), "models/" + ml.getPath() + ".geo.json"));
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());
            JsonObject geo = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
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
        UnbakedBedrockModel unbaked = MODELS.getOrDefault(location, null);
        if (unbaked == null) return null;
        List<Geometry> geometry = unbaked.getGeometries();
        ArrayList<AnimModel> result = new ArrayList<>(geometry.size());
        geometry.forEach(g -> result.add(g.getAnimationModel(type)));
        return result;
    }

    public static AnimModel getModel(ResourceLocation location, RenderType type) {
        UnbakedBedrockModel unbaked = MODELS.getOrDefault(location, null);
        if (unbaked == null) return null;
        List<Geometry> geometries = unbaked.getGeometries();
        if (geometries.isEmpty()) return null;
        Geometry geometry = geometries.get(0);
        return geometry.getAnimationModel(type);
    }

    public static LazyRecomputable<UnbakedBedrockModel> fromFile(ResourceLocation location) {
        ResourceLocation location1 =
                new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json");
        if (!registerFired) {
            UNREGISTERED.add(location);
            return LazyRecomputable.of(() -> MODELS.getOrDefault(location, null));
        } else if (MODELS.containsKey(location)) {
            MODELS.get(location);
        }
        try {
            Resource resource = Resources.getResource(location1);
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                KasugaLib.MAIN_LOGGER.error(location + " is not a JsonObject");
                return MISSING;
            }
            UnbakedBedrockModel model = INSTANCE.read(null, element.getAsJsonObject());
            MODELS.put(location, model);
            return LazyRecomputable.of(() -> model);
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Failed to load model file" + location, e);
            return MISSING;
        }

    }
}
