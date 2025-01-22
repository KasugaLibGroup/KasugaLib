package kasuga.lib.core.menu.api;

import kasuga.lib.core.channel.peer.ChannelHandle;
import kasuga.lib.core.javascript.CompoundTagWrapper;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;

import java.util.Objects;
import java.util.WeakHashMap;

public class ChannelHandlerProxy {
    private static final WeakHashMap<ChannelHandle, ChannelHandlerProxy> CACHE = new WeakHashMap<>();
    private final ChannelHandle channel;

    private ChannelHandlerProxy(ChannelHandle channel) {
        this.channel = channel;
    }

    public static ChannelHandlerProxy wrap(ChannelHandle channel) {
        return CACHE.computeIfAbsent(channel, ChannelHandlerProxy::new);
    }

    @HostAccess.Export
    public void sendMessage(CompoundTagWrapper message){
        channel.sendMessage(message.getNativeTag());
    }

    @HostAccess.Export
    public void close(){
        channel.close();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ChannelHandlerProxy)) return false;
        ChannelHandlerProxy that = (ChannelHandlerProxy) object;
        return Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel);
    }
}
