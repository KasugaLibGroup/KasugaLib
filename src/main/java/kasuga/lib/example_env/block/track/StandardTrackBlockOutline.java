package kasuga.lib.example_env.block.track;

import com.simibubi.create.content.trains.track.TrackBlockOutline;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StandardTrackBlockOutline extends TrackBlockOutline {

    public static final VoxelShape STANDARD_LONG_CROSS =
            Shapes.or(
                    StandardTrackVoxelShapes.longOrthogonalZ(),
                    StandardTrackVoxelShapes.longOrthogonalX());
    public static final VoxelShape STANDARD_LONG_ORTHO = StandardTrackVoxelShapes.longOrthogonalZ();
    public static final VoxelShape STANDARD_LONG_ORTHO_OFFSET =
            StandardTrackVoxelShapes.longOrthogonalZOffset();

    public static VoxelShape convert(Object o, boolean standard) {
        if (o instanceof VoxelShape shape) return convert(shape, standard);
        throw new IllegalArgumentException("object is not a VoxelShape");
    }
}
