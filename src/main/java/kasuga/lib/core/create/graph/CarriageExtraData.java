package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.entity.Train;
import kasuga.lib.core.create.device.manager.TrainDeviceManager;

public class CarriageExtraData {
    protected TrainDeviceManager deviceManager;

    public CarriageExtraData(Train train, TrainExtraData parent, int index) {
        deviceManager = new TrainDeviceManager(parent.getDeviceManager());
    }

    public TrainDeviceManager getDeviceManager() {
        return deviceManager;
    }
}
