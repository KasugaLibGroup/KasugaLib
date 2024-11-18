package kasuga.lib.core.menu.network;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.menu.locator.MenuLocator;
import kasuga.lib.core.menu.locator.MenuLocatorRegistry;
import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerLocatorChangePacket extends S2CPacket {
    private final MenuLocator locator;
    private final List<UUID> knownData;

    public ServerLocatorChangePacket(MenuLocator locator, List<UUID> knownData) {
        this.locator = locator;
        this.knownData = knownData;
    }

    public ServerLocatorChangePacket(FriendlyByteBuf buf) {
        this.locator = KasugaLib.STACKS.MENU.getLocatorRegistry().read(buf);
        this.knownData = new ArrayList<>();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            knownData.add(buf.readUUID());
        }
    }

    @Override
    public void handle(Minecraft minecraft) {
        KasugaLib.STACKS.MENU.notifyMenuChange(locator, knownData);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        locator.write(buf);
        buf.writeVarInt(knownData.size());
        for (UUID uuid : knownData) {
            buf.writeUUID(uuid);
        }
    }

    public MenuLocator getLocator() {
        return locator;
    }

    public List<UUID> getKnownData() {
        return knownData;
    }
}
