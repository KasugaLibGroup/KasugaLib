package kasuga.lib.core.channel.route;

import kasuga.lib.core.channel.network.address.NetworkAddressTypes;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.network.address.MinecraftClientPlayerAddress;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class PlayerLabelMatchRule implements RouteRule {
    private final RouteTarget target;
    private final ServerPlayer player;

    @Override
    public Optional<RouteTarget> route(Channel channel) {
        if (
                channel.destination().address().getType() == NetworkAddressTypes.PLAYER_ADDRESS &&
                ((MinecraftClientPlayerAddress)channel.destination().address()).getPlayer() == player
        ) {
            return Optional.of(target);
        }
        return Optional.empty();
    }

    public PlayerLabelMatchRule(RouteTarget target, ServerPlayer player) {
        this.target = target;
        this.player = player;
    }

    public static PlayerLabelMatchRule create(ServerPlayer player, RouteTarget target) {
        return new PlayerLabelMatchRule(target, player);
    }
}
