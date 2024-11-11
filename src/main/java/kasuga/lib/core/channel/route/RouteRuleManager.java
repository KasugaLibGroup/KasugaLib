package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.peer.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RouteRuleManager {
    List<RouteRule> rules = new ArrayList<>();
    public void sendPacket(Channel channel) {
        for (RouteRule rule : rules) {
            Optional<RouteTarget> target = rule.route(channel);
            if(target.isPresent()){
                target.get().distribute(channel, this);
                return;
            }
        }
    }

    public void addRule(RouteRule rule) {
        rules.add(rule);
    }

    public void removeRule(RouteRule rule) {
        rules.remove(rule);
    }
}
