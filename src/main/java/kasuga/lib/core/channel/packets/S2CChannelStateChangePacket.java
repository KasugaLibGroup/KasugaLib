package kasuga.lib.core.channel.packets;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.peer.ChannelStatus;
import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class S2CChannelStateChangePacket extends S2CPacket {
    private final ChannelStatus state;
    long networkId;
    boolean isConnectionSender;

    public S2CChannelStateChangePacket(long networkId, ChannelStatus state, boolean isConnectionSender) {
        this.networkId = networkId;
        this.state = state;
        this.isConnectionSender = isConnectionSender;
    }

    public S2CChannelStateChangePacket(FriendlyByteBuf buf) {
        this.networkId = buf.readLong();
        this.state = ChannelStatus.fromInt(buf.readInt());
    }

    @Override
    public void handle(Minecraft minecraft) {
        KasugaLib.STACKS.CHANNEL.client().onStateUpdate(networkId, state, isConnectionSender);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(networkId);
        buf.writeInt(state.toInt());
    }
}
