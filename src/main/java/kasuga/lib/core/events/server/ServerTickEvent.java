package kasuga.lib.core.events.server;

import kasuga.lib.KasugaLib;
import net.minecraftforge.event.TickEvent;

public class ServerTickEvent {
    public static void onServerTick(TickEvent.ServerTickEvent serverTickEvent){
        KasugaLib.STACKS.JAVASCRIPT.GROUP_SERVER.dispatchTick();
    }
}
