package kasuga.lib.core.channel.network.address;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.network.NetworkSeriaizableType;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.Charset;

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
        this.name = byteBuf.readCharSequence(byteBuf.readInt(), Charset.defaultCharset()).toString();
    }

    public static PlainStringAddress of(String client) {
        return new PlainStringAddress(client);
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(name.length());
        byteBuf.writeCharSequence(name, Charset.defaultCharset());
    }
}
