package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.address.LabelType;
import kasuga.lib.core.channel.peer.Channel;

import java.util.Optional;

public class TargetLabelMatchRule implements RouteRule{
    RouteTarget target;
    LabelType type;
    @Override
    public Optional<RouteTarget> route(Channel channel) {
        if(channel.destination().address().getType() == type)
            return Optional.of(target);
        return Optional.empty();
    }

    public TargetLabelMatchRule(RouteTarget target, LabelType type) {
        this.target = target;
        this.type = type;
    }

    public static TargetLabelMatchRule create(LabelType type, RouteTarget target) {
        return new TargetLabelMatchRule(target, type);
    }
}
