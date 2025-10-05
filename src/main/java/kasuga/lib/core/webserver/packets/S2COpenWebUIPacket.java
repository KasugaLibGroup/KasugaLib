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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

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
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()-> {
            KasugaClientFastAuthenticator.handle(this);
        });
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(path);
    }

    public String getPath() {
        return path;
    }
}
