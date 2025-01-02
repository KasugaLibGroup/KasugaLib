package kasuga.lib.core.create.device.locator;

import kasuga.lib.core.create.device.TrainDevice;
import kasuga.lib.core.create.device.TrainDeviceType;
import kasuga.lib.core.create.device.manager.TrainDeviceManager;

public class TrainTypeDeviceLocator extends TrainDeviceLocator {
    protected final TrainDeviceType type;

    public TrainTypeDeviceLocator(TrainDeviceType type) {
        this.type = type;
    }

    public TrainDeviceType getType() {
        return type;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof TrainTypeDeviceLocator)) return false;
        TrainTypeDeviceLocator that = (TrainTypeDeviceLocator) object;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public void register(TrainDevice device, TrainDeviceManager deviceManager) {
        while(deviceManager.getParent() != null) {
            deviceManager = deviceManager.getParent();
        }
        deviceManager.setDevice(this, device);
    }

    @Override
    public TrainDevice getDevice(TrainDeviceManager deviceManager) {
        while(deviceManager.getParent() != null) {
            deviceManager = deviceManager.getParent();
        }
        return deviceManager.getDevice(this);
    }
}
