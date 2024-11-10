package kasuga.lib.core.channel;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelPeer;
import kasuga.lib.core.channel.peer.ChannelReciever;

import java.util.HashMap;

public class NetworkSwitcher implements ChannelReciever {
    HashMap<Label, ChannelPeer> direct_peers = new HashMap<>();
    @Override
    public void $onConnect(Channel channel) {
        Label next = channel.destination().address();
        if(direct_peers.containsKey(next)){
            direct_peers.get(next).$onConnect(channel);
        }

        channel.close();
    }

    public void addPeer(ChannelPeer peer) {
        direct_peers.put(peer.getAddress(), peer);
    }
}
