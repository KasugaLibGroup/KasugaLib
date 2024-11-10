package kasuga.lib.core.channel.packets;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.KasugaLibStacks;
import kasuga.lib.registrations.common.ChannelReg;

public class ChannelNetworkPacket {
    public final ChannelReg channelReg = new ChannelReg("channel")
            .brand("1.0")
            .loadPacket(C2SChannelConnectionPacket.class, C2SChannelConnectionPacket::new)
            .loadPacket(S2CChannelConnectionPacket.class, S2CChannelConnectionPacket::new)
            .loadPacket(C2SChannelMessagePacket.class, C2SChannelMessagePacket::new)
            .loadPacket(S2CChannelMessagePacket.class, S2CChannelMessagePacket::new)
            .loadPacket(C2SChannelStateChangePacket.class, C2SChannelStateChangePacket::new)
            .loadPacket(S2CChannelStateChangePacket.class, S2CChannelStateChangePacket::new)
            .submit(KasugaLib.STACKS.REGISTRY);

    public static void invoke(){}
}
