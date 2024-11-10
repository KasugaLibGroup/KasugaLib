package kasuga.lib.core.channel;

import kasuga.lib.core.channel.address.LabelTypeRegistry;
import kasuga.lib.core.channel.network.NetworkManager;
import kasuga.lib.core.channel.network.NetworkServerManager;
import kasuga.lib.core.channel.packets.ChannelNetworkPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;

public class ChannelNetworkManager {
    public LabelTypeRegistry labelTypeRegistry = new LabelTypeRegistry();
    public ChannelNetworkPacket packet = new ChannelNetworkPacket();
    public HashMap<ServerPlayer, NetworkServerManager> SERVER = new HashMap<>();

    public NetworkSwitcher SERVER_SWITCHER = new NetworkSwitcher();
    public NetworkSwitcher CLIENT_SWITCHER = new NetworkSwitcher();
    public NetworkManager CLIENT = new NetworkManager(CLIENT_SWITCHER);


    public NetworkManager server(ServerPlayer player){
        return SERVER.computeIfAbsent(player, k -> new NetworkServerManager(SERVER_SWITCHER, player));
    }

    public NetworkManager client(){
        return CLIENT;
    }
}
