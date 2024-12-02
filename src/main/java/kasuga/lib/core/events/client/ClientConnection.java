package kasuga.lib.core.events.client;


import kasuga.lib.KasugaLib;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;

public class ClientConnection {

    public static void onClientConnect(ClientPlayerNetworkEvent.LoggingIn loggingIn){
        KasugaLib.STACKS.CHANNEL.enableClient();
    }
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut loggingOut){
        KasugaLib.STACKS.CHANNEL.disableClient();
        KasugaLib.STACKS.CHANNEL.reloadClient();
    }
}
