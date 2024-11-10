package kasuga.lib.core.channel.network.address;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.network.NetworkSeriaizableType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class MinecraftClientPlayerAddress extends Label {
    public MinecraftClientPlayerAddress(ServerPlayer player){
        this();
    }

    public MinecraftClientPlayerAddress(FriendlyByteBuf byteBuf) {
        this();
    }

    public MinecraftClientPlayerAddress() {
        super(NetworkAddressTypes.PLAYER_ADDRESS);
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {}
}
