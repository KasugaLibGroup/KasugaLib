package kasuga.lib.core.packets;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.menu.packet.*;
import kasuga.lib.core.packets.gui.DevOpenScreenPacket;
import kasuga.lib.registrations.common.ChannelReg;
import kasuga.lib.registrations.registry.SimpleRegistry;

public class AllPackets {
    public static final SimpleRegistry REGISTRY = new SimpleRegistry(KasugaLib.MOD_ID, KasugaLib.EVENTS);

    public static ChannelReg channel = new ChannelReg("kasuga/packets")
            .brand("1.0")
            .loadPacket(DevOpenScreenPacket.class,DevOpenScreenPacket::new)
            .loadPacket(C2SConnectMenuPacket.class, C2SConnectMenuPacket::new)
            .loadPacket(S2CConeectMenuResponsePacket.class, S2CConeectMenuResponsePacket::new)
            .loadPacket(S2CChannelClosedPacket.class, S2CChannelClosedPacket::new)
            .loadPacket(C2SChannelClosedPacket.class, C2SChannelClosedPacket::new)
            .loadPacket(S2CChannelMessagePacket.class, S2CChannelMessagePacket::new)
            .loadPacket(C2SChannelMessagePacket.class, C2SChannelMessagePacket::new)
            .submit(REGISTRY);

    public static void init(){
        REGISTRY.submit();
    }
}
