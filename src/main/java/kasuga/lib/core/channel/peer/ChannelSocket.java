package kasuga.lib.core.channel.peer;

import net.minecraft.nbt.CompoundTag;

public interface ChannelSocket {
    public void onMessage(CompoundTag message);
    public void onEstablished();
    public void onClose();

    void setChannel(Channel channel);
}
