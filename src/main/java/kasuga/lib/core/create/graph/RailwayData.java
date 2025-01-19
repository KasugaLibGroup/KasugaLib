package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import kasuga.lib.core.create.boundary.ResourcePattle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;

public class RailwayData {

    private final KasugaRailwayDataManager manager;

    RailwayData(KasugaRailwayDataManager manager){
        this.manager = manager;
    }

    public HashMap<UUID, GraphExtraData> extraDatas = new HashMap<>();

    public HashMap<UUID, TrainExtraData> trainExtraDatas = new HashMap<>();

    public void createExtraData(UUID graphId){
        this.extraDatas.computeIfAbsent(graphId, (id)->new GraphExtraData());
    }

    public void removeExtraData(UUID graphId){
        this.extraDatas.remove(graphId);
    }


    public GraphExtraData withGraph(TrackGraph target) {
        if(this.extraDatas.containsKey(target.id)){
            return this.extraDatas.get(target.id);
        }else{
            createExtraData(target.id);
            return this.extraDatas.get(target.id);
        }
    }


    public void syncExtraData(Set<UUID> uuids) {
        Set<UUID> keySets = new HashSet<>(extraDatas.keySet());
        HashSet<UUID> shouldAddSets = new HashSet<>();
        for (UUID uuid : uuids) {
            if(keySets.contains(uuid)){
                keySets.remove(uuid);
            }else{
                shouldAddSets.add(uuid);
            }
        }
        shouldAddSets.forEach(this::createExtraData);
        keySets.forEach(this::removeExtraData);
        markDirty();
    }

    public CompoundTag write(DimensionPalette dimensions, ResourcePattle resourcePattle) {
        CompoundTag tag = new CompoundTag();
        ListTag extraDataTags = new ListTag();
        for (Map.Entry<UUID, GraphExtraData> entry : this.extraDatas.entrySet()) {
            CompoundTag graphExtraDataTag = new CompoundTag();
            graphExtraDataTag.putUUID("Id", entry.getKey());
            graphExtraDataTag.put("Data", entry.getValue().write(dimensions, resourcePattle));
            extraDataTags.add(graphExtraDataTag);
        }
        tag.put("ExtraDatas", extraDataTags);
        return tag;
    }

    public void read(CompoundTag compoundTag, DimensionPalette dimensions, ResourcePattle resourcePattle) {
        markDirty();
        trainExtraDatas.clear();
        extraDatas.clear();
        ListTag extraDataTags = compoundTag.getList("ExtraDatas", Tag.TAG_COMPOUND);
        for(int i=0;i<extraDataTags.size();i++){
            CompoundTag tag = extraDataTags.getCompound(i);
            UUID id = tag.getUUID("Id");
            GraphExtraData extraData = extraDatas.computeIfAbsent(id, (x)->new GraphExtraData());
            extraData.read(tag.getCompound("Data"), dimensions, resourcePattle);
        }
    }

    public void markDirty(){
        if(manager != null){
            manager.setDirty();
        }
    }

    public void putTrainExtraData(Train train) {
        if(trainExtraDatas.containsKey(train.id)){
            return;
        }
        trainExtraDatas.put(train.id, new TrainExtraData(train));
    }

    public void removeTrainExtraData(UUID id) {
        trainExtraDatas.remove(id);
    }

    public TrainExtraData withTrainExtraData(Train train) {
        if(trainExtraDatas.containsKey(train.id)){
            return trainExtraDatas.get(train.id);
        }
        putTrainExtraData(train);
        return trainExtraDatas.get(train.id);
    }

    public TrainExtraData withTrainExtraData(UUID id) {
        if(trainExtraDatas.containsKey(id)){
            return trainExtraDatas.get(id);
        }
        return null;
    }
}
