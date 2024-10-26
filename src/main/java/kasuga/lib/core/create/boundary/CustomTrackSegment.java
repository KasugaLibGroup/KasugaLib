package kasuga.lib.core.create.boundary;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class CustomTrackSegment {
    protected UUID segmentId;

    public CustomTrackSegment(UUID segmentId){
        this.segmentId = segmentId;
    }

    public CompoundTag write(){
        return new CompoundTag();
    }

    public void read(CompoundTag tag){

    }
}
