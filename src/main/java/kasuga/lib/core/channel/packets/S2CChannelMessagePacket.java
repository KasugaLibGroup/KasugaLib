package kasuga.lib.core.channel.packets;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class S2CChannelMessagePacket extends S2CPacket {
    long networkId;
    CompoundTag message;
    boolean isConnectionSender;

    public S2CChannelMessagePacket(long channelId, CompoundTag message, boolean isConnectionSender) {
        this.networkId = channelId;
        this.message = message;
        this.isConnectionSender = isConnectionSender;
    }

    public S2CChannelMessagePacket(FriendlyByteBuf buf) {
        this.networkId = buf.readLong();
        this.message = buf.readNbt();
    }

    @Override
    public void handle(Minecraft minecraft) {
        KasugaLib.STACKS.CHANNEL.client().onMessage(networkId, message, isConnectionSender);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(networkId);
        buf.writeNbt(message);
    }
}
