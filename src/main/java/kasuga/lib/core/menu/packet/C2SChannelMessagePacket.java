package kasuga.lib.core.menu.packet;

import kasuga.lib.core.menu.GuiMenu;
import kasuga.lib.core.menu.GuiMenuManager;
import kasuga.lib.core.network.C2SPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class C2SChannelMessagePacket extends C2SPacket {

    UUID fromUUID;

    UUID toUUID;

    CompoundTag data;

    public C2SChannelMessagePacket(UUID fromUUID, UUID toUUID, CompoundTag data){
        this.fromUUID = fromUUID;
        this.toUUID = toUUID;
        this.data = data;
    }

    public C2SChannelMessagePacket(FriendlyByteBuf byteBuf){
        fromUUID = byteBuf.readUUID();
        toUUID = byteBuf.readUUID();
        data = byteBuf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(fromUUID);
        buf.writeUUID(toUUID);
        buf.writeNbt(data);
    }

    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(()->{
            GuiMenu guiMenu = GuiMenuManager.findMenuFromServer(toUUID);
            if(guiMenu != null)
                return;
            guiMenu.onMessage(fromUUID, data);
        });
    }
}
