package kasuga.lib.core.create.boundary;

import com.ibm.icu.impl.ICUResourceBundle;
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
    private static HashMap<ResourceLocation, Function<UUID, CustomTrackSegment>>
            CONSTRUCT_FUNCTION_BY_FEATRUE = new HashMap<>();
    public static HashMap<EdgePointType<? extends CustomBoundary>, ResourceLocation>
            FEATURE_LOCATION = new HashMap<>();

    public static HashMap<EdgePointType<? extends CustomBoundary>, Function<UUID, CustomTrackSegment>>
            CONSTRUCT_FUNCTION = new HashMap<>();

    public static HashSet<EdgePointType<? extends CustomBoundary>> BOUNDARY_TYPES = new HashSet<>();

    public static HashMap<ResourceLocation, HashSet<EdgePointType<?>>> UPDATE_LISTENERS = new HashMap<>();

    public static CustomTrackSegment createSegment(CustomBoundary boundary, UUID uuid){
        return CONSTRUCT_FUNCTION.get(boundary.getType()).apply(uuid);
    }

    public static void register(
            ResourceLocation featureLocation,
            EdgePointType<? extends CustomBoundary> boundary,
            Function<UUID, CustomTrackSegment> constructor
    ){
        FEATURE_LOCATION.put(boundary, featureLocation);
        CONSTRUCT_FUNCTION.put(boundary, constructor);
        CONSTRUCT_FUNCTION_BY_FEATRUE.put(featureLocation, constructor);
        BOUNDARY_TYPES.add(boundary);
    }

    public static void registerUpdateListener(
            ResourceLocation featureLocation,
            EdgePointType<? extends SegmentUpdateListener> listener
    ){
        UPDATE_LISTENERS.computeIfAbsent(featureLocation, (l)->new HashSet<>()).add(listener);
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

    public static CustomTrackSegment createSegmentByFeatureName(ResourceLocation featureName, UUID uuid){
        return CONSTRUCT_FUNCTION_BY_FEATRUE.get(featureName).apply(uuid);
    }
}