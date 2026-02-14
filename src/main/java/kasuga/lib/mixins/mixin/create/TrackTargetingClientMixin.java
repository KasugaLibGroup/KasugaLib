package kasuga.lib.mixins.mixin.create;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.content.trains.track.TrackTargetingClient;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import kasuga.lib.core.create.edge_point.EdgePointOverlayRenderer;
import kasuga.lib.core.create.edge_point.EdgePointOverlayRendererRegistry;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(value = TrackTargetingClient.class, remap = false)
public class TrackTargetingClientMixin {
    @Unique
    @Shadow private static EdgePointType<?> lastType;

    @Shadow private static BezierTrackPointLocation lastHoveredBezierSegment;

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/trains/track/TrackTargetingBehaviour;render(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction$AxisDirection;Lcom/simibubi/create/content/trains/track/BezierTrackPointLocation;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILcom/simibubi/create/content/trains/track/TrackTargetingBehaviour$RenderedTrackOverlayType;F)V"
    ), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void onRenderCall(
            PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, // 原始参数
            CallbackInfo ci,
            // --- 以下是根据方法体推断的局部变量 ---
            Minecraft mc,
            BlockPos pos,
            int light,
            Direction.AxisDirection direction,
            TrackTargetingBehaviour.RenderedTrackOverlayType type // 这里捕获到了 type
    ) {
        // 1. 获取 Registry。
        // 因为这是静态环境，我们无法通过 this 获取实例。
        // 我们直接使用该类中的静态变量 lastType（如果 Mixin 能访问到）
        // 或者通过你的 EdgePointOverlayRendererRegistry 获取。

        // 注意：在 Create 源码中，lastType 是静态字段。
        // 我们假设 EdgePointOverlayRendererRegistry.getRendererFor 已经适配了 EdgePointType。

        // 关键：从静态上下文获取当前的 EdgePointType
        // 如果 lastType 是私有的，你需要用 @Shadow 声明它
        EdgePointOverlayRenderer renderer = EdgePointOverlayRendererRegistry.getRendererFor(lastType);

        if (renderer != null) {
            // 2. 使用捕获的变量进行渲染
            // 注意：lastHoveredBezierSegment 也是静态的，可以直接引用
            renderer.renderOverlay(
                    mc.level,
                    pos,
                    direction,
                    lastHoveredBezierSegment,
                    ms,
                    buffer,
                    light,
                    OverlayTexture.NO_OVERLAY,
                    1 + 1 / 16f
            );

            // 3. 成功则取消后续原版渲染
            ci.cancel();
        }
    }
}
