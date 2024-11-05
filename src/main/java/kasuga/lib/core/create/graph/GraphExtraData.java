package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackNode;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.create.boundary.BoundarySegmentRegistry;
import kasuga.lib.core.create.boundary.CustomTrackSegment;
import kasuga.lib.core.create.boundary.ResourcePattle;
import kasuga.lib.core.util.StackTraceUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;

import java.util.*;

public class GraphExtraData {
    final UUID graphId;
    HashMap<TrackEdgeLocation, EdgeExtraData> edgeExtraData = new HashMap<>();
    HashMap<ResourceLocation, HashMap<UUID, CustomTrackSegment>> segmentInstances = new HashMap<>();

    public GraphExtraData(UUID graphId) {
        this.graphId = graphId;
    }

    public void transfer(
            LevelAccessor level,
            TrackNode node,
            Map<TrackNode, TrackEdge> connections,
            GraphExtraData targetExtraData
    ) {
        for (TrackEdge edge : connections.values()) {
            EdgeExtraData data = removeEdge(edge);
            targetExtraData.addEdge(edge, data);
        }
    }

    public TrackEdgeLocation DEBUG_findLocationOf(EdgeExtraData data){
        for (Map.Entry<TrackEdgeLocation, EdgeExtraData> entry : edgeExtraData.entrySet()) {
            if(entry.getValue() == data){
                return entry.getKey();
            }
        }
        return null;
    }

    public void transferAll(GraphExtraData toOtherExtra) {
        edgeExtraData.forEach(toOtherExtra::addEdge);
        edgeExtraData.clear();
    }

    public void createEdge(TrackEdge edge) {
        edgeExtraData.put(TrackEdgeLocation.fromEdge(edge), new EdgeExtraData());
    }

    public void addEdge(TrackEdgeLocation edge, EdgeExtraData data) {
        edgeExtraData.put(edge, data);
    }

    public void addEdge(TrackEdge edge, EdgeExtraData data) {
        edgeExtraData.put(TrackEdgeLocation.fromEdge(edge), data);
    }

    public EdgeExtraData removeEdge(TrackEdge edge){
        return edgeExtraData.remove(TrackEdgeLocation.fromEdge(edge));
    }

    public void syncWithExternal(Collection<TrackEdge> edges){
        HashSet<TrackEdgeLocation> keySet = new HashSet<>(edgeExtraData.keySet());
        HashSet<TrackEdgeLocation> shouldAdd = new HashSet<>();
        for (TrackEdge edge : edges) {
            TrackEdgeLocation location = TrackEdgeLocation.fromEdge(edge);
            if(keySet.contains(location)){
                keySet.remove(location);
            }else{
                shouldAdd.add(location);
            }
        }
        shouldAdd.forEach((l)->{
            edgeExtraData.put(l, new EdgeExtraData());
        });
        keySet.forEach((l)->{
            edgeExtraData.remove(l);
        });
    }

    public EdgeExtraData getEdgeData(TrackEdge edge){
        return edgeExtraData.get(TrackEdgeLocation.fromEdge(edge));
    }

    public CompoundTag write(DimensionPalette dimensions, ResourcePattle resourcePattle) {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (Map.Entry<TrackEdgeLocation, EdgeExtraData> entry : edgeExtraData.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.put("Location", entry.getKey().write(dimensions));
            entryTag.put("Data", entry.getValue().write(resourcePattle));
            listTag.add(entryTag);
        }
        tag.put("EdgeExtraDatas", listTag);
        ListTag segmentListTag = new ListTag();
        for (Map.Entry<ResourceLocation, HashMap<UUID, CustomTrackSegment>> entry : segmentInstances.entrySet()) {
            for (Map.Entry<UUID, CustomTrackSegment> segmentEntry : entry.getValue().entrySet()) {
                CompoundTag segmentTag = new CompoundTag();
                segmentTag.putUUID("FeatureId", segmentEntry.getKey());
                segmentTag.putString("FeatureName", entry.getKey().toString());
                segmentTag.put("Data", segmentEntry.getValue().write());
                segmentListTag.add(segmentTag);
            }
        }
        tag.put("Segments", segmentListTag);
        return tag;
    }

    public void read(CompoundTag data, DimensionPalette dimensions, ResourcePattle resourcePattle) {
        ListTag listTag = data.getList("EdgeExtraDatas", ListTag.TAG_COMPOUND);
        for(int i=0;i<listTag.size();i++){
            CompoundTag entryTag = listTag.getCompound(i);
            TrackEdgeLocation edgeLocation = TrackEdgeLocation.read(entryTag.getCompound("Location"), dimensions);
            EdgeExtraData extraData = edgeExtraData.computeIfAbsent(edgeLocation, (x)->new EdgeExtraData());
        }
        ListTag segmentListTag = data.getList("Segments", ListTag.TAG_COMPOUND);
        for(int i=0;i<segmentListTag.size();i++){
            CompoundTag segmentTag = segmentListTag.getCompound(i);
            ResourceLocation featureName = new ResourceLocation(segmentTag.getString("FeatureName"));
            UUID featureId = segmentTag.getUUID("FeatureId");
            CustomTrackSegment segment = BoundarySegmentRegistry.createSegmentByFeatureName(featureName, featureId);
            segment.read(segmentTag.getCompound("Data"));
            addSegment(featureName, featureId, segment);
        }
    }


    public void addSegment(ResourceLocation featureName, UUID featureId, CustomTrackSegment segment){
        segmentInstances.computeIfAbsent(featureName, (i)->new HashMap<>()).put(featureId, segment);
    }

    public void removeSegment(ResourceLocation featureName, UUID featureId) {
        if(!segmentInstances.containsKey(featureName))
            return;
        segmentInstances.get(featureName).remove(featureId);
    }

    public CustomTrackSegment getSegment(ResourceLocation featureName, UUID featureId) {
        if(!segmentInstances.containsKey(featureName))
            return null;
        return segmentInstances.get(featureName).get(featureId);
    }

    public boolean hasSegment(ResourceLocation featureName, UUID featureId){
        if(!segmentInstances.containsKey(featureName))
            return false;
        return segmentInstances.get(featureName).containsKey(featureId);
    }

    public Set<TrackEdgeLocation> getEdges() {
        return edgeExtraData.keySet();
    }

    public EdgeExtraData getEdgeData(TrackEdgeLocation entry) {
        return edgeExtraData.get(entry);
    }
}
