package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.entity.Train;
import kasuga.lib.core.KasugaLibClient;
import kasuga.lib.core.base.Saved;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class RailwayManager {

    public static RailwayManager createServer(){
        return new RailwayManager(new Saved("kasuga_railway_data", ()->new KasugaRailwayDataManager(), KasugaRailwayDataManager::load));
    }


    public RailwayManager(Saved<KasugaRailwayDataManager> dataSaved) {
        this.dataSaved = dataSaved;
    }

    public RailwayManager(RailwayData data) {
        this.data = data;
    }

    public static RailwayManager createClient() {
        return new RailwayManager(new RailwayData(null));
    }


    public RailwayManager sided(boolean isClient){
        if(isClient){
            return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, ()->()-> KasugaLibClient.RAILWAY);
        }else{
            return this;
        }
    }

    public RailwayManager sided(LevelAccessor accessor){
        return sided(accessor.isClientSide());
    }

    protected Saved<KasugaRailwayDataManager> dataSaved;
    protected Map<Train, TrainDistanceIntegrator> integrators = Collections.synchronizedMap(new WeakHashMap<>());


    public RailwayData data;

    public void load(ServerLevel level){
        if(data == null && dataSaved != null){
            data = dataSaved.loadFromDisk(level).getRailwayData();
        }
    }

    public void save(ServerLevel level){
        if (dataSaved != null) {
            dataSaved.saveToDisk(level);
        }
    }

    public RailwayData get() {
        return data;
    }

    public TrainDistanceIntegrator getIntergartor(Train train){
        return integrators.computeIfAbsent(train, (i)->new TrainDistanceIntegrator());
    }
}
