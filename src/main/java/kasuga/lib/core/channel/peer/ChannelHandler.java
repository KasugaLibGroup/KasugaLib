package kasuga.lib.core.channel.peer;

import net.minecraft.nbt.CompoundTag;

public interface ChannelHandler {
    default void onChannelEstabilished(ChannelHandle channel){};
    default void onChannelMessage(ChannelHandle channel, CompoundTag payload){}
    default void onChannelClose(ChannelHandle channel){}
}
