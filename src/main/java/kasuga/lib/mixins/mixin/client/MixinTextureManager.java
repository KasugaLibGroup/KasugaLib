package kasuga.lib.mixins.mixin.client;

import kasuga.lib.core.util.Resources;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TextureManager.class)
public abstract class MixinTextureManager {

    @Shadow
    protected abstract AbstractTexture loadTexture(ResourceLocation pPath, AbstractTexture texture);

    @Redirect(
            method =
                    "register(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/renderer/texture/AbstractTexture;)V",
            at =
            @At(
                    value = "INVOKE",
                    target =
                            "Lnet/minecraft/client/renderer/texture/TextureManager;loadTexture(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/renderer/texture/AbstractTexture;)Lnet/minecraft/client/renderer/texture/AbstractTexture;"),
            remap = false)
    public AbstractTexture doLoadTexture(
            TextureManager instance, ResourceLocation path, AbstractTexture texture) {
        if (path instanceof Resources.CheatResourceLocation)
            return texture;
        return loadTexture(path, texture);
    }
}
