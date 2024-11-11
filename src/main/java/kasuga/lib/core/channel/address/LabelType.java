package kasuga.lib.core.channel.address;

import kasuga.lib.core.channel.network.NetworkSeriaizableType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public class LabelType<T extends Label> extends NetworkSeriaizableType<T> {
    public LabelType(Function<FriendlyByteBuf, T> deserializer) {
        super(deserializer);
    }
}
