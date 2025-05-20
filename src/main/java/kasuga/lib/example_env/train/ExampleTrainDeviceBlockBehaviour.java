package kasuga.lib.example_env.train;

import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import kasuga.lib.core.create.device.TrainDeviceLocation;
import kasuga.lib.core.create.device.TrainDeviceManager;
import kasuga.lib.core.util.data_type.Pair;

public class ExampleTrainDeviceBlockBehaviour implements MovementBehaviour {
    @Override
    public void startMoving(MovementContext context) {
        if(context.world.isClientSide)
            return;
        Pair<TrainDeviceManager, TrainDeviceLocation> location = TrainDeviceManager.getManager(context);
        if(location == null || location.getSecond() == null)
            return;
        location.getFirst().getOrCreateSystem(ExampleTrainDeviceModule.EXAMPLE_TRAIN_DEVICE);
    }
}
