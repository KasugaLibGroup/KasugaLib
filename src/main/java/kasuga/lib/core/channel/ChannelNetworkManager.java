package kasuga.lib.core.channel;

import kasuga.lib.core.channel.address.LabelTypeRegistry;
import kasuga.lib.core.channel.network.NetworkManager;
import kasuga.lib.core.channel.network.NetworkServerManager;
import kasuga.lib.core.channel.packets.ChannelNetworkPacket;
import kasuga.lib.core.channel.route.SimpleRouter;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

public class ChannelNetworkManager {
    public LabelTypeRegistry labelTypeRegistry = new LabelTypeRegistry();
    public ChannelNetworkPacket packet = new ChannelNetworkPacket();

    public SimpleRouter SERVER_ROUTER = new SimpleRouter();
    public HashMap<ServerPlayer, NetworkServerManager> SERVER = new HashMap<>();
    public SimpleRouter CLIENT_ROUTER = new SimpleRouter();
    public NetworkManager CLIENT = new NetworkManager(CLIENT_ROUTER);

    public NetworkManager server(ServerPlayer player){
        return SERVER.computeIfAbsent(player, k -> new NetworkServerManager(SERVER_ROUTER, player));
    }

    public void closeServer(ServerPlayer player){
        NetworkServerManager manager = SERVER.remove(player);
        if(manager != null){
            manager.close();
        }
    }

    public void closeClient(){
        CLIENT.close();
    }

    public void reloadClient(){
        closeClient();
        CLIENT = new NetworkManager(CLIENT_ROUTER);
    }

    public NetworkManager client(){
        return CLIENT;
    }
}
