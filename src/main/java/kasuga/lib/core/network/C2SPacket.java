package kasuga.lib.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public abstract class C2SPacket extends Packet {
    public C2SPacket(){}
    public C2SPacket(FriendlyByteBuf buf) {}

    public boolean onReach(NetworkEvent.Context context) {
        context.enqueueWork(() -> handle(context));
        return true;
    }


    public abstract void handle(NetworkEvent.Context context);
    abstract public void encode(FriendlyByteBuf buf);
}
