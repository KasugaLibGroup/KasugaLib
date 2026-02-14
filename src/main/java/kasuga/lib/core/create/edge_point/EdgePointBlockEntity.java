package kasuga.lib.core.create.edge_point;

import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class EdgePointBlockEntity<T extends TrackEdgePoint> extends SmartBlockEntity {

    private TrackTargetingBehaviour<T> edgePoint;

    public EdgePointBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {
        list.add(edgePoint = new TrackTargetingBehaviour<T>(this, getEdgePointType()));
    }

    protected abstract EdgePointType<T> getEdgePointType();
}
