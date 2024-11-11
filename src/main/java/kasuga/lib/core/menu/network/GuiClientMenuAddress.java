package kasuga.lib.core.menu.network;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.address.LabelType;
import net.minecraft.network.FriendlyByteBuf;
import java.util.UUID;

public class GuiClientMenuAddress extends Label {
    private UUID menuId;

    GuiClientMenuAddress(){
        super(MenuAddressTypes.CLIENT);
    }

    public GuiClientMenuAddress(FriendlyByteBuf buf) {
        this();
        this.menuId = buf.readUUID();
    }
    
    private GuiClientMenuAddress(UUID menuId) {
        this();
        this.menuId = menuId;
    }
    
    public static GuiClientMenuAddress of(UUID menuId) {
        return new GuiClientMenuAddress(menuId);
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(menuId);
    }

    @Override
    public LabelType<?> getType() {
        return MenuAddressTypes.CLIENT;
    }
} 