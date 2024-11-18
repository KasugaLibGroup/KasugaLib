package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelReciever;

import java.util.function.Function;

public class RouteTargets {
    public static final DropRouteTarget DROP = DropRouteTarget.instance;

    public ForwardRouteTarget to(ChannelReciever receiver) {
        return new ForwardRouteTarget(receiver);
    }

    public ForwardAndTransformAddressTarget to(ChannelReciever receiver, Function<Label, Label> transferFunction) {
        return new ForwardAndTransformAddressTarget(receiver, transferFunction);
    }
}
