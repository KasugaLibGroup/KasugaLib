package kasuga.lib.core.channel.peer;

import kasuga.lib.core.channel.address.ChannelPort;
import kasuga.lib.core.channel.address.Label;

import java.util.HashMap;
import java.util.function.Function;

public class FeaturedChannelPeer extends ChannelPeer {
    public FeaturedChannelPeer(Label address) {
        super(address);
    }

    HashMap<ChannelPort, Function<ChannelPeerSocketServer, ChannelHandler>> openedPorts = new HashMap<>();

    public void openPort(ChannelPort port, Function<ChannelPeerSocketServer, ChannelHandler> handler){
        openedPorts.put(port, handler);
    }

    public void closePort(ChannelPort port){
        openedPorts.remove(port);
    }

    @Override
    protected boolean onConnect(ChannelPeerSocketServer server) {
        ChannelPort port = server.getChannel().destination().getPort();
        if(openedPorts.containsKey(port)){
            server.setHandler(openedPorts.get(port).apply(server));
            return true;
        }
        return false;
    }
}
