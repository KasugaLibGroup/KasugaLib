package kasuga.lib.core.create.boundary;

import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;

import java.util.Collection;
import java.util.HashSet;

public class EdgePointFilter {
    protected final HashSet<EdgePointType<?>> filter;
    public EdgePointFilter(HashSet<EdgePointType<?>> filter){
        this.filter = filter;
    }

    public static EdgePointFilter create(Collection<EdgePointType<?>> filter){
        return new EdgePointFilter(new HashSet<>(filter));
    }

    public TrackEdgePoint next(TrackEdge edge, double minPosition){
        EdgeData edgeData = edge.getEdgeData();
        TrackEdgePoint nextPoint = null;
        double currentPosition = minPosition;
        while(true){
            nextPoint = edgeData.next(currentPosition);
            if(nextPoint == null)
                return null;
            if(filter.contains(nextPoint.getType()))
                return nextPoint;
            currentPosition = nextPoint.getLocationOn(edge);
        }
    }
}
