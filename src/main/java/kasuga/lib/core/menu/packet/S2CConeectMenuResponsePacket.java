package kasuga.lib.core.menu.packet;

import kasuga.lib.core.menu.GuiMenu;
import kasuga.lib.core.menu.GuiMenuManager;
import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class S2CConeectMenuResponsePacket extends S2CPacket {
    UUID localUUID;
    UUID remoteUUID;

    boolean established;
    public S2CConeectMenuResponsePacket(UUID localUUID, UUID remoteUUID, boolean established){
        this.localUUID = localUUID;
        this.remoteUUID = remoteUUID;
        this.established = established;
    }

    public S2CConeectMenuResponsePacket(FriendlyByteBuf buf){
        this.localUUID = buf.readUUID();
        this.remoteUUID = buf.readUUID();
        this.established = buf.readBoolean();
    }

    @Override
    public void handle(Minecraft minecraft) {
        GuiMenu localMenu = GuiMenuManager.findMenuFromClient(localUUID);
        if(localMenu == null)
            return;
        localMenu.removeConnecting(remoteUUID);
        if(established)
            localMenu.addRemote(remoteUUID);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(localUUID);
        buf.writeUUID(remoteUUID);
        buf.writeBoolean(established);
    }
}
