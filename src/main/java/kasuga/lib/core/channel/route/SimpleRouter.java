package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelPeer;
import kasuga.lib.core.channel.peer.ChannelReciever;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SimpleRouter extends RouteRuleManager implements ChannelReciever {
    HashMap<Label, ChannelReciever> directPeers = new HashMap<>();
    private ChannelReciever defaultReciever;
    private final Set<Channel> activeChannels = new HashSet<>();

    @Override
    public void $onConnect(Channel channel) {
        activeChannels.add(channel);
        channel.addOnCloseListener((c) -> {
            activeChannels.remove(channel);
        });
        
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

        if(defaultReciever != null){
            defaultReciever.$onConnect(channel);
            return;
        }
        
        channel.close();
    }

    public void attach(ChannelPeer peer){
        directPeers.put(peer.getAddress(), peer);
        peer.setDistributor(this);
    }

    public void detach(ChannelPeer peer){
        directPeers.remove(peer.getAddress());
        peer.setDistributor(null);
    }

    public void setDefaultReciever(ChannelReciever reciever) {
        this.defaultReciever = reciever;
    }

    public void addPeer(ChannelPeer clientPeer) {
        directPeers.put(clientPeer.getAddress(), clientPeer);
        clientPeer.setDistributor(this);
    }

    public void removePeer(ChannelPeer clientPeer) {
        directPeers.remove(clientPeer.getAddress());
        clientPeer.setDistributor(null);
    }

    public void close() {
        for (Channel channel : activeChannels) {
            channel.close();
        }
        activeChannels.clear();

        for (ChannelReciever peer : directPeers.values()) {
            if (peer instanceof ChannelPeer) {
                ((ChannelPeer) peer).close();
            }
        }
        directPeers.clear();
        defaultReciever = null;
    }
}