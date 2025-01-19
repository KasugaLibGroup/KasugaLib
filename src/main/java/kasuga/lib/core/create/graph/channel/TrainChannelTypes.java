package kasuga.lib.core.create.graph.channel;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.address.LabelType;
import kasuga.lib.core.channel.route.*;

public class TrainChannelTypes {
    public static final LabelType<TrainLabel> TRAIN = new LabelType<>(TrainLabel::deserialize);

    public static void inoke(){
        KasugaLib.STACKS.CHANNEL.SERVER_ROUTER.addRule(new TargetLabelMatchRule(new TrainTarget(false), TRAIN));
        KasugaLib.STACKS.CHANNEL.CLIENT_ROUTER.addRule(new TargetLabelMatchRule(new TrainTarget(true), TRAIN));
    }
}
