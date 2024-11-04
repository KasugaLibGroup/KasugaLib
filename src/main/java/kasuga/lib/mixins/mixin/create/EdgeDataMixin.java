package kasuga.lib.mixins.mixin.create;

import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.create.boundary.BoundarySegmentRegistry;
import kasuga.lib.core.create.boundary.CustomBoundary;
import kasuga.lib.core.create.graph.EdgeExtraData;
import kasuga.lib.core.create.graph.TrackEdgeLocation;
import kasuga.lib.core.util.StackTraceUtil;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = EdgeData.class, remap = false)
public class EdgeDataMixin {
    @Shadow private TrackEdge edge;

    @Inject(method = "addPoint",at=@At("HEAD"))
    private void onAddPoint(TrackGraph graph, TrackEdgePoint point, CallbackInfo ci){
        if (!(point instanceof CustomBoundary customBoundary)) {
            return;
        }
        ResourceLocation boundaryFeature = BoundarySegmentRegistry.getFeatureName(customBoundary);

        if(boundaryFeature == null)
            return;
        EdgeExtraData edgeData = KasugaLib.STACKS.RAILWAY.get().withGraph(graph).getEdgeData(edge);


        KasugaLib.STACKS.RAILWAY.debugStream.printf("F=|EdgeDataMixin.onRemovePoint|%s|%s|%s|%s|%s\n",
                graph.id,
                TrackEdgeLocation.fromEdge(edge),
                edgeData.getBoundaryFeature(boundaryFeature),
                null,
                StackTraceUtil.writeStackTrace()
        );

        KasugaLib.STACKS.RAILWAY.get().withGraph(graph).getEdgeData(edge).setBoundaryFeature(boundaryFeature, null);
    }

    @Inject(method = "removePoint",at=@At("TAIL"))
    private void onRemovePoint(TrackGraph graph, TrackEdgePoint point, CallbackInfo ci){
        EdgeData self = (EdgeData)(Object) this;

        if (!(point instanceof CustomBoundary customBoundary)) {
            return;
        }

        ResourceLocation boundaryFeature = BoundarySegmentRegistry.getFeatureName(customBoundary);

        if(boundaryFeature == null)
            return;

        UUID nextId = self.next(point.getType(), 0) == null ? EdgeExtraData.passiveBoundaryGroup : null;
        EdgeExtraData edgeData = KasugaLib.STACKS.RAILWAY.get().withGraph(graph).getEdgeData(edge);
        KasugaLib.STACKS.RAILWAY.debugStream.printf("F=|EdgeDataMixin.onRemovePoint|%s|%s|%s|%s|%s\n",
                graph.id,
                TrackEdgeLocation.fromEdge(edge),
                edgeData.getBoundaryFeature(boundaryFeature),
                nextId,
                StackTraceUtil.writeStackTrace()
        );
        edgeData.setBoundaryFeature(boundaryFeature, nextId);
    }
}
