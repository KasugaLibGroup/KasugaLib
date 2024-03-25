package kasuga.lib.core.network;

import kasuga.lib.core.annos.Inner;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * This class is designed for network packages. Network packages transmit our custom data between client and server.
 * For packages from client to server, use {@link C2SPacket}.
 * For packages from server to client, use {@link S2CPacket}
 */
public abstract class Packet {

    /**
     * This constructor is also used as decoder. While the program get data from network, it would
     * use this deserializer to create our packet.
     * @param buf the bytes we got from network.
     */
    public Packet(FriendlyByteBuf buf) {}

    public Packet() {}

    @Inner
    abstract public boolean onReach(NetworkEvent.Context context);

    /**
     * The encoder of your packet, you must push all your data into this byte buffer.
     * @param buf the data container buffer, push your data into it.
     */
    abstract public void encode(FriendlyByteBuf buf);
}
