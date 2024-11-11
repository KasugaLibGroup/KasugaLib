package kasuga.lib.core.channel.packets;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.network.C2SPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class C2SChannelMessagePacket extends C2SPacket {
    long networkId;
    CompoundTag message;
    boolean isConnectionSender;

    public C2SChannelMessagePacket(long channelId, CompoundTag message, boolean isConnectionSender) {
        this.networkId = channelId;
        this.message = message;
        this.isConnectionSender = isConnectionSender;
    }

    public C2SChannelMessagePacket(FriendlyByteBuf buf) {
        this.networkId = buf.readLong();
        this.message = buf.readNbt();
        this.isConnectionSender = buf.readBoolean();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        KasugaLib.STACKS.CHANNEL.server(context.getSender()).onMessage(networkId, message, isConnectionSender);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(networkId);
        buf.writeNbt(message);
        buf.writeBoolean(isConnectionSender);
    }
}
