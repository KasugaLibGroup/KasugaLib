package kasuga.lib.core.channel.packets;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.peer.ChannelStatus;
import kasuga.lib.core.network.C2SPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class C2SChannelStateChangePacket extends C2SPacket {
    private final ChannelStatus state;
    long networkId;
    boolean isConnectionSender;

    public C2SChannelStateChangePacket(long networkId, ChannelStatus state, boolean isConnectionSender) {
        this.networkId = networkId;
        this.state = state;
        this.isConnectionSender = isConnectionSender;
    }

    public C2SChannelStateChangePacket(FriendlyByteBuf buf) {
        this.networkId = buf.readLong();
        this.state = ChannelStatus.fromInt(buf.readInt());
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        KasugaLib.STACKS.CHANNEL.server(context.getSender()).onStateUpdate(networkId, state, isConnectionSender);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(networkId);
        buf.writeInt(state.toInt());
    }
}
