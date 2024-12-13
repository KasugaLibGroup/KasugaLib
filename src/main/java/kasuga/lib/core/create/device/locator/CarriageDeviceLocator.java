package kasuga.lib.core.create.device.locator;

import kasuga.lib.core.channel.network.NetworkSeriaizableType;
import kasuga.lib.core.channel.network.NetworkSerializable;
import net.minecraft.network.FriendlyByteBuf;

public class CarriageDeviceLocator implements NetworkSerializable {
    @Override
    public void write(FriendlyByteBuf byteBuf) {

    }

    @Override
    public NetworkSeriaizableType<?> getType() {
        return null;
    }
}
