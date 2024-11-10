package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelPeer;
import kasuga.lib.core.channel.peer.ChannelReciever;

import java.util.HashMap;
import java.util.Optional;

public class SimpleRouter extends RouteRuleManager implements ChannelReciever {
    HashMap<Label, ChannelReciever> directPeers = new HashMap<>();

    @Override
    public void $onConnect(Channel channel) {
        Label next = channel.destination().address();
        if(directPeers.containsKey(next)){
            directPeers.get(next).$onConnect(channel);
            return;
        }
        
        for (RouteRule rule : rules) {
            Optional<RouteTarget> target = rule.route(channel);
            if(target.isPresent()){
                target.get().distribute(channel, this);
                return;
            }
        }
        
        channel.close();
    }

    public void attach(ChannelPeer peer){
        directPeers.put(peer.getAddress(), peer);
        peer.setDistributor(this);
    }

    public void detach(ChannelPeer peer){
        directPeers.remove(peer.getAddress());
    }
}
