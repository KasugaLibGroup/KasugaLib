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
    HashMap<ResourceLocation, EdgeExtraPayload> payload = new HashMap<>();
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
            listTag.add(entryTag);
        }
        tag.put("BoundaryGroups", listTag);
        ListTag payloadTag = new ListTag();
        for (Map.Entry<ResourceLocation, EdgeExtraPayload> entry : payload.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("Id", resourcePattle.encode(entry.getKey()));
            entryTag.put("Data", entry.getValue().write());
            payloadTag.add(entryTag);
        }
        tag.put("Payload", payloadTag);
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

        ListTag payloadTag = data.getList("Payload", Tag.TAG_COMPOUND);
        for(int i=0;i<payloadTag.size();i++){
            CompoundTag entryTag = payloadTag.getCompound(i);
            EdgeExtraPayloadType<?> type = EdgeExtraPayloadRegistry.get(resourcePattle.decode(entryTag.getInt("Id")));
            if(type == null)
                continue;
            EdgeExtraPayload payload = (EdgeExtraPayload) type.read(entryTag.getCompound("Data"));
            this.payload.put(resourcePattle.decode(entryTag.getInt("Id")), payload);
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

    public void setPayload(EdgeExtraPayload payload) {
        this.payload.put(EdgeExtraPayloadRegistry.getId(payload.getType()), payload);
    }

    public EdgeExtraPayload getPayload(ResourceLocation featureName) {
        return payload.computeIfAbsent(featureName, (x)->{
            EdgeExtraPayloadType<?> type = EdgeExtraPayloadRegistry.get(featureName);
            if(type == null)
                return null;
            return (EdgeExtraPayload) type.create();
        });
    }
}
