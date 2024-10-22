package kasuga.lib.core.menu.packet;

import kasuga.lib.core.menu.GuiMenu;
import kasuga.lib.core.menu.GuiMenuManager;
import kasuga.lib.core.network.C2SPacket;
import kasuga.lib.core.packets.AllPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class C2SConnectMenuPacket extends C2SPacket {
    UUID localUUID;
    UUID remoteUUID;

    public C2SConnectMenuPacket(UUID localUUID, UUID remoteUUID){
        this.localUUID = localUUID;
        this.remoteUUID = remoteUUID;
    }

    public C2SConnectMenuPacket(FriendlyByteBuf byteBuf){
        this.localUUID = byteBuf.readUUID();
        this.remoteUUID = byteBuf.readUUID();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(()->{
            GuiMenu menu = GuiMenuManager.findMenuFromServer(remoteUUID);
            boolean established = false;
            if(menu != null)
                established = menu.addRemote(localUUID, context.getSender());
            S2CConeectMenuResponsePacket response = new S2CConeectMenuResponsePacket(localUUID, remoteUUID, established);
            AllPackets.channel.sendToClient(response, context.getSender());
        });
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.localUUID);
        buf.writeUUID(this.remoteUUID);
    }
}
