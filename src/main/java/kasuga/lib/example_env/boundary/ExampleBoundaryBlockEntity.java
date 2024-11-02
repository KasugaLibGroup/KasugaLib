package kasuga.lib.example_env.boundary;

import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ExampleBoundaryBlockEntity extends SmartBlockEntity {
    TrackTargetingBehaviour<ExampleBoundary> edgePoint;
    public ExampleBoundaryBlockEntity(BlockPos blockPos, BlockState state) {
        super(ExampleBoundaryModule.EXAMPLE_BOUNDARY_BLOCK_ENTITY.getType(), blockPos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {
        list.add(edgePoint = new TrackTargetingBehaviour<>(this, ExampleBoundaryModule.EXAMPLE_BOUNDARY));
    }
}
