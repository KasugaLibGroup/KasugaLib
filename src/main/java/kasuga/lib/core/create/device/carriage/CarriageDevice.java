package kasuga.lib.core.create.device.carriage;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import kasuga.lib.core.create.device.locator.CarriageDeviceLocator;

import java.util.UUID;

public class CarriageDevice {
    protected final CarriageDeviceManager manager;
    protected final Carriage carriage;
    protected final CarriageDeviceLocator locator;

    public CarriageDevice(
            CarriageDeviceManager manager,
            Carriage carriage,
            CarriageDeviceLocator locator
    ) {
        this.manager = manager;
        this.carriage = carriage;
        this.locator = locator;
    }
}
