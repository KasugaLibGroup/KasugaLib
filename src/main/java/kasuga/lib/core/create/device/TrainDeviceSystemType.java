package kasuga.lib.core.create.device;

import java.util.function.Function;

public class TrainDeviceSystemType<T extends TrainDeviceSystem> {

    public TrainDeviceSystemType(Function<TrainDeviceManager, T> factory) {
        this.factory = factory;
    }

    public static <T extends TrainDeviceSystem> TrainDeviceSystemType<T> of(
            Function<TrainDeviceManager, T> factory
    ) {
        return new TrainDeviceSystemType<>(factory);
    }

    protected final Function<TrainDeviceManager, T> factory;
    public T create(TrainDeviceManager trainDeviceManager) {
        return factory.apply(trainDeviceManager);
    }
}
