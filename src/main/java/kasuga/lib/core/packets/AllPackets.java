package kasuga.lib.core.packets;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.menu.packet.C2SConnectMenuPacket;
import kasuga.lib.core.menu.packet.S2CConeectMenuResponsePacket;
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
            .submit(REGISTRY);

    public static void init(){
        REGISTRY.submit();
    }
}
