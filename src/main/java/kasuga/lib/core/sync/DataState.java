package kasuga.lib.core.sync;

import net.minecraft.nbt.CompoundTag;

public abstract class DataState {
    public int id;
    public abstract DataState copy();

    public abstract CompoundTag serialize();

    public abstract CompoundTag diff(DataState otherState);

    public abstract void applyDiff(CompoundTag diffTag);
}
