package kasuga.lib.core.create.device.locator;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import kasuga.lib.core.create.device.TrainDeviceType;

import java.util.function.BiFunction;

public class TrainDeviceLocatorType {
    private final BiFunction<MovementContext, TrainDeviceType, TrainDeviceLocator> movementCreateFunction;
    private final BiFunction<InteractiveContext, TrainDeviceType, TrainDeviceLocator> interactiveCreateFunction;

    public TrainDeviceLocatorType(
            BiFunction<MovementContext, TrainDeviceType, TrainDeviceLocator> createFunction,
            BiFunction<InteractiveContext, TrainDeviceType, TrainDeviceLocator> interactiveCreateFunction
    ) {
        this.movementCreateFunction = createFunction;
        this.interactiveCreateFunction = interactiveCreateFunction;
    }
    public TrainDeviceLocator create(MovementContext context, TrainDeviceType type) {
        return movementCreateFunction.apply(context, type);
    }

    public TrainDeviceLocator create(InteractiveContext interactiveContext, TrainDeviceType type) {
        return interactiveCreateFunction.apply(interactiveContext, type);
    }
}
