package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.address.ConnectionInfo;
import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelReciever;

import java.util.function.Function;
import java.util.Stack;

public class ForwardAndTransformAddressTarget extends ForwardRouteTarget {
    private final Function<Label, Label> transferFunction;

    public ForwardAndTransformAddressTarget(ChannelReciever reciever, Function<Label, Label> transferFunction) {
        super(reciever);
        this.transferFunction = transferFunction;
    }

    @Override
    public void distribute(Channel channel, RouteRuleManager manager) {
        Stack<Label> destLabels = new Stack<>();
        destLabels.addAll(channel.destination().getLabels());

        Label topLabel = destLabels.pop();
        Label transformedLabel = transferFunction.apply(topLabel);

        Stack<Label> sourceLabels = new Stack<>();
        sourceLabels.addAll(channel.source().getLabels());
        sourceLabels.push(transformedLabel);

        ConnectionInfo newSource = new ConnectionInfo(
            sourceLabels,
            channel.source().getPort()
        );
        
        ConnectionInfo newDest = new ConnectionInfo(
            destLabels,
            channel.destination().getPort()
        );
        
        super.distribute(channel.proxy(newSource, newDest), manager);
    }
}
