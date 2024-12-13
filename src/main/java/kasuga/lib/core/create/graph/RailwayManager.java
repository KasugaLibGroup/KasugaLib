package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.entity.Train;
import kasuga.lib.core.base.Saved;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class RailwayManager {
    public RailwayManager sided(boolean isClient){
        if(isClient){
            return null;
        }else{
            return this;
        }
    }

    public RailwayManager sided(LevelAccessor accessor){
        return sided(accessor.isClientSide());
    }

    Saved<KasugaRailwayDataManager> dataSaved = new Saved("kasuga_railway_data", ()->new KasugaRailwayDataManager(), KasugaRailwayDataManager::load);
    RailwayData data;

    Map<Train, TrainDistanceIntegrator> integrators = Collections.synchronizedMap(new WeakHashMap<>());

    public void load(ServerLevel level){
        if(data == null){
            data = dataSaved.loadFromDisk(level).getRailwayData();
        }
    }

    public void save(ServerLevel level){
        dataSaved.saveToDisk(level);
    }

    public RailwayData get() {
        return data;
    }

    public TrainDistanceIntegrator getIntergartor(Train train){
        return integrators.computeIfAbsent(train, (i)->new TrainDistanceIntegrator());
    }
}
