package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelPeer;
import kasuga.lib.core.channel.peer.ChannelReciever;

import java.util.ArrayList;
import java.util.HashMap;

public class SimpleRouter extends RouteRuleManager implements ChannelReciever {
    HashMap<Label, ChannelReciever> direct_recievers = new HashMap<>();

    @Override
    public void $onConnect(Channel channel) {
        Label next = channel.destination().address();
        if(direct_recievers.containsKey(next)){
            direct_recievers.get(next).$onConnect(channel);
            return;
        }
        channel.close();
    }

    public void attach(ChannelPeer peer){
        direct_recievers.put(peer.getAddress(), peer);
        peer.setDistributor(this);
    }

    public void detach(ChannelPeer peer){
        direct_recievers.remove(peer.getAddress());
    }
}
