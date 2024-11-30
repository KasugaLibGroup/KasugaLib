package kasuga.lib.core.channel.network;

import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelSocket;
import kasuga.lib.core.channel.peer.ChannelStatus;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class NetworkDuplexer {

    AtomicLong networkIdGenerator = new AtomicLong(1);
    HashMap<Long, Channel> channels = new HashMap<>();
    HashMap<Long, NetworkChannelSocket> sockets = new HashMap<>();

    public long createConnection(Channel channel){
        long networkId = networkIdGenerator.getAndIncrement();
        channels.put(networkId, channel);
        return networkId;
    }

    public boolean createConnection(Channel channel, long networkId){
        if(channels.containsKey(networkId))
            return false;
        channels.put(networkId, channel);
        return true;
    }

    public void establishConnection(long networkId, NetworkChannelSocket socket){
        Channel channel = channels.get(networkId);
        if(channel == null)
            return;
        channel.establish(socket);
        sockets.put(networkId, socket);
    }

    public void sendMessageAsRemote(long networkId, CompoundTag message){
        Channel channel = channels.get(networkId);
        NetworkChannelSocket socket = sockets.get(networkId);
        if(channel == null || socket == null)
            return;
        channel.sendMessage(socket, message);
    }

    public void closeConnection(long networkId){
        Channel channel = channels.get(networkId);
        NetworkChannelSocket socket = sockets.get(networkId);
        if(channel == null)
            return;
        if(socket != null)
            socket.internalClose();
        channel.close();
        sockets.remove(networkId);
        channels.remove(networkId);
    }

    public void removeConnection(long networkId) {
        sockets.remove(networkId);
        channels.remove(networkId);
    }

    public void onExistedConnectionEstablished(long networkId, NetworkChannelSocket socket) {
        sockets.put(networkId, socket);
    }

    public void close(){
        ArrayList<Channel> currentChannels = new ArrayList<>(channels.values());
        for(Channel channel : currentChannels){
            channel.close();
        }
        channels.clear();
        sockets.clear();
    }
}
