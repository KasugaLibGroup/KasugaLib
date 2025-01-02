package kasuga.lib.core.create.device.locator;

import kasuga.lib.core.create.device.TrainDeviceType;

public class CarriageTypeDeviceLocator extends TrainDeviceLocator {
    protected final TrainDeviceType type;

    public CarriageTypeDeviceLocator(TrainDeviceType type) {
        this.type = type;
    }

    public TrainDeviceType getType() {
        return type;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof CarriageTypeDeviceLocator)) return false;
        CarriageTypeDeviceLocator that = (CarriageTypeDeviceLocator) object;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
