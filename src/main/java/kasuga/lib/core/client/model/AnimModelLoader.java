package kasuga.lib.core.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import kasuga.lib.core.client.model.model_json.BedrockModel;
import kasuga.lib.core.client.model.model_json.Geometry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;

public class AnimModelLoader implements IGeometryLoader<AnimModel>, ResourceManagerReloadListener, ItemTransformProvider {

    private ResourceManager manager;
    private final HashMap<ResourceLocation, AnimModel> MODELS;

    public static final AnimModelLoader INSTANCE = new AnimModelLoader();

    private AnimModelLoader() {
        this.MODELS = new HashMap<>();
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        this.manager = pResourceManager;
    }

    @Override
    public AnimModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        BedrockModel model = BedrockModelLoader.readModel(jsonObject, deserializationContext);
        if (model == null) return null;
        ResourceLocation renderTypeHint;
        if (jsonObject.has("render_type")) {
            renderTypeHint = new ResourceLocation(jsonObject.get("render_type").getAsString());
        } else {
            renderTypeHint = new ResourceLocation("solid");
        }
        String geometry = jsonObject.get("geometry").getAsString();
        Geometry geo = null;
        for (Geometry g : model.getGeometries()) {
            if (g.getDescription().getIdentifier().equals(geometry)) {
                geo = g;
                break;
            }
        }
        if (geo == null) return null;
        AnimModel animModel = new AnimModel(geo, model.getMaterials(), renderTypeHint);
        if (jsonObject.has("identifier")) {
            ResourceLocation identifier = new ResourceLocation(jsonObject.get("identifier").getAsString());
            if (MODELS.containsKey(identifier)) {
                KasugaLib.MAIN_LOGGER.warn("Anim Model: " + identifier + " already exists, COVERED!");
            }
            MODELS.put(identifier, animModel);
        } else {
            KasugaLib.MAIN_LOGGER.warn("Anim Model: " + model.modelLocation + " has no identifier, " +
                    "and it WOULD NOT BE ABLE TO USE IN RENDERERS!");
        }
        return animModel;
    }

    public @Nullable AnimModel getModel(ResourceLocation location) {
        AnimModel model = MODELS.get(location);
        if (model == null) return null;
        model.init();
        return model;
    }
}
