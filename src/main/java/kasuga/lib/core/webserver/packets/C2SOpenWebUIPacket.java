package kasuga.lib.core.webserver.packets;

import kasuga.lib.core.network.C2SPacket;
import kasuga.lib.core.webserver.KasugaServerAuthenticator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class C2SOpenWebUIPacket extends C2SPacket {

    String path;


    public C2SOpenWebUIPacket(String path) {
        this.path = path;
    }

    public C2SOpenWebUIPacket(FriendlyByteBuf byteBuf) {
        path = byteBuf.readUtf();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(path);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(()->{
            ServerPlayer player = context.getSender();
            KasugaServerAuthenticator.getURL(player, path, false);
        });
    }

}
