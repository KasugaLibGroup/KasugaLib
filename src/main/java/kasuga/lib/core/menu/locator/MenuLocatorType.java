package kasuga.lib.core.menu.locator;

import kasuga.lib.core.channel.network.NetworkSeriaizableType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public class MenuLocatorType<T extends MenuLocator> extends NetworkSeriaizableType<T> {
    public MenuLocatorType(Function<FriendlyByteBuf, T> deserializer) {
        super(deserializer);
    }
}
