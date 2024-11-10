package kasuga.lib.core.channel.packets;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.address.ConnectionInfo;
import kasuga.lib.core.network.C2SPacket;
import kasuga.lib.core.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class C2SChannelConnectionPacket extends C2SPacket {
    ConnectionInfo sourceInfo;
    ConnectionInfo destInfo;
    long networkId;

    public C2SChannelConnectionPacket(ConnectionInfo sourceInfo, ConnectionInfo destInfo, long networkId) {
        this.sourceInfo = sourceInfo;
        this.destInfo = destInfo;
        this.networkId = networkId;
    }

    public C2SChannelConnectionPacket(FriendlyByteBuf byteBuf) {
        this.sourceInfo = ConnectionInfo.read(byteBuf);
        this.destInfo = ConnectionInfo.read(byteBuf);
        this.networkId = byteBuf.readLong();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        KasugaLib.STACKS.CHANNEL.server(context.getSender()).onConnection(sourceInfo, destInfo, networkId);
    }


    @Override
    public void encode(FriendlyByteBuf buf) {
        sourceInfo.write(buf);
        destInfo.write(buf);
        buf.writeLong(networkId);
    }

    public ConnectionInfo source(){
        return sourceInfo;
    }

    public ConnectionInfo destination(){
        return destInfo;
    }
}