package kasuga.lib.core.channel.peer;

import net.minecraft.nbt.CompoundTag;

public class ChannelPeerSocketServer implements ChannelSocket, ChannelHandle {
    private final Channel channel;
    private ChannelHandler handler;

    public ChannelPeerSocketServer(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void onMessage(CompoundTag message) {
        if (this.handler != null) {
            this.handler.onChannelMessage(this, message);
        }
    }

    @Override
    public void onEstablished() {
        if (this.handler != null) {
            this.handler.onChannelEstabilished(this);
        }
    }

    @Override
    public void onClose() {
        if (this.handler != null) {
            this.handler.onChannelClose(this);
        }
    }

    @Override
    public void setChannel(Channel channel) {}

    @Override
    public void sendMessage(CompoundTag message) {
        channel.sendMessage(this, message);
    }

    @Override
    public void close() {
        channel.close();
    }

    public void setHandler(ChannelHandler handler) {
        this.handler = handler;
    }
}
