package kasuga.lib.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public abstract class Packet {
    public Packet(){}
    public Packet(FriendlyByteBuf buf) {}
    abstract public boolean onReach(NetworkEvent.Context context);
    abstract public void encode(FriendlyByteBuf buf);
}
