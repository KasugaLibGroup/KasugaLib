package kasuga.lib.core.channel.peer;

import com.mojang.datafixers.optics.profunctors.FunctorProfunctor;
import kasuga.lib.core.channel.NetworkSwitcher;
import kasuga.lib.core.channel.address.ChannelPort;
import kasuga.lib.core.channel.address.ConnectionInfo;
import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.address.UUIDChannelPort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChannelPeer {
    Label address;
    private ChannelReciever distributor;

    private List<Channel> managedChannels = new ArrayList<>();
    public ChannelPeer(Label address){
        this.address = address;
    }

    public ChannelPeerSocketClient createSocket(ConnectionInfo remote){
        ChannelPeerSocketClient client = new ChannelPeerSocketClient();
        UUIDChannelPort port = new UUIDChannelPort(UUID.randomUUID());
        Channel channel = new Channel(ConnectionInfo.of(address, port),remote, client);
        channel.addOnCloseListener((c)->managedChannels.remove(c));
        this.distributor.$onConnect(channel);
        return client;
    }

    public ChannelPeerSocketClient createSocket(ConnectionInfo remote, ChannelHandler handler){
        ChannelPeerSocketClient client = new ChannelPeerSocketClient();
        client.setHandler(handler);
        UUIDChannelPort port = new UUIDChannelPort(UUID.randomUUID());
        Channel channel = new Channel(ConnectionInfo.of(address, port),remote, client);
        channel.addOnCloseListener((c)->managedChannels.remove(c));
        this.distributor.$onConnect(channel);
        return client;
    }

    public void $onConnect(Channel channel){
        ChannelPeerSocketServer server = new ChannelPeerSocketServer(channel);
        if(this.onConnect(server)){
            channel.establish(server);
            channel.addOnCloseListener((c)->managedChannels.remove(c));
        }else{
            channel.close();
        }
    }

    protected boolean onConnect(ChannelPeerSocketServer server) {
        return false;
    }

    public void close(){
        for (Channel managedChannel : this.managedChannels) {
            managedChannel.close();
        }
    }

    public Label getAddress() {
        return address;
    }

    public void setDistributor(ChannelReciever distributor) {
        this.distributor = distributor;
    }
}
