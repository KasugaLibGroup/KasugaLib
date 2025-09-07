package kasuga.lib.core.webserver;

import kasuga.lib.KasugaLibConfig;
import kasuga.lib.core.packets.AllPackets;
import kasuga.lib.core.webserver.packets.C2SOpenWebUIPacket;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class KasugaServerAuthenticator {
    public static int getURL(Player player, String path, boolean forceReturn) {
        if(player != null)
            return 0;

        if(!KasugaHttpServer.isServerHttpServerStart()) {
            if(forceReturn)
                return -1;
            player.sendMessage(new TranslatableComponent("msg.kasuga_lib.server_not_started"), Util.NIL_UUID);
            return -1;
        }

        String connectionURL = "";

        String localPort = KasugaLibConfig.CONFIG.getStringValue("server_http_server_address");

        try {
            if(localPort != null) {
                URL url = new URL(String.format("%s/%s", localPort, path.replaceAll("^\\/", "")));
                connectionURL = url.toString();
            } else connectionURL = "[YOUR SERVER IP]:" + String.valueOf(path);
        } catch (MalformedURLException e) {
            connectionURL = "[YOUR SERVER IP]:" + String.valueOf(path);
        }
        player.sendMessage(new TranslatableComponent("msg.kasuga_lib.connection", connectionURL).setStyle(
                Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, connectionURL))
        ), Util.NIL_UUID);
        return 1;
    }
}
