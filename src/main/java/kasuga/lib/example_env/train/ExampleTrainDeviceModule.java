package kasuga.lib.example_env.train;

import com.simibubi.create.AllMovementBehaviours;
import kasuga.lib.core.create.device.TrainDeviceRegistry;
import kasuga.lib.core.create.device.TrainDeviceSystemType;
import kasuga.lib.example_env.ExampleMain;
import kasuga.lib.registrations.common.BlockEntityReg;
import kasuga.lib.registrations.common.BlockReg;
import kasuga.lib.registrations.create.MovementReg;
import net.minecraft.world.level.material.MapColor;

public class ExampleTrainDeviceModule {
    public static final TrainDeviceSystemType<ExampleTrainDevice> EXAMPLE_TRAIN_DEVICE = new TrainDeviceSystemType<>(ExampleTrainDevice::new);

    public static final BlockReg<ExampleTrainDeviceBlock> EXAMPLE_DEVICE_BLOCK =
            new BlockReg<ExampleTrainDeviceBlock>("example_train_device")
            .blockType(ExampleTrainDeviceBlock::new)
            .defaultBlockItem()
            .materialColor(MapColor.COLOR_BLACK)
            .submit(ExampleMain.testRegistry);

    public static final BlockEntityReg<ExampleTrainDeviceBlockEntity> EXAMPLE_DEVICE_BLOCK_ENTITY =
            new BlockEntityReg<ExampleTrainDeviceBlockEntity>("example_train_device")
            .blockEntityType(ExampleTrainDeviceBlockEntity::new)
            .addBlock(EXAMPLE_DEVICE_BLOCK)
            .submit(ExampleMain.testRegistry);

    public static final MovementReg<ExampleTrainDeviceBlockBehaviour> EXAMPLE_DEVICE_MOVEMENT =
            new MovementReg<ExampleTrainDeviceBlockBehaviour>("example_train_device")
            .behaviour(new ExampleTrainDeviceBlockBehaviour())
            .sortByBlocks(EXAMPLE_DEVICE_BLOCK)
            .submit(ExampleMain.testRegistry);

    public static void invoke(){
        TrainDeviceRegistry.register(ExampleMain.testRegistry.asResource("example_train_device"), EXAMPLE_TRAIN_DEVICE);
    }
}
