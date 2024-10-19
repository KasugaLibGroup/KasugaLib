package kasuga.lib.core.events.client;

import kasuga.lib.core.client.model.anim_instance.AnimateTickerManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayLogEvent {

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        AnimateTickerManager.INSTANCE.resetTicks();
    }

    @SubscribeEvent
    public static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        AnimateTickerManager.INSTANCE.resetTicks();
    }
}
