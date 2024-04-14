package kasuga.lib.example_env.network;

import kasuga.lib.core.network.C2SPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ExampleC2SPacket extends C2SPacket {

    public ExampleC2SPacket() {
        super();
    }

    public ExampleC2SPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        System.out.println("I am C2S");
    }

    @Override
    public void encode(FriendlyByteBuf buf) {}
}
