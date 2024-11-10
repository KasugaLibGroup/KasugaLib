package kasuga.lib.core.channel.network;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.NetworkSwitcher;
import kasuga.lib.core.channel.address.ConnectionInfo;
import kasuga.lib.core.channel.network.address.MinecraftClientPlayerAddress;
import kasuga.lib.core.channel.network.address.MinecraftServerAddress;
import kasuga.lib.core.channel.packets.S2CChannelConnectionPacket;
import kasuga.lib.core.channel.packets.S2CChannelMessagePacket;
import kasuga.lib.core.channel.packets.S2CChannelStateChangePacket;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelReciever;
import kasuga.lib.core.channel.peer.ChannelStatus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class NetworkServerManager extends NetworkManager {
    private final ServerPlayer player;

    public NetworkServerManager(NetworkSwitcher reciever, ServerPlayer player){
        super(reciever);
        this.player = player;
    }

    @Override
    protected void registerReciever(NetworkSwitcher reciever) {
        // @TODO
    }

    @Override
    protected boolean transform(ConnectionInfo source, ConnectionInfo dest) {
        if(!(dest.lastAddress() instanceof MinecraftServerAddress)){
            return false;
        }
        dest.popAddress();
        source.pushAddress(new MinecraftClientPlayerAddress(player));
        return true;
    }

    @Override
    protected void sendNetworkConnecitonPacket(Channel channel, long networkId) {
        KasugaLib.STACKS.CHANNEL.packet.channelReg.sendToClient(
                new S2CChannelConnectionPacket(
                        channel.source(),
                        channel.destination(),
                        networkId
                ),
                player
        );
    }

    @Override
    protected void sendMessagePacket(long networkId, CompoundTag message, boolean isOwn) {
        KasugaLib.STACKS.CHANNEL.packet.channelReg.sendToClient(
                new S2CChannelMessagePacket(
                        networkId,
                        message,
                        isOwn
                ),
                player
        );
    }

    @Override
    protected void sendStatePacket(long networkId, ChannelStatus state, boolean isOwn) {
        KasugaLib.STACKS.CHANNEL.packet.channelReg.sendToClient(
                new S2CChannelStateChangePacket(
                        networkId,
                        state,
                        isOwn
                ),
                player
        );
    }
}
