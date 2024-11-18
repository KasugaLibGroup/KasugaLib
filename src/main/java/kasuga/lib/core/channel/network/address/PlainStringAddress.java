package kasuga.lib.core.channel.network.address;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.network.NetworkSeriaizableType;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class PlainStringAddress extends Label {
    String name;

    protected PlainStringAddress() {
        super(NetworkAddressTypes.PLAIN_STRING);
    }

    public PlainStringAddress(String name) {
        this();
        this.name = name;
    }

    public PlainStringAddress(FriendlyByteBuf byteBuf) {
        this();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        this.name = new String(bytes, StandardCharsets.UTF_8);
    }

    public static PlainStringAddress of(String client) {
        return new PlainStringAddress(client);
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PlainStringAddress that = (PlainStringAddress) object;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
