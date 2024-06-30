package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.gui.GuiEngine;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderTickEvent {
    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent renderTickEvent){
        KasugaLib.STACKS.JAVASCRIPT.renderTick();
        KasugaLib.STACKS.GUI.renderTick();
    }
}
