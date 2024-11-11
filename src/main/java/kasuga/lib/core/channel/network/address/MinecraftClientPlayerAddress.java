package kasuga.lib.core.channel.network.address;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.network.NetworkSeriaizableType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class MinecraftClientPlayerAddress extends Label {
    private ServerPlayer player;

    public MinecraftClientPlayerAddress(ServerPlayer player){
        this();
        this.player = player;
    }

    public MinecraftClientPlayerAddress(FriendlyByteBuf byteBuf) {
        this();
        throw new UnsupportedOperationException();
    }

    public MinecraftClientPlayerAddress() {
        super(NetworkAddressTypes.PLAYER_ADDRESS);
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        throw new UnsupportedOperationException();
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
