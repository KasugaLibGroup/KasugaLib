package kasuga.lib.registrations.create;

import kasuga.lib.core.create.boundary.BoundarySegmentRegistry;
import kasuga.lib.core.create.boundary.CustomTrackSegment;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;

import java.util.UUID;
import java.util.function.Function;

public class TrackSegmentReg extends Reg {

    private TrackEdgePointReg boundaryEdgePointType;
    private Function<UUID, CustomTrackSegment> constructor;

    public TrackSegmentReg(String registrationKey) {
        super(registrationKey);
    }

    public TrackSegmentReg boundaryEdgePoint(TrackEdgePointReg boundaryEdgePoint){
        this.boundaryEdgePointType = boundaryEdgePoint;
        return this;
    }

    public TrackSegmentReg segmentType(Function<UUID, CustomTrackSegment> constructor){
        this.constructor = constructor;
        return this;
    }

    @Override
    public TrackSegmentReg submit(SimpleRegistry registry) {
        BoundarySegmentRegistry.register(
            registry.asResource(getIdentifier()),
            boundaryEdgePointType.getType(),
            constructor
        );
        return this;
    }

    @Override
    public String getIdentifier() {
        return "track_segment";
    }
}
