package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.HashMap;
import java.util.Map;

public class TrainExtraData {
    private final Train train;

    TrainExtraData(Train train){
        this.train = train;
    }


    public CompoundTag write() {
        return new CompoundTag();
    }
}