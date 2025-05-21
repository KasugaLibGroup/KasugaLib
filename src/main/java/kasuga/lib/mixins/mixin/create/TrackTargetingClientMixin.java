package kasuga.lib.mixins.mixin.create;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.content.trains.track.TrackTargetingClient;
import kasuga.lib.core.create.edge_point.EdgePointOverlayRenderer;
import kasuga.lib.core.create.edge_point.EdgePointOverlayRendererRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrackTargetingClient.class, remap = false)
public class TrackTargetingClientMixin {
    @Shadow private static EdgePointType<?> lastType;

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/trains/track/TrackTargetingBehaviour;render(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction$AxisDirection;Lcom/simibubi/create/content/trains/track/BezierTrackPointLocation;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILcom/simibubi/create/content/trains/track/TrackTargetingBehaviour$RenderedTrackOverlayType;F)V"
    ), cancellable = true)
    void onRenderCall(
            LevelAccessor level,
            BlockPos pos,
            Direction.AxisDirection direction,
            BezierTrackPointLocation bezier,
            PoseStack ms,
            MultiBufferSource buffer,
            int light,
            int overlay,
            TrackTargetingBehaviour.RenderedTrackOverlayType type,
            float scale,
            CallbackInfo callbackInfo
    ){
        EdgePointOverlayRenderer renderer = EdgePointOverlayRendererRegistry.getRendererFor(lastType);
        if(renderer == null){
            return;
        }
        renderer.renderOverlay(level, pos, direction, bezier, ms, buffer, light, overlay, scale);
        callbackInfo.cancel();
    }
}
