package kasuga.lib.core.sync;

import kasuga.lib.core.channel.peer.ChannelHandle;
import kasuga.lib.core.channel.peer.ChannelHandler;
import kasuga.lib.core.channel.peer.ChannelSocket;
import kasuga.lib.core.sync.DataSynchronizer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.Lazy;

public class ChannelDataSynchronizer extends DataSynchronizer {
    public ChannelDataSynchronizer() {}

    Lazy<ChannelHandler> handler = Lazy.of(()->{
       return new ChannelHandler() {
        @Override
        public void onChannelMessage(ChannelHandle channel, CompoundTag payload) {
            handleRemoteUpdate(channel, payload);
        }
        
        @Override
        public void onChannelClose(ChannelHandle channel) {
            removeRemote(channel);
        }
        
        @Override
        public void onChannelEstabilished(ChannelHandle channel) {
            addRemote(channel, channel::sendMessage);
        }
       };
    });

    public ChannelHandler getHandler(){
        return handler.get();
    }

    @Override
    public void removeRemote(Object object) {
        super.removeRemote(object);
    }
} 