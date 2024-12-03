package kasuga.lib.core.menu.locator;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class AbstractChunkBasedLocator extends MenuLocator implements IChunkBasedLocator {
    protected Level level;
    protected ResourceKey<Level> levelResourceKey;
    protected final ChunkPos chunkPos;

    protected AbstractChunkBasedLocator(MenuLocatorType<?> type, ChunkPos chunkPos) {
        super(type);
        this.chunkPos = chunkPos;
    }

    protected AbstractChunkBasedLocator(MenuLocatorType<?> type, FriendlyByteBuf byteBuf) {
        super(type);
        this.chunkPos = byteBuf.readChunkPos();
        this.levelResourceKey = byteBuf.readResourceKey(Registry.DIMENSION_REGISTRY);
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeChunkPos(chunkPos);
        byteBuf.writeResourceKey(levelResourceKey);
    }

    public void withLevel(Level level) {
        if (this.levelResourceKey != null) {
            return;
        }
        this.level = level;
        this.levelResourceKey = level.dimension();
    }

    @Override
    public void enable(LocatedMenuManager manager) {
        super.enable(manager);
        this.listen();
        if (!level.hasChunk(chunkPos.x, chunkPos.z)) {
            return;
        }
        this.broadcastEnable();
    }

    protected void broadcastEnable() {
        List<ServerPlayer> players =
                ((ServerChunkCache) level
                        .getChunkSource()
                )
                        .chunkMap
                        .getPlayers(chunkPos, false);
        if (players == null)
            return;

        for (ServerPlayer player : players) {
            sendUpTo(player.connection.getConnection());
        }
    }

    protected void listen() {
        ServerChunkMenuLocatorManager.register(this);
    }

    protected void unlisten() {
        ServerChunkMenuLocatorManager.unregister(this);
    }

    @Override
    public void disable(LocatedMenuManager manager) {
        this.broadcastDisable();
        super.disable(manager);
        this.unlisten();
    }

    @Override
    public ChunkPos getPosition() {
        return chunkPos;
    }
} 