package kasuga.lib.core.channel.network.address;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.KasugaLibStacks;
import kasuga.lib.core.channel.address.LabelType;
import kasuga.lib.core.channel.network.NetworkSeriaizableType;

public class NetworkAddressTypes {
    public static final LabelType<MinecraftClientPlayerAddress> PLAYER_ADDRESS = KasugaLib.STACKS.CHANNEL.labelTypeRegistry.register(
            KasugaLibStacks.REGISTRY.asResource("player_address"),
            new LabelType<MinecraftClientPlayerAddress>(MinecraftClientPlayerAddress::new)
    );

    public static final LabelType<MinecraftServerAddress> SERVER = KasugaLib.STACKS.CHANNEL.labelTypeRegistry.register(
            KasugaLibStacks.REGISTRY.asResource("server"),
            new LabelType<MinecraftServerAddress>(MinecraftServerAddress::new)
    );


    public static final LabelType<PlainStringAddress> PLAIN_STRING = KasugaLib.STACKS.CHANNEL.labelTypeRegistry.register(
            KasugaLibStacks.REGISTRY.asResource("string"),
            new LabelType<PlainStringAddress>(PlainStringAddress::new)
    );

    public static void invoke() {}
}
