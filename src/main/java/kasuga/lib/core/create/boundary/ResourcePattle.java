package kasuga.lib.core.create.boundary;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

// @TODO: Add LICENSE, Inspired by Create
public class ResourcePattle {
    List<ResourceLocation> resourceLocations = new ArrayList<>();

    public ResourcePattle(){

    }

    public int encode(ResourceLocation location){
        int index = resourceLocations.indexOf(location);
        if(index == -1){
            index = resourceLocations.size();
            resourceLocations.add(location);
        }
        return index;
    }

    public ResourceLocation decode(int index){
        if(index >= this.resourceLocations.size() || index < 0)
            throw new IllegalArgumentException("Illegal ResourceLocation Encoding Value.");
        return this.resourceLocations.get(index);
    }

    public void write(CompoundTag tag){
        ListTag listTag = new ListTag();
        for (ResourceLocation resourceLocation : resourceLocations) {
            listTag.add(StringTag.valueOf(resourceLocation.toString()));
        }
        tag.put("ResourcePattle", listTag);
    }

    public static ResourcePattle read(CompoundTag tag){
        ResourcePattle pattle = new ResourcePattle();
        ListTag listTag = tag.getList("ResourcePattle", Tag.TAG_STRING);
        for(int i=0;i<listTag.size();i++){
            pattle.addResourceString(listTag.getString(i));
        }
        return pattle;
    }

    private void addResourceString(String string) {
        this.resourceLocations.add(new ResourceLocation(string));
    }
}
