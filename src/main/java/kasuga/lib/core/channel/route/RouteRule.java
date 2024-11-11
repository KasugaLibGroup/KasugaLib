package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.address.LabelType;
import kasuga.lib.core.channel.peer.Channel;

import java.util.Optional;

public interface RouteRule {
    public Optional<RouteTarget> route(Channel channel);
}
