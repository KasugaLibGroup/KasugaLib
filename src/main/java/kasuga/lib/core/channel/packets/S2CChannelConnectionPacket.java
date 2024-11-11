package kasuga.lib.core.channel.packets;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.address.ConnectionInfo;
import kasuga.lib.core.network.C2SPacket;
import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class S2CChannelConnectionPacket extends S2CPacket {
    ConnectionInfo sourceInfo;
    ConnectionInfo destInfo;
    long networkId;

    public S2CChannelConnectionPacket(ConnectionInfo sourceInfo, ConnectionInfo destInfo, long networkId) {
        this.sourceInfo = sourceInfo;
        this.destInfo = destInfo;
        this.networkId = networkId;
    }

    public S2CChannelConnectionPacket(FriendlyByteBuf byteBuf) {
        this.sourceInfo = ConnectionInfo.read(byteBuf);
        this.destInfo = ConnectionInfo.read(byteBuf);
        this.networkId = byteBuf.readLong();
    }

    @Override
    public void handle(Minecraft minecraft) {
        KasugaLib.STACKS.CHANNEL.client().onConnection(sourceInfo, destInfo, networkId);
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
