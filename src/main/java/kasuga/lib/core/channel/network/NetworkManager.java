package kasuga.lib.core.channel.network;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.NetworkSwitcher;
import kasuga.lib.core.channel.address.ConnectionInfo;
import kasuga.lib.core.channel.packets.C2SChannelConnectionPacket;
import kasuga.lib.core.channel.packets.C2SChannelMessagePacket;
import kasuga.lib.core.channel.packets.C2SChannelStateChangePacket;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelReciever;
import kasuga.lib.core.channel.peer.ChannelStatus;
import kasuga.lib.core.channel.route.SimpleRouter;
import net.minecraft.nbt.CompoundTag;

import java.util.HashSet;
import java.util.Set;

public class NetworkManager implements ChannelReciever {
    NetworkDuplexer reciever = new NetworkDuplexer();
    NetworkDuplexer sender = new NetworkDuplexer();
    ChannelReciever _interface;

    public NetworkManager(SimpleRouter reciever) {
        this._interface = reciever;
        registerReciever(reciever);
    }

    protected void registerReciever(SimpleRouter reciever){
        reciever.setDefaultReciever(this);
    }

    protected void unregisterReciever(SimpleRouter reciever){
        reciever.setDefaultReciever(null);
    }

    @Override
    public void $onConnect(Channel channel) {
        long networkId = sender.createConnection(channel);
        sendNetworkConnecitonPacket(channel, networkId);
    }

    public void onConnection(ConnectionInfo sourceInfo, ConnectionInfo destInfo, long networkId) {
        if(!transform(sourceInfo, destInfo)){
            sendStatePacket(networkId, ChannelStatus.CLOSED , false);
            return;
        }
        NetworkChannelSocket socket = new NetworkChannelSocket( this, reciever, networkId);
        Channel channel = new Channel(sourceInfo, destInfo, socket);
        if(!reciever.createConnection(channel, networkId)){
            sendStatePacket(networkId, ChannelStatus.CLOSED , false);
            return;
        }
        _interface.$onConnect(channel);
    }

    protected boolean transform(ConnectionInfo source, ConnectionInfo dest){
        return true;
    }

    public void onStateUpdate(long networkId, ChannelStatus state, boolean isConnectionSender) {
        NetworkDuplexer duplexer = isConnectionSender ? reciever : sender;
        switch (state){
            case ESTABLISHED:
                NetworkChannelSocket socket = new NetworkChannelSocket(this, duplexer, networkId);
                socket.internalEstablish();
                duplexer.establishConnection(networkId, socket);
                break;
            case CLOSED:
                duplexer.closeConnection(networkId);
                break;
        }
    }


    public void onMessage(long networkId, CompoundTag message, boolean isConnectionSender) {
        NetworkDuplexer duplexer = isConnectionSender ? reciever : sender;
        duplexer.sendMessageAsRemote(networkId, message);
    }


    public void sendMessage(NetworkChannelSocket channel, CompoundTag message) {
        boolean isOwn = channel.isDuplexedBy(sender);
        long networkId = channel.getNetworkId();
        sendMessagePacket(networkId, message, isOwn);
    }

    public void sendEstablished(NetworkChannelSocket socket) {
        long networkId = socket.getNetworkId();
        boolean isOwn = socket.isDuplexedBy(sender);
        sendStatePacket(networkId, ChannelStatus.ESTABLISHED, isOwn);
        socket.getDuplexer().onExistedConnectionEstablished(networkId, socket);
    }

    public void sendClose(NetworkChannelSocket socket) {
        long networkId = socket.getNetworkId();
        boolean isOwn = socket.isDuplexedBy(sender);
        sendStatePacket(networkId, ChannelStatus.CLOSED, isOwn);
    }

    protected void sendNetworkConnecitonPacket(Channel channel, long networkId) {
        if(!KasugaLib.STACKS.CHANNEL.clientIsEnabled()){
            channel.close();
            return;
        }
        KasugaLib.STACKS.CHANNEL.packet.channelReg.sendToServer(new C2SChannelConnectionPacket(
                channel.source(),
                channel.destination(),
                networkId)
        );
    }

    protected void sendMessagePacket(long networkId, CompoundTag message, boolean isOwn){
        if(!KasugaLib.STACKS.CHANNEL.clientIsEnabled())
            return;
        KasugaLib.STACKS.CHANNEL.packet.channelReg.sendToServer(
                new C2SChannelMessagePacket(networkId, message, isOwn)
        );
    }

    protected void sendStatePacket(long networkId, ChannelStatus state, boolean isOwn){
        if(!KasugaLib.STACKS.CHANNEL.clientIsEnabled())
            return;
        KasugaLib.STACKS.CHANNEL.packet.channelReg.sendToServer(
                new C2SChannelStateChangePacket(networkId, state, isOwn)
        );
    }

    public void close(){
        reciever.close();
        sender.close();
    }
}
