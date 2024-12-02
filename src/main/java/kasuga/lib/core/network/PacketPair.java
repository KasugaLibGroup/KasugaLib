package kasuga.lib.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkDirection;

public abstract class PacketPair {

    public PacketPair() {}
    public abstract void encode(FriendlyByteBuf buf);
    public abstract void decode(FriendlyByteBuf buf);
}
