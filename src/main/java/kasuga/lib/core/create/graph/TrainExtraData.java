package kasuga.lib.core.create.graph;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import kasuga.lib.core.create.device.carriage.CarriageDeviceManager;

import java.util.HashMap;

public class TrainExtraData {
    private final Train train;
    HashMap<Integer, CarriageDeviceManager> carriageDevices = new HashMap<>();

    TrainExtraData(Train train){
        this.train = train;
    }

    public void getCarriageDeviceManager(Carriage carriage){
        getCarriageDeviceManager(train.carriages.indexOf(carriage));
    }

    public void getCarriageDeviceManager(int index){
        carriageDevices.computeIfAbsent(index, (i)->new CarriageDeviceManager());
    }
}