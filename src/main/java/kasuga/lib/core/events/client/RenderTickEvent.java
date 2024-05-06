package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.KasugaLibStacks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderTickEvent {
    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent renderTickEvent){
        KasugaLib.STACKS.GUI_MANAGER.renderTick();
    }
}
