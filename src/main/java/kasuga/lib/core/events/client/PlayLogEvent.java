package kasuga.lib.core.events.client;

import kasuga.lib.core.KasugaLibClient;
import kasuga.lib.core.client.model.anim_instance.AnimateTickerManager;
import kasuga.lib.core.util.Envs;
import kasuga.lib.core.util.projectile.Panel;
import kasuga.lib.core.util.projectile.PanelRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayLogEvent {

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        AnimateTickerManager.INSTANCE.resetTicks();
        Player player = event.getEntity();
        if (!Envs.isDevEnvironment()) return;
        Panel.test = new PanelRenderer(player.getForward(), Vec3.ZERO);
        // TODO: deal with this
        KasugaLibClient.PANEL_RENDERERS.add(Panel.test);
    }

    @SubscribeEvent
    public static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        AnimateTickerManager.INSTANCE.resetTicks();
        if (!Envs.isDevEnvironment()) return;
        KasugaLibClient.PANEL_RENDERERS.clear();
    }
}
