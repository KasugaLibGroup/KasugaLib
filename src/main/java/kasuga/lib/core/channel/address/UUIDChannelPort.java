package kasuga.lib.core.channel.address;

import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.UUID;

public class UUIDChannelPort extends ChannelPort {
    private final UUID uuid;

    public UUIDChannelPort(UUID uuid) {
        this.uuid = uuid;
    }

    public static UUIDChannelPort of(UUID uuid) {
        return new UUIDChannelPort(uuid);
    }

    public static ChannelPort read(FriendlyByteBuf byteBuf) {
        return new UUIDChannelPort(byteBuf.readUUID());
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeByte(0x01);
        byteBuf.writeUUID(uuid);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        UUIDChannelPort that = (UUIDChannelPort) object;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
