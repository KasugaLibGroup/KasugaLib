package kasuga.lib.mixins.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.KasugaLibClient;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {

    @Final
    @Shadow
    private RenderBuffers renderBuffers;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V"))
    public void doRender(PoseStack pPoseStack, float pPartialTick,
                         long pFinishNanoTime, boolean pRenderBlockOutline,
                         Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture,
                         Matrix4f pProjectionMatrix, CallbackInfo ci) {
        MultiBufferSource.BufferSource bufferSource = renderBuffers.bufferSource();
        pPoseStack.pushPose();
        Player player = Minecraft.getInstance().player;
        Vec3 pos = player.getPosition(pPartialTick);
        pPoseStack.translate(-pos.x(), -pos.y(), -pos.z());

        KasugaLibClient.PANEL_RENDERERS.forEach(
                renderer -> {
                    pPoseStack.pushPose();
                    renderer.render(pPoseStack, bufferSource, LightTexture.FULL_BLOCK, 0, pPartialTick);
                    pPoseStack.popPose();
                }
        );
        pPoseStack.popPose();
    }
}
