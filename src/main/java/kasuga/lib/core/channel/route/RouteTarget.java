package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.peer.Channel;

public interface RouteTarget {
    public void distribute(Channel channel, RouteRuleManager manager);
}
