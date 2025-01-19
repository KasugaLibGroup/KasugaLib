package kasuga.lib.core.create.device;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import kasuga.lib.core.create.device.locator.TrainDeviceLocatorType;

import java.util.function.Supplier;

public class TrainDeviceType {
    private TrainDeviceLocatorType locator;

    public static TrainDeviceType create(
            Supplier<TrainDevice> deviceSupplier,
            TrainDeviceLocatorType trainDeviceLocatorType
    ){
        TrainDeviceType type = new TrainDeviceType();
        type.deviceSupplier = deviceSupplier;
        type.locator = trainDeviceLocatorType;
        return type;
    }
    protected Supplier<TrainDevice> deviceSupplier;
    public TrainDevice create(MovementContext context){
        return deviceSupplier.get();
    }

    public TrainDeviceLocatorType getLocatorType(){
        return locator;
    }
}
