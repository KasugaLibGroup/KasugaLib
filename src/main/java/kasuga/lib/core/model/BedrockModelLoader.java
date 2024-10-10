package kasuga.lib.core.model;

import com.google.common.collect.Maps;
import com.google.gson.*;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.model.base.Geometry;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

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
        this.unbakedCache.clear();
    }

    @Override
    public UnbakedBedrockModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        ResourceLocation ml = new ResourceLocation(jsonObject.get("model").getAsString());
        if (unbakedCache.containsKey(ml)) return unbakedCache.get(ml);

        boolean flag = jsonObject.has("render_type");
        UnbakedBedrockModel model = new UnbakedBedrockModel(new ResourceLocation(ml.getNamespace(), "models/" + ml.getPath() + ".json"),
                new ResourceLocation(jsonObject.get("texture").getAsString()),
                flag ? ForgeRenderTypes.valueOf(jsonObject.get("render_type").getAsString()).get()
                        : RenderType.solid());
        unbakedCache.put(ml, model);
        return model;
    }

    @Override
    public HashMap<ItemTransforms.TransformType, ItemTransform> generate(JsonObject jsonObject, Type type, JsonDeserializationContext context) {
        ResourceLocation ml = new ResourceLocation(jsonObject.get("model").getAsString());
        JsonArray geoJson;
        try {
            Resource resource = Resources.getResource(new ResourceLocation(ml.getNamespace(), "models/" + ml.getPath() + ".json"));
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
}
