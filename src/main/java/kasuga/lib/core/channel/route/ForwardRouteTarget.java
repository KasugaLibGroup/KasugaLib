package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelReciever;

public class ForwardRouteTarget implements RouteTarget {
    private final ChannelReciever reciever;

    public ForwardRouteTarget(ChannelReciever reciever) {
        this.reciever = reciever;
    }

    @Override
    public void distribute(Channel channel, RouteRuleManager manager) {
        reciever.$onConnect(channel);
    }

    public static ForwardRouteTarget create(ChannelReciever reciever) {
        return new ForwardRouteTarget(reciever);
    }
}
