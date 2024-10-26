package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.graph.DimensionPalette;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class KasugaRailwayDataManager extends SavedData {
    RailwayData railwayData;

    KasugaRailwayDataManager(){
        railwayData = new RailwayData(this);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        DimensionPalette dimensions = new DimensionPalette();
        compoundTag.put("Data", railwayData.write(dimensions));
        dimensions.write(compoundTag);
        return compoundTag;
    }

    public static KasugaRailwayDataManager load(CompoundTag compoundTag){
        KasugaRailwayDataManager railwayData = new KasugaRailwayDataManager();
        DimensionPalette dimensions = DimensionPalette.read(compoundTag);
        railwayData.railwayData.read(compoundTag, dimensions);
        return railwayData;
    }

    public RailwayData getRailwayData() {
        return railwayData;
    }
}
