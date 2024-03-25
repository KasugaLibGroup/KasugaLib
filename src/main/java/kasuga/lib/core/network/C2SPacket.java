package kasuga.lib.core.network;

import kasuga.lib.core.annos.Inner;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

/**
 * This packet should be sent by logical client and received by logical server.
 * It is used for your custom packets that transmit custom data from client to server.
 * To register one of this, see {@link kasuga.lib.registrations.common.ChannelReg}
 */
public abstract class C2SPacket extends Packet {

    /**
     * This function is the deserializer of your packet.
     * See {@link Packet} for more constructor info.
     * @param buf data bytes you got from the network.
     */
    public C2SPacket(FriendlyByteBuf buf) {super(buf);}

    public C2SPacket() {super();}

    @Inner
    public boolean onReach(NetworkEvent.Context context) {
        context.enqueueWork(() -> handle(context));
        return true;
    }

    /**
     * The handler of your packet. After this packet has been received by
     * logical server, we would handle this packet in this method.
     * @param context some server info, such level, player and so on.
     */
    public abstract void handle(NetworkEvent.Context context);

    /**
     * The encoder of your packet. You must put all your data into this
     * byte buffer in order to transmit them.
     * @param buf the data container buffer, push your data into it.
     */
    abstract public void encode(FriendlyByteBuf buf);
}
