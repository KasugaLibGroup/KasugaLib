package kasuga.lib.core.create.edge_point;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class EdgePointBlockEntityRenderer<T extends TrackEdgePoint>
        extends SmartBlockEntityRenderer<EdgePointBlockEntity<T>>
{
    public EdgePointBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(
            EdgePointBlockEntity<T> blockEntity,
            float partialTicks,
            PoseStack ms,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
    }
}
