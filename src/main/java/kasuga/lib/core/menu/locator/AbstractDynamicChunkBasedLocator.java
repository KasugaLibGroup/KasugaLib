package kasuga.lib.core.menu.locator;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;

public abstract class AbstractDynamicChunkBasedLocator extends AbstractChunkBasedLocator{
    ChunkPos latestPos;

    protected AbstractDynamicChunkBasedLocator(MenuLocatorType<?> type, ChunkPos chunkPos) {
        super(type, chunkPos);
        latestPos = chunkPos;
    }

    protected AbstractDynamicChunkBasedLocator(MenuLocatorType<?> type, FriendlyByteBuf byteBuf) {
        super(type, byteBuf);
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        super.write(byteBuf);
    }

    @Override
    protected void listen() {
        ServerChunkMenuLocatorManager.register(this, latestPos);
    }

    @Override
    protected void unlisten() {
        ServerChunkMenuLocatorManager.unregister(this, latestPos);
    }

    protected void transfer(ChunkPos newChunk){
        if(this.latestPos == newChunk)
            return;
        ServerChunkMenuLocatorManager.transfer(this, latestPos, newChunk);
        latestPos = newChunk;
    }
}
