package kasuga.lib.core.client.block_bench_model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.block_bench_model.anim_model.AnimBlockBenchModel;
import kasuga.lib.core.client.block_bench_model.json_data.BlockBenchFile;
import kasuga.lib.core.client.block_bench_model.model.BlockBenchModel;
import kasuga.lib.core.client.model.ItemTransformProvider;
import kasuga.lib.core.resource.Resources;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class BlockBenchModelLoader implements IGeometryLoader<BlockBenchModel>, ResourceManagerReloadListener, ItemTransformProvider {

    public static final BlockBenchModelLoader INSTANCE = new BlockBenchModelLoader();

    private final HashMap<String, BlockBenchModel> models;

    private BlockBenchModelLoader() {
        this.models = new HashMap<>();
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {

    }

    public @Nullable BlockBenchModel getModel(String identifier) {
        return models.getOrDefault(identifier, null);
    }

    public boolean hasModel(String identifier) {
        return models.containsKey(identifier);
    }

    public @Nullable AnimBlockBenchModel getAnimModel(String identifier, RenderType type) {
        if (!hasModel(identifier)) return null;
        BlockBenchModel model = models.get(identifier);
        return new AnimBlockBenchModel(model, type);
    }

    @Override
    public BlockBenchModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        ResourceLocation loc = new ResourceLocation(jsonObject.get("model").getAsString());
        ResourceLocation modelLocation = new ResourceLocation(loc.getNamespace(), "models/" + loc.getPath() + ".bbmodel");
        String modelIdentifier = jsonObject.has("identifier") ? jsonObject.get("identifier").getAsString() : null;
        try {
            Resource resource = Resources.getResource(modelLocation);
            BlockBenchFile file = new BlockBenchFile(JsonParser.parseReader(resource.openAsReader()).getAsJsonObject());
            BlockBenchModel model = new BlockBenchModel(file);
            if (modelIdentifier != null) {
                models.put(modelIdentifier, model);
            }
            return model;
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Failed to load block bench model", e);
            return null;
        }
    }
}
