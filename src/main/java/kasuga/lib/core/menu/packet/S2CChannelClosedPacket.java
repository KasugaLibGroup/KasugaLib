package kasuga.lib.core.menu.packet;

import kasuga.lib.core.menu.GuiMenu;
import kasuga.lib.core.menu.GuiMenuManager;
import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class S2CChannelClosedPacket extends S2CPacket {

    private UUID remoteId;
    private UUID localId;

    public S2CChannelClosedPacket(UUID remoteId, UUID localId){
        this.remoteId = remoteId;
        this.localId = localId;
    }

    public S2CChannelClosedPacket(FriendlyByteBuf buf){
        this.remoteId = buf.readUUID();
        this.localId = buf.readUUID();
    }

    @Override
    public void handle(Minecraft minecraft) {
        GuiMenu guiMenu = GuiMenuManager.findMenuFromClient(localId);
        if(guiMenu != null)
            return;
        guiMenu.onClose(remoteId);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.remoteId);
        buf.writeUUID(this.localId);
    }
}
