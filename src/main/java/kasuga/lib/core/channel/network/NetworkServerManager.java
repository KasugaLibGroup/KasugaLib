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
import kasuga.lib.core.channel.route.ForwardRouteTarget;
import kasuga.lib.core.channel.route.PlayerLabelMatchRule;
import kasuga.lib.core.channel.route.SimpleRouter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class NetworkServerManager extends NetworkManager {
    private final ServerPlayer player;

    public NetworkServerManager(SimpleRouter reciever, ServerPlayer player){
        super(reciever);
        this.player = player;
    }

    @Override
    protected void registerReciever(SimpleRouter reciever) {
        reciever.addRule(PlayerLabelMatchRule.create(player, ForwardRouteTarget.create(this)));
    }

    @Override
    protected void unregisterReciever(SimpleRouter reciever) {
        reciever.addRule(PlayerLabelMatchRule.create(player, ForwardRouteTarget.create(this)));
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

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void $onConnect(Channel channel) {
        ConnectionInfo source = channel.source();
        ConnectionInfo dest = channel.destination();
        
        if (source.lastAddress() instanceof MinecraftClientPlayerAddress) {
            source.popAddress();
        }
        
        dest.pushAddress(MinecraftServerAddress.INSTANCE.get());
        
        Channel transformedChannel = channel.proxy(source, dest);
        
        super.$onConnect(transformedChannel);
    }
}
