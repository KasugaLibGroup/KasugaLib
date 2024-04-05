package kasuga.lib.core.network;

import kasuga.lib.core.annos.Inner;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * This packet should be sent by logical server and received by logical client.
 * It is used for your custom packets that transmit custom data from server to client.
 * To register one of this, see {@link kasuga.lib.registrations.common.ChannelReg}
 */
public abstract class S2CPacket extends Packet {

    /**
     * The decoder constructor of your packet. Take all your data out from the byte buffer here.
     * @param buf the received byte buffer.
     */
    public S2CPacket(FriendlyByteBuf buf) {super(buf);}

    public S2CPacket() {super();}
    @Override
    @Inner
    public boolean onReach(NetworkEvent.Context context) {
        context.enqueueWork(() -> handle(Minecraft.getInstance()));
        return true;
    }

    /**
     * The handler of your packet, the packet would be handled here.
     * @param minecraft Your minecraft client.
     */
    public abstract void handle(Minecraft minecraft);

    /**
     * Push your data into the byte buffer here.
     * @param buf the data container buffer, push your data into it.
     */
    public abstract void encode(FriendlyByteBuf buf);
}
