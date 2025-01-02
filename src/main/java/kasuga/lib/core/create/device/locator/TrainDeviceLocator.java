package kasuga.lib.core.create.device.locator;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.create.device.TrainDevice;
import kasuga.lib.core.create.device.manager.TrainDeviceManager;

import java.util.List;

public abstract class TrainDeviceLocator {
    public void register(TrainDevice device, TrainDeviceManager deviceManager) {
        deviceManager.setDevice(this, device);
    }

    public TrainDevice getDevice(TrainDeviceManager deviceManager) {
        return deviceManager.getDevice(this);
    }

}
