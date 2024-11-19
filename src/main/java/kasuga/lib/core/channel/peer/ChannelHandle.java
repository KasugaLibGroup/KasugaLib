package kasuga.lib.core.channel.peer;

import net.minecraft.nbt.CompoundTag;

public interface ChannelHandle {
    void setHandler(ChannelHandler newHandler);
    void sendMessage(CompoundTag message);
    void close();
}
