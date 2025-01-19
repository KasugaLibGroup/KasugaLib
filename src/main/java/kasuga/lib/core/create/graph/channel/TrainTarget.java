package kasuga.lib.core.create.graph.channel;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.address.ConnectionInfo;
import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.route.RouteRuleManager;
import kasuga.lib.core.channel.route.RouteTarget;
import kasuga.lib.core.create.graph.TrainExtraData;

import java.util.Stack;

public class TrainTarget implements RouteTarget {
    private boolean isClient;

    public TrainTarget(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    public void distribute(Channel channel, RouteRuleManager manager) {
        Stack<Label> destLabels = new Stack<>();
        destLabels.addAll(channel.destination().getLabels());

        Label topLabel = destLabels.pop();

        if(!(topLabel instanceof TrainLabel trainLabel)){
            channel.close();
            return;
        }

        Stack<Label> sourceLabels = new Stack<>();
        sourceLabels.addAll(channel.source().getLabels());
        sourceLabels.push(topLabel);

        ConnectionInfo newSource = new ConnectionInfo(
                sourceLabels,
                channel.source().getPort()
        );

        ConnectionInfo newDest = new ConnectionInfo(
                destLabels,
                channel.destination().getPort()
        );

        TrainExtraData trainExtraData = KasugaLib
                .STACKS
                .RAILWAY
                .sided(isClient)
                .get()
                .withTrainExtraData(trainLabel.getId());

        if(trainExtraData == null){
            channel.close();
            return;
        }

        trainExtraData
                .router
                .$onConnect(channel.proxy(newSource, newDest));
    }
}
