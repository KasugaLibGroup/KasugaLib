package kasuga.lib.core.client.model;

import com.google.common.collect.Maps;
import com.google.gson.*;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.model.model_json.Geometry;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public interface ItemTransformProvider {
    @Nullable
    default HashMap<ItemTransforms.TransformType, ItemTransform> generate(JsonObject jsonObject, Type type, JsonDeserializationContext context) {
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
}
