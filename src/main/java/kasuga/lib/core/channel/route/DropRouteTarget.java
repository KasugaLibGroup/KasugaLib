package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.peer.Channel;

public class DropRouteTarget implements RouteTarget {
    public static DropRouteTarget instance = new DropRouteTarget();
    
    @Override
    public void distribute(Channel channel, RouteRuleManager manager) {
        channel.close();
        return;
    }
}
