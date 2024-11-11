package kasuga.lib.core.channel.peer;

import kasuga.lib.core.channel.address.ConnectionInfo;
import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.concurrent.Computable;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    private final ConnectionInfo clientAddress;
    private final ConnectionInfo serverAddress;
    private ConnectionInfo proxySource;
    private ConnectionInfo proxyDest;
    private final ChannelSocket client;
    private final List<ChannelCloseListener> closeListeners = new ArrayList<>();
    private ChannelSocket server;
    private ChannelStatus status = ChannelStatus.PENDING;
    public Channel(
            ConnectionInfo clientAddress,
            ConnectionInfo serverAddress,
            ChannelSocket client
    ){
        this.clientAddress = clientAddress;
        this.serverAddress = serverAddress;
        this.client = client;
        client.setChannel(this);
    }

    public void establish(ChannelSocket server) {
        this.server = server;
        this.client.onEstablished();
        this.server.onEstablished();
        status = ChannelStatus.ESTABLISHED;
        server.setChannel(this);
    }

    public void close(){
        if(status == ChannelStatus.CLOSED)
            return;
        status = ChannelStatus.CLOSED;
        if(this.server != null){
            this.server.onClose();
        }

        if(this.client != null){
            this.client.onClose();
        }

        for(ChannelCloseListener listener : closeListeners){
            listener.onClose(this);
        }
    }

    public void sendMessage(boolean toServer, CompoundTag message){
        if(toServer){
            this.server.onMessage(message);
        }else{
            this.client.onMessage(message);
        }
    }

    public void sendMessage(ChannelSocket self, CompoundTag message){
        sendMessage(self != server, message);
    }

    public void addOnCloseListener(ChannelCloseListener listener) {
        closeListeners.add(listener);
    }

    public ConnectionInfo source(){
        if(proxySource != null){
            return proxySource;
        }
        return clientAddress;
    }

    public ConnectionInfo destination(){
        if(proxyDest != null){
            return proxyDest;
        }
        return serverAddress;
    }

    public boolean isEstablished() {
        return status == ChannelStatus.ESTABLISHED;
    }

    public boolean isClient(Channel channel) {
        return this.client == channel;
    }

    public Channel proxy(ConnectionInfo newSource, ConnectionInfo newDest) {
        proxySource = newSource;
        proxyDest = newDest;
        return this;
    }
}
