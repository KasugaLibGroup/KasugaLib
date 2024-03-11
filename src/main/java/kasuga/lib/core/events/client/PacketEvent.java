package kasuga.lib.core.events.client;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkEvent;

public class PacketEvent {

    @SubscribeEvent
    public static void onClientPayloadHandleEvent(NetworkEvent.ClientCustomPayloadEvent event) {
        System.out.println();
    }

    @SubscribeEvent
    public static void onServerPayloadHandleEvent(NetworkEvent.ServerCustomPayloadEvent event) {
        System.out.println();
    }
}
