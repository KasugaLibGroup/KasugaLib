package kasuga.lib.core.menu.network;

import kasuga.lib.core.channel.address.Label;
import kasuga.lib.core.channel.address.LabelType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class GuiServerMenuAddress extends Label {
    private final UUID menuId;
    
    GuiServerMenuAddress(FriendlyByteBuf buf) {
        super(MenuAddressTypes.SERVER);
        this.menuId = buf.readUUID();
    }
    
    private GuiServerMenuAddress(UUID menuId) {
        super(MenuAddressTypes.SERVER);
        this.menuId = menuId;
    }
    
    public static GuiServerMenuAddress of(UUID menuId) {
        return new GuiServerMenuAddress(menuId);
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(menuId);
    }

    @Override
    public LabelType<?> getType() {
        return MenuAddressTypes.SERVER;
    }
} 