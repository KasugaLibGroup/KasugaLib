package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.foundation.utility.Couple;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.nbt.CompoundTag;

public class TrackEdgeLocation extends Pair<TrackNodeLocation,TrackNodeLocation> {
    protected TrackEdgeLocation(TrackNodeLocation first, TrackNodeLocation second) {
        super(first, second);
    }

    public static TrackEdgeLocation fromEdge(TrackEdge edge){
        return new TrackEdgeLocation(edge.node1.getLocation(), edge.node2.getLocation());
    }

    public Couple<TrackNodeLocation> toCouple(){
        return Couple.create(getFirst(), getSecond());
    }

    public CompoundTag write(DimensionPalette palette){
        CompoundTag tag = new CompoundTag();
        tag.put("First", getFirst().write(palette));
        tag.put("Second", getSecond().write(palette));
        return tag;
    }

    public static TrackEdgeLocation read(CompoundTag tag, DimensionPalette palette){
        TrackNodeLocation location1 = TrackNodeLocation.read(tag.getCompound("First"), palette);
        TrackNodeLocation location2 = TrackNodeLocation.read(tag.getCompound("Second"), palette);
        return new TrackEdgeLocation(location1, location2);
    }
}
