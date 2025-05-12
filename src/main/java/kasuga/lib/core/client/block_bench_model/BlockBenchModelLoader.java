package kasuga.lib.core.client.block_bench_model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.block_bench_model.json_data.BlockBenchFile;
import kasuga.lib.core.client.block_bench_model.model.BlockBenchModel;
import kasuga.lib.core.client.model.ItemTransformProvider;
import kasuga.lib.core.resource.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

import java.io.IOException;

public class BlockBenchModelLoader implements IGeometryLoader<BlockBenchModel>, ResourceManagerReloadListener, ItemTransformProvider {

    public static final BlockBenchModelLoader INSTANCE = new BlockBenchModelLoader();

    private BlockBenchModelLoader() {

    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {

    }

    @Override
    public BlockBenchModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        ResourceLocation loc = new ResourceLocation(jsonObject.get("model").getAsString());
        ResourceLocation modelLocation = new ResourceLocation(loc.getNamespace(), "models/" + loc.getPath() + ".bbmodel");
        try {
            Resource resource = Resources.getResource(modelLocation);
            BlockBenchFile file = new BlockBenchFile(JsonParser.parseReader(resource.openAsReader()).getAsJsonObject());
            BlockBenchModel model = new BlockBenchModel(file);
            return model;
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Failed to load block bench model", e);
            return null;
        }
    }
}
