package kasuga.lib.core.channel;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.network.NetworkManager;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelPeer;
import kasuga.lib.core.channel.peer.ChannelReciever;
import kasuga.lib.core.channel.route.SimpleRouter;

import java.util.HashMap;

public class NetworkSwitcher implements ChannelReciever {
    public ChannelReciever defaultReciever;
    HashMap<Label, ChannelPeer> direct_peers = new HashMap<>();
    @Override
    public void $onConnect(Channel channel) {
        Label next = channel.destination().address();
        if(direct_peers.containsKey(next)){
            direct_peers.get(next).$onConnect(channel);
            return;
        }

        if(defaultReciever != null){
            defaultReciever.$onConnect(channel);
            return;
        }
        channel.close();
    }

    public void addPeer(ChannelPeer peer) {
        direct_peers.put(peer.getAddress(), peer);
        peer.setDistributor(this);
    }

    public void removePeer(ChannelPeer peer) {
        direct_peers.remove(peer.getAddress());
        peer.setDistributor(null);
    }

    public void setDefaultReciever(ChannelReciever reciever) {
        this.defaultReciever = reciever;
    }
}
