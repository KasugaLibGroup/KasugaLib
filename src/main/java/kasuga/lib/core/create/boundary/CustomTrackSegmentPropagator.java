package kasuga.lib.core.create.boundary;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.*;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.create.graph.EdgeExtraData;
import kasuga.lib.core.create.graph.GraphExtraData;
import kasuga.lib.core.create.graph.TrackEdgeLocation;
import kasuga.lib.core.util.StackTraceUtil;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Predicate;

public class CustomTrackSegmentPropagator{
    public static void propagate(TrackGraph graph, CustomBoundary boundary, boolean direction){
        KasugaLib.STACKS.RAILWAY.debugStream.printf("C|Propagator.propagate|%s|%s|%s|%s\n",
                graph.id,
                boundary.id,
                direction,
                StackTraceUtil.writeStackTrace()
        );
        UUID segmentId = UUID.randomUUID();
        CustomTrackSegment segment = BoundarySegmentRegistry.createSegment(boundary, segmentId);
        ResourceLocation featureName = BoundarySegmentRegistry.getFeatureName(boundary);
        GraphExtraData extraData = KasugaLib.STACKS.RAILWAY.get().withGraph(graph);

        TrackGraphSync sync = Create.RAILWAYS.sync;

        boundary.setSegment(direction, segmentId);
        walk(
                graph,
                featureName,
                boundary,
                (EdgePointType<? super CustomBoundary>) boundary.getType(),
                direction,
                (val)->{
                    TrackNode node = val.getFirst();
                    CustomBoundary newBoundary = (CustomBoundary) val.getSecond();
                    UUID currentGroup = newBoundary.getGroupId(newBoundary.isPrimary(node));
                    if(extraData.hasSegment(featureName, currentGroup)){
                        KasugaLib.STACKS.RAILWAY.debugStream.printf("S-|Propagator.propagate$B|%s|%s|%s|%s\n",
                                graph.id,
                                featureName,
                                currentGroup,
                                StackTraceUtil.writeStackTrace()
                        );
                        extraData.removeSegment(featureName, currentGroup);
                    }
                    KasugaLib.STACKS.RAILWAY.debugStream.printf("BF+|Propagator.propagate$B|%s|%s|%s|%s\n",
                            graph.id,
                            node.getLocation().getLocation(),
                            segmentId,
                            StackTraceUtil.writeStackTrace()
                    );
                    newBoundary.setSegmentAndUpdate(newBoundary.isPrimary(node), segmentId);
                    sync.pointAdded(graph, newBoundary);
                    return true;
                },
                (val)->{
                    if(val.hasBoundaryFeature(featureName) && val.getBoundaryFeature(featureName) != EdgeExtraData.passiveBoundaryGroup){
                        KasugaLib.STACKS.RAILWAY.debugStream.printf("S-|Propagator.propagate$E|%s|%s|%s|%s\n",
                                graph.id,
                                featureName,
                                val.getBoundaryFeature(featureName),
                                StackTraceUtil.writeStackTrace()
                        );
                        extraData.removeSegment(featureName ,val.getBoundaryFeature(featureName));
                    }
                    TrackEdgeLocation location = extraData.DEBUG_findLocationOf(val);
                    KasugaLib.STACKS.RAILWAY.debugStream.printf("F=|Propagator.propagate$E|%s|%s|%s|%s|%s\n",
                            graph.id,
                            location,
                            val.getBoundaryFeature(featureName),
                            segmentId,
                            StackTraceUtil.writeStackTrace()
                    );
                    val.setBoundaryFeature(featureName, segmentId);
                    return true;
                },
                false
        );
        extraData.addSegment(featureName, segmentId, segment);
        KasugaLib.STACKS.RAILWAY.debugStream.printf("S+|Propagator.propagate|%s|%s|%s|%s\n",
                graph.id,
                featureName,
                segmentId,
                StackTraceUtil.writeStackTrace()
        );
        KasugaLib.STACKS.RAILWAY.debugStream.printf("E|Propagator.propagate|%s|%s|%s|%s\n",
                graph.id,
                boundary.id,
                direction,
                StackTraceUtil.writeStackTrace()
        );
    }

    public static void notifyNewNode(TrackGraph graph, TrackNode node){
        KasugaLib.STACKS.RAILWAY.debugStream.printf("C|Propagator.notifyNewNode|%s|%s|%s\n",
                graph.id,
                node.getLocation().getLocation(),
                StackTraceUtil.writeStackTrace()
        );
        for (EdgePointType<? extends CustomBoundary> boundary : BoundarySegmentRegistry.getBoundaries()) {
            List<Couple<TrackNode>> frontier = new ArrayList();
            frontier.add(Couple.create(node, (TrackNode) null));
            ResourceLocation featureName = BoundarySegmentRegistry.getFeatureName(boundary);
            GraphExtraData extraData = KasugaLib.STACKS.RAILWAY.get().withGraph(graph);
            walk(graph, featureName, frontier, (EdgePointType<? super CustomBoundary>) boundary, (pair)->{
                TrackNode node1 = pair.getFirst();
                CustomBoundary customBoundaryInstance = (CustomBoundary) pair.getSecond();
                customBoundaryInstance.markDirty(customBoundaryInstance.isPrimary(node1));
                KasugaLib.STACKS.RAILWAY.debugStream.printf("BFD|Propagator.onRemoved|%s|%s|%s|%s\n",
                        graph.id,
                        node.getLocation().getLocation(),
                        customBoundaryInstance.id,
                        StackTraceUtil.writeStackTrace()
                );
                return false;
            }, (edge)->{
                if(!edge.hasCustomBoundaryInThisEdge(featureName)){
                    edge.setBoundaryFeature(featureName, EdgeExtraData.passiveBoundaryGroup);
                    TrackEdgeLocation location = extraData.DEBUG_findLocationOf(edge);
                    KasugaLib.STACKS.RAILWAY.debugStream.printf("F-|Propagator.notifyNewNode|%s|%s|%s|%s\n",
                            graph.id,
                            location,
                            edge.getBoundaryFeature(featureName),
                            StackTraceUtil.writeStackTrace()
                    );
                    return true;
                }
                return false;
            },false);
        }
        KasugaLib.STACKS.RAILWAY.debugStream.printf("E|Propagator.notifyNewNode|%s|%s|%s\n",
                graph.id,
                node.getLocation().getLocation(),
                StackTraceUtil.writeStackTrace()
        );
    }

    public static void onRemoved(TrackGraph graph, CustomBoundary customBoundary) {
        KasugaLib.STACKS.RAILWAY.debugStream.printf("C|Propagator.notifyNewNode|%s|%s|%s\n",
                graph.id,
                customBoundary.id,
                StackTraceUtil.writeStackTrace()
        );
        ResourceLocation featureName = BoundarySegmentRegistry.getFeatureName(customBoundary);
        GraphExtraData extraData = KasugaLib.STACKS.RAILWAY.get().withGraph(graph);
        for(boolean front : Iterate.trueAndFalse){
            if (customBoundary.shouldUpdate(front))
                continue;
            walk(
                    graph,
                    featureName,
                    customBoundary,
                    (EdgePointType<? super CustomBoundary>) customBoundary.getType(),
                    front,
                    boundaryPair->{
                        CustomBoundary boundary = (CustomBoundary) boundaryPair.getSecond();
                        TrackNode node = boundaryPair.getFirst();
                        KasugaLib.STACKS.RAILWAY.debugStream.printf("BFD|Propagator.onRemoved|%s|%s|%s|%s\n",
                                graph.id,
                                node.getLocation().getLocation(),
                                boundary.id,
                                StackTraceUtil.writeStackTrace()
                        );
                        boundary.markDirty(boundary.isPrimary(node));
                        return false;
                    },edgeData -> {
                        if(!edgeData.hasCustomBoundaryInThisEdge(featureName)){
                            extraData.removeSegment(featureName ,edgeData.getBoundaryFeature(featureName));
                            edgeData.setBoundaryFeature(featureName, EdgeExtraData.passiveBoundaryGroup);
                            TrackEdgeLocation location = extraData.DEBUG_findLocationOf(edgeData);
                            KasugaLib.STACKS.RAILWAY.debugStream.printf("F-|Propagator.onRemoved|%s|%s|%s|%s\n",
                                    graph.id,
                                    location,
                                    edgeData.getBoundaryFeature(featureName),
                                    StackTraceUtil.writeStackTrace()
                            );
                            return true;
                        }
                        return false;
                    },
                    false
            );
        }
        KasugaLib.STACKS.RAILWAY.debugStream.printf("E|Propagator.notifyNewNode|%s|%s|%s\n",
                graph.id,
                customBoundary.id,
                StackTraceUtil.writeStackTrace()
        );
    }

    public static void walk(
            TrackGraph graph,
            ResourceLocation featureName,
            CustomBoundary boundary,
            EdgePointType<? super CustomBoundary> boundaryEdgePointType,
            boolean front,
            Predicate<Pair<TrackNode, ? super CustomBoundary>> boundaryCallback,
            Predicate<EdgeExtraData> nonBoundaryCallback,
            boolean forCollection
    ) {

        Couple<TrackNodeLocation> edgeLocation = boundary.edgeLocation;
        Couple<TrackNode> startNodes = edgeLocation.map(graph::locateNode);
        Couple<TrackEdge> startEdges = startNodes.mapWithParams((l1, l2) -> graph.getConnectionsFrom(l1)
                .get(l2), startNodes.swap());

        TrackNode node1 = startNodes.get(front);
        TrackNode node2 = startNodes.get(!front);
        TrackEdge startEdge = startEdges.get(front);
        TrackEdge oppositeEdge = startEdges.get(!front);

        if (startEdge == null)
            return;

        if (!forCollection) {
            // Create.RAILWAYS.sync.edgeDataChanged(graph, node1, node2, startEdge, oppositeEdge);
            // @TODO: Notify ourselves's Extra Edge Data Changed
        }

        // Check for signal on the same edge

        CustomBoundary immediateBoundary = (CustomBoundary) startEdge.getEdgeData()
                .next(boundaryEdgePointType, boundary.getLocationOn(startEdge));
        if (immediateBoundary != null) {
            return;
        }

        // Search for any connected signals
        List<Couple<TrackNode>> frontier = new ArrayList<>();
        frontier.add(Couple.create(node2, node1));
        walk(
                graph,
                featureName,
                frontier,
                boundaryEdgePointType,
                boundaryCallback,
                nonBoundaryCallback,
                forCollection
        );
    }

    private static void walk(
            TrackGraph graph,
            ResourceLocation featureName,
            List<Couple<TrackNode>> frontier,
            EdgePointType<? super CustomBoundary> boundaryEdgePointType,
            Predicate<Pair<TrackNode, ? super CustomBoundary>> circuitCallback,
            Predicate<EdgeExtraData> nonBoundaryCallback,
            boolean forCollection
    ) {
        KasugaLib.STACKS.RAILWAY.debugStream.printf("VS|Propagator.walk|%s|%s\n",
                graph.id,
                StackTraceUtil.writeStackTrace()
        );

        // This method is copied and edited from Create mod https://github.com/Creators-of-Create/Create/blob/mc1.18/dev/src/main/java/com/simibubi/create/content/trains/signal/SignalPropagator.java#L91
        Set<TrackEdge> visited = new HashSet<>();

        GraphExtraData graphExtraData = KasugaLib.STACKS.RAILWAY.get().withGraph(graph);

        while (!frontier.isEmpty()) {
            Couple<TrackNode> couple = frontier.remove(0);
            TrackNode currentNode = couple.getFirst();
            TrackNode prevNode = couple.getSecond();

            EdgeWalk: for (Map.Entry<TrackNode, TrackEdge> entry : graph.getConnectionsFrom(currentNode)
                    .entrySet()) {
                TrackNode nextNode = entry.getKey();
                TrackEdge edge = entry.getValue();

                if (nextNode == prevNode)
                    continue;

                // already checked this edge
                if (!visited.add(edge))
                    continue;

                // chain signal: check if reachable
                if (forCollection && !graph.getConnectionsFrom(prevNode)
                        .get(currentNode)
                        .canTravelTo(edge))
                    continue;

                TrackEdge oppositeEdge = graph.getConnectionsFrom(nextNode)
                        .get(currentNode);
                visited.add(oppositeEdge);

                KasugaLib.STACKS.RAILWAY.debugStream.printf("V|Propagator.walk|%s|%s|%s\n",
                        graph.id,
                        TrackEdgeLocation.fromEdge(edge),
                        StackTraceUtil.writeStackTrace()
                );


                for (boolean flip : Iterate.falseAndTrue) {
                    TrackEdge currentEdge = flip ? oppositeEdge : edge;
                    EdgeData signalData = currentEdge.getEdgeData();
                    EdgeExtraData extraData = graphExtraData.getEdgeData(currentEdge);

                    // no boundary- update group of edge\


                    KasugaLib.STACKS.RAILWAY.debugStream.printf("VU|Propagator.walk|%s|%s|%s|%s\n",
                            graph.id,
                            TrackEdgeLocation.fromEdge(edge),
                            extraData.getCustomBoundariesListString(),
                            StackTraceUtil.writeStackTrace()
                    );

                    if (!extraData.hasCustomBoundaryInThisEdge(featureName)) {
                        if (nonBoundaryCallback.test(extraData)) {
                            // Create.RAILWAYS.sync.edgeDataChanged(graph, currentNode, nextNode, edge, oppositeEdge);
                            // @TODO: Notify ourselves sync module to notify edge data changed
                        }
                        continue;
                    }

                    // other/own boundary found
                    CustomBoundary nextBoundary = (CustomBoundary) signalData.next(boundaryEdgePointType, 0);
                    if (nextBoundary == null)
                        continue;
                    if (circuitCallback.test(Pair.of(currentNode, nextBoundary))) {
                        // currentEdge.getEdgeData()
                        //        .refreshIntersectingSignalGroups(graph);
                        // Create.RAILWAYS.sync.edgeDataChanged(graph, currentNode, nextNode, edge, oppositeEdge);
                        // @TODO: Notify ourselves sync module to notify edge data changed
                    }
                    continue EdgeWalk;
                }

                frontier.add(Couple.create(nextNode, currentNode));
            }
        }
        KasugaLib.STACKS.RAILWAY.debugStream.printf("VE|Propagator.walk|%s|%s\n",
                graph.id,
                StackTraceUtil.writeStackTrace()
        );
    }
}
