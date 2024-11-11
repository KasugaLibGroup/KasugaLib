package kasuga.lib.core.create.boundary;

import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.create.graph.EdgeExtraData;
import kasuga.lib.core.create.graph.GraphExtraData;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class CustomSegmentUtil {
    public static UUID getSegmentIdAt(
            TrackGraph graph,
            TrackEdge edge,
            EdgeData edgeData,
            EdgeExtraData extraData,
            EdgePointType<? extends CustomBoundary> boundaryType,
            ResourceLocation featureName,
            double position
    ){
        if(!extraData.hasCustomBoundaryInThisEdge(featureName)){
            return getEffectiveCircuit(graph,extraData,featureName);
        }
        CustomBoundary firstCircuit = edgeData.next(boundaryType,0);
        if(firstCircuit == null){
            return null;
        }
        UUID current = firstCircuit.getGroupId(false);

        for (TrackEdgePoint trackEdgePoint : edgeData.getPoints()) {
            if (!(trackEdgePoint instanceof CustomBoundary customBoundary))
                continue;
            if (customBoundary.getLocationOn(edge) >= position)
                return current;
            current = customBoundary.getGroupId(true);
        }
        return current;
    }

    public static CustomTrackSegment getSegment(
            TrackGraph graph,
            TrackEdge edge,
            EdgePointType<? extends CustomBoundary> boundaryType,
            double position
    ){
        GraphExtraData extraData = KasugaLib.STACKS.RAILWAY.get().withGraph(graph);

        ResourceLocation featureName = BoundarySegmentRegistry.getFeatureName(boundaryType);
        UUID segmentId = getSegmentIdAt(
                graph,
                edge,
                edge.getEdgeData(),
                extraData.getEdgeData(edge),
                boundaryType,
                featureName,
                position
        );

        return extraData.getSegment(featureName, segmentId);
    }

    public static UUID getEffectiveCircuit(TrackGraph graph, EdgeExtraData extraData, ResourceLocation featureName){
        return !extraData.hasBoundaryFeature(featureName) ? null : (
                extraData.getBoundaryFeature(featureName) == EdgeExtraData.passiveBoundaryGroup ?
                        graph.id : extraData.getBoundaryFeature(featureName)
        );
    }
}
