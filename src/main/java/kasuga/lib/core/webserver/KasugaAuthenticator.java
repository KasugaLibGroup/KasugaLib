package kasuga.lib.core.webserver;

import kasuga.lib.KasugaLibConfig;
import kasuga.lib.core.packets.AllPackets;
import kasuga.lib.core.webserver.packets.C2SOpenWebUIPacket;
import kasuga.lib.core.webserver.packets.S2COpenWebUIPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class KasugaAuthenticator {

    public static enum Direction {
        BOTH,
        CLIENT,
        SERVER
    }
    public static void notifyOpen(Player player, String path, Direction preferDirection) {
        String authenticateUrl = null;
        if(
                player.level().isClientSide() ||
                DistExecutor.unsafeCallWhenOn(Dist.CLIENT, ()->()->WebServerMinecraftClientUtils.isIntergratedServer(player.getServer()))
        ) {

            if(preferDirection == Direction.SERVER) {
                AllPackets.CHANNEL_REG.sendToServer(new C2SOpenWebUIPacket(path));
                return;
            }

            authenticateUrl = KasugaClientFastAuthenticator.authenticate(path);
            if(authenticateUrl != null && authenticateUrl.length() > 0) {
                player.sendSystemMessage(
                        Component.translatable("msg.kasuga_lib.connection", authenticateUrl)
                                .setStyle(
                                        Component.empty().getStyle()
                                                .withClickEvent(new net.minecraft.network.chat.ClickEvent(
                                                        net.minecraft.network.chat.ClickEvent.Action.OPEN_URL,
                                                        authenticateUrl
                                                ))
                                )
                );
            } else AllPackets.CHANNEL_REG.sendToServer(new C2SOpenWebUIPacket(path));
            return;
        } else if(player instanceof ServerPlayer serverPlayer) {
            if(KasugaHttpServer.isServerHttpServerStart() && preferDirection != Direction.CLIENT) {
                int status = KasugaServerAuthenticator.getURL(player, path, true);
                if(status == 1)
                    return;
            }
            AllPackets.CHANNEL_REG.sendToClient(new S2COpenWebUIPacket(path), (ServerPlayer) player);
        }
    }
}
