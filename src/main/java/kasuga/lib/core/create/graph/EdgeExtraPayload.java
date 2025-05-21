package kasuga.lib.core.create.graph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public interface EdgeExtraPayload {
    CompoundTag write();

    EdgeExtraPayloadType<?> getType();
}
