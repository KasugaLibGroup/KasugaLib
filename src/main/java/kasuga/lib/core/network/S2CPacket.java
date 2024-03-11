package kasuga.lib.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public abstract class S2CPacket extends Packet {

    public S2CPacket() {super();}
    public S2CPacket(FriendlyByteBuf buf) {super(buf);}
    @Override
    public boolean onReach(NetworkEvent.Context context) {
        context.enqueueWork(() -> handle(Minecraft.getInstance()));
        return true;
    }

    public abstract void handle(Minecraft minecraft);
    public abstract void encode(FriendlyByteBuf buf);
}
