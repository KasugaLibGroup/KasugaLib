package kasuga.lib.core.events.client;

import kasuga.lib.registrations.client.KeyBindingReg;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientTickEvent {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            KeyBindingReg.onClientTick();
        }
    }
}