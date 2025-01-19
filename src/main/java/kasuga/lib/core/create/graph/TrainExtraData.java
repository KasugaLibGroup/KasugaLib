package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.entity.Train;
import kasuga.lib.core.channel.route.SimpleRouter;
import kasuga.lib.core.create.device.manager.TrainDeviceManager;
import net.minecraft.nbt.CompoundTag;

import java.util.LinkedList;

public class TrainExtraData {
    private final Train train;
    private LinkedList<CarriageExtraData> carriages = new LinkedList<>();

    protected TrainDeviceManager deviceManager = new TrainDeviceManager();

    public SimpleRouter router = new SimpleRouter();

    TrainExtraData(Train train){
        this.train = train;
        for (int i = 0; i < train.carriages.size(); i++) {
            this.carriages.add(new CarriageExtraData(train, this, i));
        }
    }

    public CompoundTag write() {
        return new CompoundTag();
    }

    public TrainDeviceManager getDeviceManager() {
        return deviceManager;
    }

    public CarriageExtraData getCarriage(int index) {
        return carriages.get(index);
    }
}