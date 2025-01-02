package kasuga.lib.core.create.device.manager;

import kasuga.lib.core.create.device.TrainDevice;
import kasuga.lib.core.create.device.locator.TrainDeviceLocator;

import java.util.HashMap;

public class TrainDeviceManager {

    protected TrainDeviceManager parent;

    public TrainDeviceManager() {}

    public TrainDeviceManager(TrainDeviceManager parent) {
        this.parent = parent;
    }

    public TrainDeviceManager getParent() {
        return parent;
    }

    protected HashMap<TrainDeviceLocator, TrainDevice> devices = new HashMap<>();


    public boolean hasDevice(TrainDeviceLocator trainDeviceLocator){
        return devices.containsKey(trainDeviceLocator);
    }

    public void setDevice(TrainDeviceLocator trainDeviceLocator, TrainDevice trainDevice){
        devices.put(trainDeviceLocator, trainDevice);
    }

    public TrainDevice getDevice(TrainDeviceLocator trainDeviceLocator) {
        return devices.get(trainDeviceLocator);
    }
}
