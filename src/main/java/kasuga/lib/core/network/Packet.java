package kasuga.lib.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * This class is designed for network packages. Network packages transmit our custom data between client and server.
 * For packages from client to server, use {@link C2SPacket}.
 * For packages from server to client, use {@link S2CPacket}
 */
public abstract class Packet {
    public Packet(){}
    public Packet(FriendlyByteBuf buf) {}
    abstract public boolean onReach(NetworkEvent.Context context);
    abstract public void encode(FriendlyByteBuf buf);
}
