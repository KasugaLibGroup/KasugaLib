package kasuga.lib.mixins.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import kasuga.lib.core.KasugaAtlasSprite;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModelManager.class)
public class MixinModelManager {

    /*
    @Redirect(method = "lambda$loadModels$15", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/AtlasSet$StitchResult;getSprite(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"))
    private static TextureAtlasSprite doGetSprite(AtlasSet.StitchResult instance, ResourceLocation pLocation) {
        TextureAtlasSprite sprite = instance.getSprite(pLocation);
        if (sprite != null) return sprite;
        try {
            ResourceLocation loadLocation = new ResourceLocation(pLocation.getNamespace(), "textures/" + pLocation.getPath() + ".png");
            Resource resource = Resources.getResource(loadLocation);
            NativeImage image = NativeImage.read(resource.open());
            SpriteContents contents = new SpriteContents(pLocation,
                    new FrameSize(image.getWidth(), image.getHeight()), image,
                    AnimationMetadataSection.EMPTY, ForgeTextureMetadata.forResource(resource)
            );
            return new KasugaAtlasSprite(pLocation, contents);
        } catch (Exception e) {
            return null;
        }
    }
    
     */
}
