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

    @Redirect(method = "processLoading", at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z"), remap = false)
    private boolean addAll(Set instance, Collection<? extends Material> es) {
        BedrockModelLoader.registerFired = true;
        ModelPreloadManager.INSTANCE.scan();
        return instance.addAll(BedrockModelLoader.ADDITIONAL_MATERIALS);
    }
}
