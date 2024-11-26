package kasuga.lib.core.menu.api;

import kasuga.lib.core.channel.address.ConnectionInfo;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelHandle;
import kasuga.lib.core.javascript.engine.HostAccess;
import kasuga.lib.core.util.data_type.Pair;

import java.util.Objects;
import java.util.WeakHashMap;

public class ChannelProxy {

    private static final WeakHashMap<Pair<Channel, Boolean>, ChannelProxy> CACHE = new WeakHashMap<>();
    private final Channel channel;
    private final boolean isClient;

    private ChannelProxy(Channel channel, boolean isClient) {
        this.channel = channel;
        this.isClient = isClient;
    }

    public static ChannelProxy wrap(Channel channel, boolean isClient) {
        return CACHE.computeIfAbsent(Pair.of(channel, isClient), c -> new ChannelProxy(c.getFirst(),c.getSecond()));
    }

    @HostAccess.Export
    public ConnectionInfo source(){
        return isClient ? channel.destination() : channel.source();
    }

    @HostAccess.Export
    public ConnectionInfo destination(){
        return isClient ? channel.source() : channel.destination();
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ChannelProxy that = (ChannelProxy) object;
        return isClient == that.isClient && Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, isClient);
    }
}
