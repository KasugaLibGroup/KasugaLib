package kasuga.lib.core.menu.packet;

import kasuga.lib.core.menu.GuiMenu;
import kasuga.lib.core.menu.GuiMenuManager;
import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class S2CChannelMessagePacket extends S2CPacket {
    UUID fromUUID;

    UUID toUUID;

    CompoundTag data;

    public S2CChannelMessagePacket(UUID fromUUID, UUID toUUID, CompoundTag data){
        this.fromUUID = fromUUID;
        this.toUUID = toUUID;
        this.data = data;
    }

    public S2CChannelMessagePacket(FriendlyByteBuf byteBuf){
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

    @Override
    public void handle(Minecraft minecraft) {
        GuiMenu guiMenu = GuiMenuManager.findMenuFromClient(toUUID);
        if(guiMenu == null)
            return;
        guiMenu.onMessage(fromUUID, data);
    }
}
