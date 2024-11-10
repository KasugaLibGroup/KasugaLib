package kasuga.lib.core.channel.network;

import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelSocket;
import net.minecraft.nbt.CompoundTag;

public class NetworkChannelSocket implements ChannelSocket {
    private final NetworkManager manager;
    private final long networkId;
    private final NetworkDuplexer duplexer;
    private boolean internalClosed;
    private boolean internalEstablished;

    NetworkChannelSocket(NetworkManager manager, NetworkDuplexer duplexer, long networkId){
        this.manager = manager;
        this.duplexer = duplexer;
        this.networkId = networkId;
    }

    @Override
    public void onMessage(CompoundTag message) {
        manager.sendMessage(this, message);
    }

    @Override
    public void onEstablished() {
        if(internalEstablished){
            return;
        }
        internalEstablished = true;
        manager.sendEstablished(this);
    }

    @Override
    public void onClose() {
        if(internalClosed)
            return;
        internalClosed = true;
        duplexer.removeConnection(networkId);
        manager.sendClose(this);
    }

    @Override
    public void setChannel(Channel channel) {}

    public void internalClose() {
        this.internalClosed = true;
    }

    public long getNetworkId() {
        return networkId;
    }

    public boolean isDuplexedBy(NetworkDuplexer duplexer){
        return this.duplexer == duplexer;
    }

    public void internalEstablish() {
        this.internalEstablished = true;
    }
}
