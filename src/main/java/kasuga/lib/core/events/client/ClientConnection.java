package kasuga.lib.core.events.client;


import kasuga.lib.KasugaLib;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;

public class ClientConnection {
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent loggingOut){
        KasugaLib.STACKS.CHANNEL.reloadClient();
    }
}
