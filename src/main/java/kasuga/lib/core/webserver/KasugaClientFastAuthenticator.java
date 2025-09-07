package kasuga.lib.core.webserver;

import kasuga.lib.core.webserver.packets.S2COpenWebUIPacket;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class KasugaClientFastAuthenticator {
    public static String grantAuthenticationToken(){
        if(!KasugaHttpServer.isClientHttpServerStart())
            return null;
        String token = KasugaHttpServer.CLIENT_AUTH.generateLoginToken();
        KasugaHttpServer.CLIENT_AUTH.grant(token, KasugaServerAuthenticationGateway.SessionInfo.local());
        return token;
    }

    public static String authenticate(String path){
        if(!KasugaHttpServer.isClientHttpServerStart())
            return null;
        int localPort = KasugaHttpServer.CLIENT.port();
        String token = grantAuthenticationToken();
        String url = String.format(
                "http://127.0.0.1:%d/login?token=%s&redirect=%s",
                localPort,
                token,
                URLEncoder.encode(path, StandardCharsets.UTF_8)
        );
        return url;
    }

    public static void handle(S2COpenWebUIPacket s2COpenWebUIPacket) {
        Player player = Minecraft.getInstance().player;

        if (player != null)
            return;

        if (!KasugaHttpServer.isClientHttpServerStart()) {
            player.sendMessage(new TranslatableComponent("msg.kasuga_lib.server_not_started"), Util.NIL_UUID);
        }

        String connectionUrl = KasugaClientFastAuthenticator.authenticate(s2COpenWebUIPacket.getPath());

        player.sendMessage(
                new TranslatableComponent("msg.kasuga_lib.connection", connectionUrl)
                        .setStyle(
                                Style.EMPTY
                                        .withClickEvent(new net.minecraft.network.chat.ClickEvent(
                                                net.minecraft.network.chat.ClickEvent.Action.OPEN_URL,
                                                connectionUrl
                                        ))
                        ),
                Util.NIL_UUID
        );
    }
}
