package kasuga.lib.core.channel.network.address;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.network.NetworkSeriaizableType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.Lazy;

import java.util.Objects;

public class MinecraftServerAddress extends Label {
    int x;
    public static final Lazy<MinecraftServerAddress> INSTANCE = Lazy.of(()->new MinecraftServerAddress());
    protected MinecraftServerAddress() {
        super(NetworkAddressTypes.SERVER);
    }

    public MinecraftServerAddress(FriendlyByteBuf byteBuf) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {}

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        return object != null && getClass() == object.getClass();
    }

    @Override
    public int hashCode() {
        return 25565;
    }
}
