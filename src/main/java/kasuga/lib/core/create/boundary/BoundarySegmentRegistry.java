package kasuga.lib.core.create.boundary;

import com.simibubi.create.content.trains.graph.EdgePointType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class BoundarySegmentRegistry
{
    public static HashMap<EdgePointType<? extends CustomBoundary>, ResourceLocation>
            FEATURE_LOCATION = new HashMap<>();

    public static HashMap<EdgePointType<? extends CustomBoundary>, Function<UUID, CustomTrackSegment>>
            CONSTRUCT_FUNCTION = new HashMap<>();

    public static HashSet<EdgePointType<? extends CustomBoundary>> BOUNDARY_TYPES = new HashSet<>();

    public static CustomTrackSegment createSegment(CustomBoundary boundary, UUID uuid){
        return CONSTRUCT_FUNCTION.get(boundary.getType()).apply(uuid);
    }

    public static void register(ResourceLocation featureLocation,EdgePointType<? extends CustomBoundary> boundary, Function<UUID, CustomTrackSegment> constructor){
        FEATURE_LOCATION.put(boundary, featureLocation);
        CONSTRUCT_FUNCTION.put(boundary, constructor);
        BOUNDARY_TYPES.add(boundary);
    }

    public static ResourceLocation getFeatureName(CustomBoundary boundary) {
        return FEATURE_LOCATION.get(boundary.getType());
    }

    public static ResourceLocation getFeatureName(EdgePointType<? extends CustomBoundary> boundaryType) {
        return FEATURE_LOCATION.get(boundaryType);
    }

    public static List<EdgePointType<? extends CustomBoundary>> getBoundaries(){
        return BOUNDARY_TYPES.stream().toList();
    }
}
