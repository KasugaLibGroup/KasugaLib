package kasuga.lib.mixins.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import kasuga.lib.core.client.world_overlay.WorldOverlayRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {

    @Shadow @Final private RenderBuffers renderBuffers;

    @Shadow @Nullable private ClientLevel level;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V"))
    private void doRenderOverlays(PoseStack pPoseStack, float pPartialTick,
                                  long pFinishNanoTime, boolean pRenderBlockOutline,
                                  Camera pCamera, GameRenderer pGameRenderer,
                                  LightTexture pLightTexture, Matrix4f pProjectionMatrix,
                                  CallbackInfo ci) {
        WorldOverlayRenderer.INSTANCE.render(pPoseStack, renderBuffers.bufferSource(), this.level, OverlayTexture.NO_OVERLAY, pPartialTick);
    }
}
