package kasuga.lib.mixins.mixin.client;

import kasuga.lib.core.KasugaLibStacks;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(DirectoryLister.class)
public class MixinDirectoryLister {

    @Inject(method = "run", at = @At(value = "RETURN"))
    private void doRun(ResourceManager pResourceManager, SpriteSource.Output pOutput, CallbackInfo ci) {
        KasugaLibStacks.getAdditionAtlasManager().forEach(manager -> {
            try {
                manager.runLoadingResources();
                manager.registerAtlas(pOutput);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
