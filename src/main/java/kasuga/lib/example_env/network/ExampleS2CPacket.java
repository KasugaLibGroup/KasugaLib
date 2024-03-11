package kasuga.lib.example_env.network;

import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class ExampleS2CPacket extends S2CPacket {

    public ExampleS2CPacket() {
        super();
    }

    public ExampleS2CPacket(FriendlyByteBuf buf) {
        super(buf);
    }
    @Override
    public void handle(Minecraft minecraft) {
        System.out.println("I am S2C");
    }

    @Override
    public void encode(FriendlyByteBuf buf) {

    }
}
