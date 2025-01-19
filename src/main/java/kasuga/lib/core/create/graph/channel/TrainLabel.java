package kasuga.lib.core.create.graph.channel;

import kasuga.lib.core.channel.address.Label;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class TrainLabel extends Label {
    protected TrainLabel() {
        super(TrainChannelTypes.TRAIN);
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {

    }

    public static TrainLabel deserialize(FriendlyByteBuf byteBuf) {
        return new TrainLabel();
    }

    public UUID getId() {
        return null;
    }
}
