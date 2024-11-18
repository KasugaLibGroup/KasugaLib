package kasuga.lib.core.events.server;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.menu.GuiMenuManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ServerConnectionListeners {
    public static void onClientDisconnect(PlayerEvent.PlayerLoggedOutEvent playerEvent){
        if(playerEvent.getEntity() instanceof ServerPlayer serverPlayer)
            KasugaLib.STACKS.CHANNEL.closeServer(serverPlayer);
    }
}
