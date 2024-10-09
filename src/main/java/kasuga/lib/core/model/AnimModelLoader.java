package kasuga.lib.core.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

import java.util.HashMap;

public class AnimModelLoader implements IGeometryLoader<UnbakedAnimModel>, ResourceManagerReloadListener {

    public static AnimModelLoader INSTANCE = new AnimModelLoader();
    private ResourceManager manager;
    private final HashMap<ResourceLocation, UnbakedAnimModel> unbakedCache;

    public AnimModelLoader() {
        this.unbakedCache = Maps.newHashMap();
    }
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.manager = resourceManager;
        this.unbakedCache.clear();
    }

    @Override
    public UnbakedAnimModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        ResourceLocation ml = new ResourceLocation(jsonObject.get("model").getAsString());
        if (unbakedCache.containsKey(ml)) return unbakedCache.get(ml);

        boolean flag = jsonObject.has("render_type");
        UnbakedAnimModel model = new UnbakedAnimModel(new ResourceLocation(ml.getNamespace(), "models/" + ml.getPath() + ".json"),
                new ResourceLocation(jsonObject.get("texture").getAsString()),
                flag ? ForgeRenderTypes.valueOf(jsonObject.get("render_type").getAsString()).get()
                        : RenderType.solid());
        unbakedCache.put(ml, model);
        return model;
    }
}
