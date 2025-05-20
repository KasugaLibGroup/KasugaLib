package kasuga.lib.example_env.train;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ExampleTrainDeviceBlock extends Block implements IBE<ExampleTrainDeviceBlockEntity> {
    public ExampleTrainDeviceBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<ExampleTrainDeviceBlockEntity> getBlockEntityClass() {
        return ExampleTrainDeviceBlockEntity.class;
    }

    @Override
    public BlockEntityType<ExampleTrainDeviceBlockEntity> getBlockEntityType() {
        return ExampleTrainDeviceModule.EXAMPLE_DEVICE_BLOCK_ENTITY.getType();
    }
}
