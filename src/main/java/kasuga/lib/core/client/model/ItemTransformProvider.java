package kasuga.lib.core.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;

public interface ItemTransformProvider {
    @Nullable
    HashMap<ItemDisplayContext, ItemTransform> generate(JsonObject jsonObject, Type type, JsonDeserializationContext context);
}
