package kasuga.lib.core.create.device.locator;

public class TrainDeviceLocators {
    public static final TrainDeviceLocatorType CARRIAGE_BLOCKPOS =
            new TrainDeviceLocatorType(
                    (ctx,type)->new CarriageBlockPosDeviceLocator(ctx.localPos),
                    (ctx,type)->new CarriageBlockPosDeviceLocator(ctx.blockPos())
            );

    public static final TrainDeviceLocatorType CARRIAGE_TYPE =
            new TrainDeviceLocatorType(
                    (ctx,type)->new CarriageTypeDeviceLocator(type),
                    (ctx,type)->new CarriageTypeDeviceLocator(type)
            );

    public static final TrainDeviceLocatorType TRAIN_TYPE =
            new TrainDeviceLocatorType(
                    (ctx,type)->new TrainTypeDeviceLocator(type),
                    (ctx,type)->new TrainTypeDeviceLocator(type)
            );
}
