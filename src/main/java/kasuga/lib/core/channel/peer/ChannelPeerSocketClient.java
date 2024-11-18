package kasuga.lib.core.channel.peer;

import net.minecraft.nbt.CompoundTag;

public class ChannelPeerSocketClient implements ChannelSocket, ChannelHandle {
    private Channel channel;
    private ChannelHandler handler;
    ChannelPeerSocketClient(){}

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void sendMessage(CompoundTag message){
        channel.sendMessage(this, message);
    }


    @Override
    public void close(){
        channel.close();
    }

    @Override
    public void onMessage(CompoundTag message) {
        if(this.handler != null)
            this.handler.onChannelMessage(this, message);
    }

    @Override
    public void onEstablished() {
        if(this.handler != null)
            this.handler.onChannelEstabilished(this);
    }

    @Override
    public void onClose() {
        if(this.handler != null)
            this.handler.onChannelClose(this);
    }

    public void setHandler(ChannelHandler handler) {
        this.handler = handler;
    }
}
