package kasuga.lib.example_env.boundary;

import com.simibubi.create.content.trains.entity.Train;
import kasuga.lib.core.create.boundary.CustomBoundary;
import kasuga.lib.core.create.edge_point.BogeyObserverEdgePoint;

public class ExampleBoundary extends CustomBoundary implements BogeyObserverEdgePoint {
    protected int bogeyCount = 0;
    ExampleBoundary(){
        super();
    }

    @Override
    public void notifyBogey(Train train) {
        bogeyCount++;
    }

    public int getBogeyCount(){
        return bogeyCount;
    }

    public void resetBogeyCount(){
        bogeyCount = 0;
    }
}
