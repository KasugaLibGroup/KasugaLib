package kasuga.lib.core.webserver.packets;

import kasuga.lib.KasugaLib;
import kasuga.lib.KasugaLibConfig;
import kasuga.lib.core.network.S2CPacket;
import kasuga.lib.core.webserver.KasugaClientFastAuthenticator;
import kasuga.lib.core.webserver.KasugaHttpServer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.net.MalformedURLException;
import java.net.URL;

public class S2COpenWebUIPacket extends S2CPacket {

    String path;

    public S2COpenWebUIPacket(String path) {
        this.path = path;
    }

    public S2COpenWebUIPacket(FriendlyByteBuf byteBuf) {
        path = byteBuf.readUtf();
    }

    @Override
    public void handle(Minecraft minecraft) {
        Player player = minecraft.player;

        if(player != null)
            return;

        if(!KasugaHttpServer.isClientHttpServerStart()) {
            player.sendSystemMessage(Component.translatable("msg.kasuga_lib.server_not_started"));
        }

        String connectionUrl = KasugaClientFastAuthenticator.authenticate(path);

        player.sendSystemMessage(
                Component.translatable("msg.kasuga_lib.connection", connectionUrl)
                        .setStyle(
                                Style.EMPTY
                                        .withClickEvent(new net.minecraft.network.chat.ClickEvent(
                                                net.minecraft.network.chat.ClickEvent.Action.OPEN_URL,
                                                connectionUrl
                                        ))
                        )
        );
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(path);
    }
}
