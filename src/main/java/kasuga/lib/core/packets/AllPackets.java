package kasuga.lib.core.packets;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.packets.ChannelNetworkPacket;
import kasuga.lib.core.menu.network.BlockEntityMenuIdSyncPacket;
import kasuga.lib.core.menu.network.ServerLocatorChangePacket;
import kasuga.lib.core.webserver.packets.C2SOpenWebUIPacket;
import kasuga.lib.core.webserver.packets.S2COpenWebUIPacket;
import kasuga.lib.registrations.common.ChannelReg;
import kasuga.lib.registrations.registry.SimpleRegistry;

public class AllPackets {
    public static final SimpleRegistry REGISTRY = new SimpleRegistry(KasugaLib.MOD_ID, KasugaLib.EVENTS);

    public static final ChannelReg CHANNEL_REG = new ChannelReg("root")
            .brand("1.0")
            .loadPacket(BlockEntityMenuIdSyncPacket.class, BlockEntityMenuIdSyncPacket::new)
            .loadPacket(ServerLocatorChangePacket.class, ServerLocatorChangePacket::new)
            .loadPacket(C2SOpenWebUIPacket.class, C2SOpenWebUIPacket::new)
            .loadPacket(S2COpenWebUIPacket.class, S2COpenWebUIPacket::new)
            .submit(REGISTRY);

    public static void init(){
        REGISTRY.submit();
    }
}
