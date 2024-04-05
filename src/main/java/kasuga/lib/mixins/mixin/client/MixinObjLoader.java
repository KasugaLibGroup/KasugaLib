package kasuga.lib.mixins.mixin.client;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.obj.OBJLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OBJLoader.class)
@OnlyIn(Dist.CLIENT)
public class MixinObjLoader {

    @Redirect(method = "read(Lcom/google/gson/JsonDeserializationContext;Lcom/google/gson/JsonObject;)Lnet/minecraftforge/client/model/obj/OBJModel;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/GsonHelper;getAsBoolean(Lcom/google/gson/JsonObject;Ljava/lang/String;Z)Z"))
    protected boolean doGetAsBoolean(JsonObject pJson, String pMemberName, boolean pFallback) {
        if (pMemberName.equals("flip-v")) {
            if (pJson.has("flip-v")) return pJson.get("flip-v").getAsBoolean();
            if (pJson.has("flip_v")) return pJson.get("flip_v").getAsBoolean();
            return false;
        }
        return GsonHelper.getAsBoolean(pJson, pMemberName, pFallback);
    }
}
