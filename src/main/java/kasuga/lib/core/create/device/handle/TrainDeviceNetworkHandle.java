package kasuga.lib.core.create.device.handle;

import com.simibubi.create.content.trains.entity.Carriage;
import kasuga.lib.core.channel.peer.FeaturedChannelPeer;

public class TrainDeviceNetworkHandle {

    protected boolean clientSide;

    protected FeaturedChannelPeer peer;

    public TrainDeviceNetworkHandle(
            boolean clientSide,
            Carriage carriage
    ) {

    }



    public boolean isClientSide(){
        return clientSide;
    }
}
