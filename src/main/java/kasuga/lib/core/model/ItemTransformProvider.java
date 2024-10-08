package kasuga.lib.core.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;

public interface ItemTransformProvider {
    @Nullable
    HashMap<ItemTransforms.TransformType, ItemTransform> generate(JsonObject jsonObject, Type type, JsonDeserializationContext context);
}
