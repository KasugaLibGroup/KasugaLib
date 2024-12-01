package kasuga.lib.core.client.model;

import com.google.common.collect.Maps;
import com.google.gson.*;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.model.model_json.BedrockModel;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import kasuga.lib.core.client.model.model_json.Geometry;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BedrockModelLoader implements IGeometryLoader<BedrockModel>, ResourceManagerReloadListener, ItemTransformProvider {

    public static BedrockModelLoader INSTANCE = new BedrockModelLoader();
    private ResourceManager manager;
    public static final HashMap<ResourceLocation, BedrockModel> MODELS = new HashMap<>();
    public static final ResourceLocation MISSING_MODEL_LOCATION = new ResourceLocation(KasugaLib.MOD_ID, "default/missing_model");
    private static final LazyRecomputable<BedrockModel> MISSING = new LazyRecomputable<>(() -> MODELS.get(MISSING_MODEL_LOCATION));

    public BedrockModelLoader() {}
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.manager = resourceManager;
        // this.unbakedCache.clear();
    }

    @Override
    public BedrockModel read(JsonObject jsonObject, @Nullable JsonDeserializationContext deserializationContext) throws JsonParseException {
        return readModel(jsonObject, deserializationContext);
    }

    public static BedrockModel readModel(JsonObject jsonObject, @Nullable JsonDeserializationContext context) {
        ResourceLocation ml = new ResourceLocation(jsonObject.get("model").getAsString());
        boolean flipV = jsonObject.has("flip_v") && jsonObject.get("flip_v").getAsBoolean();

        ArrayList<Material> materials = new ArrayList<>();
        Material texture = new Material(TextureAtlas.LOCATION_BLOCKS,
                new ResourceLocation(jsonObject.get("texture").getAsString()));
        materials.add(texture);
        if (jsonObject.has("particle")) {
            String particleStr = jsonObject.get("particle").getAsString();
            ResourceLocation location = new ResourceLocation(particleStr);
            materials.add(new Material(TextureAtlas.LOCATION_PARTICLES, location));
        }
        BedrockModel model = new BedrockModel(
                new ResourceLocation(ml.getNamespace(), "models/" + ml.getPath() + ".geo.json"),
                flipV, texture, materials);
        ResourceLocation identifier = new ResourceLocation(jsonObject.get("identifier").getAsString());
        MODELS.put(identifier, model);
        return model;
    }

    public static List<AnimModel> getModels(ResourceLocation location, RenderType type) {
        BedrockModel unbaked = MODELS.getOrDefault(location, null);
        if (unbaked == null) return List.of();
        List<Geometry> geometry = unbaked.getGeometries();
        ArrayList<AnimModel> result = new ArrayList<>(geometry.size());
        geometry.forEach(g -> result.add(g.getAnimationModel(type)));
        return result;
    }

    public static AnimModel getModel(ResourceLocation location, RenderType type) {
        BedrockModel unbaked = MODELS.getOrDefault(location, null);
        if (unbaked == null) return null;
        List<Geometry> geometries = unbaked.getGeometries();
        if (geometries.isEmpty()) return null;
        Geometry geometry = geometries.get(0);
        return geometry.getAnimationModel(type);
    }
}
