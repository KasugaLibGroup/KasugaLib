package kasuga.lib.core.channel.address;

import kasuga.lib.core.channel.network.NetworkSeriaizableType;
import kasuga.lib.core.channel.network.NetworkSerializable;

public abstract class Label implements NetworkSerializable {
    private final NetworkSeriaizableType<?> type;

    protected Label(NetworkSeriaizableType<?> type) {
        this.type = type;
    }
    @Override
    public NetworkSeriaizableType<?> getType() {
        return type;
    }
}
