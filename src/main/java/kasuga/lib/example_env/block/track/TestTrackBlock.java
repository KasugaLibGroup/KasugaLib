package kasuga.lib.example_env.block.track;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ISpecialBlockItemRequirement;
import com.simibubi.create.content.trains.track.*;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import kasuga.lib.example_env.AllExampleBogey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TestTrackBlock extends TrackBlock
        implements IBE<TrackBlockEntity>,
                IWrenchable,
                ITrackBlock,
                ISpecialBlockItemRequirement,
                ProperWaterloggedBlock {

    public TestTrackBlock(Properties properties, TrackMaterial material) {
        super(properties, material);
    }

    @Override
    public BlockState getBogeyAnchor(BlockGetter world, BlockPos pos, BlockState state) {
        // return state;

        return AllExampleBogey.standardBogey.getEntry()
                .getDefaultState()
                .setValue(
                        BlockStateProperties.HORIZONTAL_AXIS,
                        state.getValue(SHAPE) == TrackShape.XO
                                ? Direction.Axis.X
                                : Direction.Axis.Z);
        // return super.getBogeyAnchor(world, pos, state);
    }
}
