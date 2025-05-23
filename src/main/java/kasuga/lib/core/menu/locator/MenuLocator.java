package kasuga.lib.core.menu.locator;

import kasuga.lib.core.channel.network.NetworkSeriaizableType;
import kasuga.lib.core.channel.network.NetworkSerializable;
import kasuga.lib.core.menu.network.BlockEntityMenuIdSyncPacket;
import kasuga.lib.core.menu.network.ServerLocatorChangePacket;
import kasuga.lib.core.network.Packet;
import kasuga.lib.core.packets.AllPackets;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.List;


public abstract class MenuLocator implements NetworkSerializable {
    private NetworkSeriaizableType<?> type;
    private LocatedMenuManager manager;
    protected final HashSet<Connection> tracking = new HashSet<>();

    public MenuLocator(MenuLocatorType<?> type) {
        this.type = type;
    }

    @Override
    public NetworkSeriaizableType<?> getType() {
        return type;
    }

    public void enable(LocatedMenuManager manager){
        this.manager = manager;
    }

    public void disable(LocatedMenuManager manager){
        this.manager = null;
        broadcastDisable();
    }


    public void sendUpTo(Connection connection){
        if(!connection.isConnected())
            return;
        AllPackets.CHANNEL_REG.sendTo(
                new ServerLocatorChangePacket(this, manager.asServer()),
                connection,
                NetworkDirection.PLAY_TO_CLIENT
        );
        tracking.add(connection);
    }


    public void sendDownTo(Connection connection){
        if(!connection.isConnected())
            return;
        AllPackets.CHANNEL_REG.sendTo(
                new ServerLocatorChangePacket(this, manager.asServer()),
                connection,
                NetworkDirection.PLAY_TO_CLIENT
        );
        tracking.remove(connection);
    }

    protected void broadcastDisable() {
        for(Connection connection : tracking){
            sendDownTo(connection);
        }
        tracking.clear();
    }

    public void tick(){}
}
