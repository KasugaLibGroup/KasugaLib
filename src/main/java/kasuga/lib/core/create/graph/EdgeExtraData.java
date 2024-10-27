package kasuga.lib.core.create.graph;

import kasuga.lib.core.create.boundary.ResourcePattle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EdgeExtraData {
    public static final UUID passiveBoundaryGroup = UUID.fromString("00000000-0000-0000-0000-000000000000");
    HashMap<ResourceLocation, UUID> customBoundaryGroups = new HashMap<>();
    public CompoundTag write(ResourcePattle resourcePattle) {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (Map.Entry<ResourceLocation, UUID> entry : customBoundaryGroups.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("Id", resourcePattle.encode(entry.getKey()));
            if(entry.getValue() == null){
                entryTag.putBoolean("NullValue", true);
            }else{
                entryTag.putUUID("Value", entry.getValue());
            }
        }
        tag.put("BoundaryGroups", listTag);
        return tag;
    }

    public void read(CompoundTag data, ResourcePattle resourcePattle) {
        ListTag tag = data.getList("BoundaryGroups", Tag.TAG_COMPOUND);
        for(int i=0;i<tag.size();i++){
            CompoundTag entryTag = tag.getCompound(i);
            UUID value;
            if(!entryTag.getBoolean("NullValue")){
                value = entryTag.getUUID("Value");
            } else value = null;
            customBoundaryGroups.put(
                    resourcePattle.decode(entryTag.getInt("Id")),
                    value
            );
        }
    }

    public boolean hasBoundaryFeature(ResourceLocation featureName) {
        return !customBoundaryGroups.containsKey(featureName) || customBoundaryGroups.get(featureName)!=null;
    }

    public boolean hasCustomBoundaryInThisEdge(ResourceLocation featureName){
        return customBoundaryGroups.containsKey(featureName) && customBoundaryGroups.get(featureName)==null;
    }

    public void setBoundaryFeature(ResourceLocation featureName, UUID segmentId) {
        if(segmentId == passiveBoundaryGroup){
            setBoundaryFeaturePassive(featureName);
            return;
        }
        customBoundaryGroups.put(featureName, segmentId);
    }

    public UUID getBoundaryFeature(ResourceLocation featureName) {
        return customBoundaryGroups.getOrDefault(featureName, passiveBoundaryGroup);
    }

    public void setBoundaryFeaturePassive(ResourceLocation featureName) {
        customBoundaryGroups.remove(featureName);
    }
}
