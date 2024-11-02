package kasuga.lib.example_env.boundary;

import kasuga.lib.core.create.boundary.CustomTrackSegment;

import java.util.UUID;

public class ExampleSegment extends CustomTrackSegment {

    public ExampleSegment(UUID uuid) {
        super(uuid);
    }

    public UUID getId(){
        return segmentId;
    }
}
