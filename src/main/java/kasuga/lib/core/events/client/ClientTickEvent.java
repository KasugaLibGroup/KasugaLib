package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.registrations.common.KeyBindingReg;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientTickEvent {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        if(KasugaLib.STACKS.JAVASCRIPT.GROUP_CLIENT != null){
            KasugaLib.STACKS.JAVASCRIPT.GROUP_CLIENT.dispatchTick();
        }

        if(event.phase == TickEvent.Phase.END){
            KeyBindingReg.onClientTick();
        }
    }
}
