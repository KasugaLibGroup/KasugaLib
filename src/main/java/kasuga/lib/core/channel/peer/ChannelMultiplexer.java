package kasuga.lib.core.channel.peer;

import net.minecraft.nbt.CompoundTag;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ChannelMultiplexer implements ChannelHandler {
    private final Map<Integer, ChannelHandler> handlers = new HashMap<>();
    private int nextIndex = 0;

    public int register(ChannelHandler handler) {
        int index = nextIndex++;
        handlers.put(index, handler);
        return index;
    }

    public ChannelMultiplexer with(Consumer<ChannelMultiplexer> consumer) {
        consumer.accept(this);
        return this;
    }

    @Override
    public void onChannelEstabilished(ChannelHandle channel) {
        handlers.values().forEach(handler -> 
            handler.onChannelEstabilished(channel));
    }

    @Override
    public void onChannelMessage(ChannelHandle channel, CompoundTag payload) {
        int targetIndex = payload.getInt("T");
        int sourceIndex = payload.getInt("S");
        
        CompoundTag actualPayload = payload.getCompound("P");
        
        ChannelHandler targetHandler = handlers.get(targetIndex);
        if (targetHandler != null) {
            targetHandler.onChannelMessage(channel, actualPayload);
        }
    }

    @Override
    public void onChannelClose(ChannelHandle channel) {
        handlers.values().forEach(handler -> 
            handler.onChannelClose(channel));
    }

    public void sendMessage(ChannelHandle channel, int sourceIndex, int targetIndex, CompoundTag payload) {
        CompoundTag wrappedPayload = new CompoundTag();
        wrappedPayload.putInt("S", sourceIndex);
        wrappedPayload.putInt("T", targetIndex);
        wrappedPayload.put("P", payload);
        
        channel.sendMessage(wrappedPayload);
    }
} 