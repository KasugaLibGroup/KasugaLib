package kasuga.lib.core.create.device.carriage;

import com.simibubi.create.content.trains.entity.Carriage;
import kasuga.lib.core.create.device.locator.CarriageDeviceLocator;

public interface CarriageDeviceType<T> {
    public T create(
            CarriageDeviceManager manager,
            Carriage carriage,
            CarriageDeviceLocator locator
    );
}
