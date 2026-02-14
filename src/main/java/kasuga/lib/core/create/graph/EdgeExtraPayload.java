package kasuga.lib.core.create.graph;

import net.minecraft.nbt.CompoundTag;

public interface EdgeExtraPayload {
    CompoundTag write();

    EdgeExtraPayloadType<?> getType();
}
