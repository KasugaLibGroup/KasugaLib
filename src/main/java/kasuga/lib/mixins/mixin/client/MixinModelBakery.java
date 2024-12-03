package kasuga.lib.mixins.mixin.client;

import kasuga.lib.core.client.model.BedrockModelLoader;
import kasuga.lib.core.client.model.ModelPreloadManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Set;

@Mixin(ModelBakery.class)
public class MixinModelBakery {

//    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z"))
//    private boolean addAll(Set<Material> instance, Collection<Material> es) {
//        return instance.addAll(es) & instance.addAll(BedrockModelLoader.ADDITIONAL_MATERIALS);
//    }
}
