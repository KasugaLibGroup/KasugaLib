package kasuga.lib.core.channel.network.address;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.network.NetworkSeriaizableType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.Lazy;

public class MinecraftServerAddress extends Label {
    public static final Lazy<MinecraftServerAddress> INSTANCE = Lazy.of(()->new MinecraftServerAddress());
    protected MinecraftServerAddress() {
        super(NetworkAddressTypes.SERVER);
    }

    public MinecraftServerAddress(FriendlyByteBuf byteBuf) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {}
}
