package kasuga.lib.core.webserver.packets;

import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class S2CAuthenticateForwardPacket extends S2CPacket {
    String token;
    @Override
    public void handle(Minecraft minecraft) {

    }

    @Override
    public void encode(FriendlyByteBuf buf) {

    }
}
