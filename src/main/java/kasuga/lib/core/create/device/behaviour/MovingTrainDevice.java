package kasuga.lib.core.create.device.behaviour;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.create.device.TrainDevice;
import kasuga.lib.core.create.device.TrainDeviceType;
import kasuga.lib.core.create.device.locator.InteractiveContext;
import kasuga.lib.core.create.graph.CarriageExtraData;
import net.minecraft.core.BlockPos;

public class MovingTrainDevice {
    public static void register(MovementContext context, TrainDeviceType type){
        if(context.world.isClientSide())
            return;

        if(!(context.contraption.entity instanceof CarriageContraptionEntity carriageContraptionEntity)){
            return;
        }
        Carriage carriage = carriageContraptionEntity.getCarriage();

        CarriageExtraData extraData =
                KasugaLib.STACKS.RAILWAY.get()
                        .withTrainExtraData(carriage.train)
                        .getCarriage(carriage.train.carriages.indexOf(carriage));

        TrainDevice device = type.create(context);

        type.getLocatorType().create(context,type).register(
                device,
                extraData.getDeviceManager()
        );
    }

    public static TrainDevice get(MovementContext context, TrainDeviceType type){
        if(context.world.isClientSide()){} // @TODO: Client sided device(needs sync)
        if(!(context.contraption.entity instanceof CarriageContraptionEntity carriageContraptionEntity)){
            return null;
        }

        Carriage carriage = carriageContraptionEntity.getCarriage();

        CarriageExtraData extraData =
                KasugaLib.STACKS.RAILWAY.get()
                        .withTrainExtraData(carriage.train)
                        .getCarriage(carriage.train.carriages.indexOf(carriage));

        return type
                .getLocatorType()
                .create(context, type)
                .getDevice(extraData.getDeviceManager());
    }

    public static TrainDevice get(AbstractContraptionEntity contraptionEntity, BlockPos blockPos, TrainDeviceType type){
        if(contraptionEntity instanceof CarriageContraptionEntity carriageContraptionEntity){
            if(!(carriageContraptionEntity.getContraption() instanceof CarriageContraption carriageContraption))
                return null;

            Carriage carriage = carriageContraptionEntity.getCarriage();

            CarriageExtraData extraData =
                    KasugaLib.STACKS.RAILWAY.get()
                            .withTrainExtraData(carriage.train)
                            .getCarriage(carriage.train.carriages.indexOf(carriage));

            return type
                    .getLocatorType()
                    .create(new InteractiveContext(carriage, carriageContraption, blockPos), type)
                    .getDevice(extraData.getDeviceManager());
        }
        return null;
    }
}
