package kasuga.lib.core.menu.api;

import kasuga.lib.core.channel.peer.ChannelHandle;
import kasuga.lib.core.javascript.CompoundTagWrapper;
import kasuga.lib.core.javascript.engine.HostAccess;

public class ChannelHandlerProxy {
    private final ChannelHandle channel;

    public ChannelHandlerProxy(ChannelHandle channel) {
        this.channel = channel;
    }

    public static ChannelHandlerProxy wrap(ChannelHandle channel) {
        return new ChannelHandlerProxy(channel);
    }

    @HostAccess.Export
    public void sendMessage(CompoundTagWrapper message){
        channel.sendMessage(message.getNativeTag());
    }

    @HostAccess.Export
    public void close(){
        channel.close();
    }
}
