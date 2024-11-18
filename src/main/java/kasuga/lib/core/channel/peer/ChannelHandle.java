package kasuga.lib.core.channel.peer;

import net.minecraft.nbt.CompoundTag;

public interface ChannelHandle {

    void sendMessage(CompoundTag message);

    void close();
}
