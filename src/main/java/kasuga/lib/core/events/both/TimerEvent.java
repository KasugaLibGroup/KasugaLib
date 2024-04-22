package kasuga.lib.core.events.both;

import kasuga.lib.core.KasugaTimer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TimerEvent {
    @SubscribeEvent
    public static void onClientSideTick(TickEvent.ClientTickEvent clientTickEvent){
        KasugaTimer.CLIENT.onTick();
    }
}
