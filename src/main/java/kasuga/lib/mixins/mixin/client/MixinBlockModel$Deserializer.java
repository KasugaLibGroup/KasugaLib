package kasuga.lib.mixins.mixin.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.model.ItemTransformProvider;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.geometry.GeometryLoaderManager;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.HashMap;

@Mixin(BlockModel.Deserializer.class)
//@Mixin(targets = "net.minecraft.client.renderer.block/model/BlockModel$Deserializer")
public class MixinBlockModel$Deserializer {

    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockModel$Deserializer;getAmbientOcclusion(Lcom/google/gson/JsonObject;)Ljava/lang/Boolean;"))
    public void doDeserialize(JsonElement json, Type type, JsonDeserializationContext context, CallbackInfoReturnable<BlockModel> callBack) {
        JsonObject object= json.getAsJsonObject();
        if (!object.has("loader")) return;
        ResourceLocation location = new ResourceLocation(object.get("loader").getAsString());
        IGeometryLoader<?> loader = GeometryLoaderManager.get(location);
        if (!(loader instanceof ItemTransformProvider provider)) return;
        JsonObject transObject = new JsonObject();
        HashMap<ItemDisplayContext, ItemTransform> transforms = provider.generate(object, type, context);
        if (transforms == null) return;
        for (ItemDisplayContext tType : ItemDisplayContext.values()) {
            if (!transforms.containsKey(tType)) continue;
            ItemTransform t = transforms.get(tType);
            JsonArray rotation = VectorUtil.vec3fToJsonArray(t.rotation);
            JsonArray translation = VectorUtil.vec3fToJsonArray(t.translation);
            JsonArray scale = VectorUtil.vec3fToJsonArray(t.scale);
            JsonArray rightRotation = VectorUtil.vec3fToJsonArray(t.rightRotation);
            JsonObject transObj = new JsonObject();
            transObj.add("rotation", rotation);
            transObj.add("translation", translation);
            transObj.add("scale", scale);
            transObj.add("right_rotation", rightRotation);
            transObject.add(tType.getSerializedName(), transObj);
        }
        object.add("display", transObject);
    }
}
