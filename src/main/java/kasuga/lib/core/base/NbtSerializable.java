package kasuga.lib.core.base;

import net.minecraft.nbt.CompoundTag;

public interface NbtSerializable {
    void write(CompoundTag nbt);
    void read(CompoundTag nbt);
}
