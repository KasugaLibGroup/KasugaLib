package kasuga.lib.core.channel.network;

import net.minecraft.network.FriendlyByteBuf;

public interface NetworkSerializable {
    public void write(FriendlyByteBuf byteBuf);
    public NetworkSeriaizableType<?> getType();
}
