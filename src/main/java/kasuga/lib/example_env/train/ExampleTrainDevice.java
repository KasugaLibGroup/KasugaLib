package kasuga.lib.example_env.train;

import kasuga.lib.core.create.device.TrainDeviceManager;
import kasuga.lib.core.create.device.TrainDeviceSystem;

public class ExampleTrainDevice extends TrainDeviceSystem {
    public ExampleTrainDevice(TrainDeviceManager manager) {
        super(manager);
    }

    @Override
    public void notifySpeed(double speed) {
        // Handle speed notification
        System.out.println("Speed: " + speed);

    }

    @Override
    public boolean cancelSlowdown() {
        return true;
    }
}
