package kasuga.lib.core.create.graph;

import kasuga.lib.core.base.Saved;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

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
}
