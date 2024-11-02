package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.graph.DimensionPalette;
import kasuga.lib.core.create.boundary.ResourcePattle;
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
        ResourcePattle resourcePattle = new ResourcePattle();
        compoundTag.put("Data", railwayData.write(dimensions,resourcePattle));
        dimensions.write(compoundTag);
        resourcePattle.write(compoundTag);
        return compoundTag;
    }

    public static KasugaRailwayDataManager load(CompoundTag compoundTag){
        KasugaRailwayDataManager railwayData = new KasugaRailwayDataManager();
        DimensionPalette dimensions = DimensionPalette.read(compoundTag);
        ResourcePattle resourcePattle = ResourcePattle.read(compoundTag);
        CompoundTag data = compoundTag.getCompound("Data");
        railwayData.railwayData.read(data, dimensions, resourcePattle);
        return railwayData;
    }

    public RailwayData getRailwayData() {
        return railwayData;
    }
}
