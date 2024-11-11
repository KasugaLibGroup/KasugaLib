package kasuga.lib.core.channel.address;

import net.minecraft.network.FriendlyByteBuf;

public abstract class ChannelPort {
    public abstract void write(FriendlyByteBuf byteBuf);
    public static ChannelPort read(FriendlyByteBuf byteBuf){
        byte type = byteBuf.readByte();
        switch (type){
            case 0:
                return FeatureChannelPort.read(byteBuf);
            case 1:
                return UUIDChannelPort.read(byteBuf);
            default:
                throw new IllegalArgumentException("Unknown ChannelPort type: " + type);
        }
    }
}
