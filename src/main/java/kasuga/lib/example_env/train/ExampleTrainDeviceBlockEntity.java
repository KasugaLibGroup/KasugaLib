package kasuga.lib.example_env.train;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExampleTrainDeviceBlockEntity extends BlockEntity {
    public ExampleTrainDeviceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ExampleTrainDeviceModule.EXAMPLE_DEVICE_BLOCK_ENTITY.getType(), blockPos, blockState);
    }
}
