package kasuga.lib.core.create.boundary;

import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class CustomBoundary extends SingleBlockEntityEdgePoint {
    Map<Boolean, UUID> sidedSegements = new HashMap<>();
    Map<Boolean, Boolean> dirty = new HashMap<>();

    protected CustomBoundary(){
        dirty.put(false,true);
        dirty.put(true,true);
    }

    @Override
    public void onRemoved(TrackGraph graph) {
        super.onRemoved(graph);
        CustomTrackSegmentPropagator.onRemoved(graph, this);
    }

    public void setSegment(boolean direction, UUID segmentId) {
        sidedSegements.put(direction, segmentId);
    }

    public void setSegmentAndUpdate(boolean direction, UUID segmentId){
        sidedSegements.put(direction, segmentId);
        dirty.put(direction, false);
    }

    public void markDirty(boolean direction) {
        dirty.put(direction, true);
    }

    public UUID getGroupId(boolean direction) {
        return sidedSegements.get(direction);
    }

    @Override
    public void tick(TrackGraph graph, boolean preTrains) {
        for (boolean i : Iterate.trueAndFalse){
            if(shouldUpdate(i)){
                dirty.put(i, false);
                CustomTrackSegmentPropagator.propagate(graph,this,i);
            }
        }
    }

    public boolean shouldUpdate(boolean direction) {
        return dirty.get(direction);
    }

    @Override
    public void write(CompoundTag nbt, DimensionPalette dimensions) {
        super.write(nbt, dimensions);
        for (boolean i : Iterate.trueAndFalse){
            if(shouldUpdate(i)) {
               nbt.putBoolean("ShouldUpdate" + (i ? "Front" : "Back"), true);
            }
            nbt.putUUID("SignalGroup"+ (i ? "Front" : "Back"), sidedSegements.get(i));
        }
    }

    @Override
    public void read(CompoundTag nbt, boolean migration, DimensionPalette dimensions) {
        super.read(nbt, migration, dimensions);
        for (boolean i : Iterate.trueAndFalse){
            this.dirty.put(i,nbt.getBoolean("ShouldUpdate" + (i ? "Front" : "Back")));
            this.sidedSegements.put(i, nbt.getUUID("SignalGroup"+ (i ? "Front" : "Back")));
        }
    }

    public void clearDirty() {
        this.dirty.put(false, false);
        this.dirty.put(true, false);
    }
}
