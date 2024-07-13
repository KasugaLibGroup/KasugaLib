package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientTickEvent {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        if(KasugaLib.STACKS.JAVASCRIPT.GROUP_CLIENT != null){
            KasugaLib.STACKS.JAVASCRIPT.GROUP_CLIENT.dispatchTick();
        }
    }
}
