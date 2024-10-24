package kasuga.lib.core.menu.packet;

import kasuga.lib.core.menu.GuiMenu;
import kasuga.lib.core.menu.GuiMenuManager;
import kasuga.lib.core.network.C2SPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class C2SChannelClosedPacket extends C2SPacket {

    private final UUID remoteId;
    private final UUID localId;

    public C2SChannelClosedPacket(FriendlyByteBuf buf){
        remoteId = buf.readUUID();
        localId = buf.readUUID();
    }

    public C2SChannelClosedPacket(UUID remoteId, UUID localId){
        this.remoteId = remoteId;
        this.localId = localId;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(()->{
            GuiMenu guiMenu = GuiMenuManager.findMenuFromServer(localId);
            if(guiMenu != null)
                return;
            guiMenu.onClose(remoteId);
        });
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(remoteId);
        buf.writeUUID(localId);
    }
}
