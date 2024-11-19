package kasuga.lib.core.menu.locator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class BlockMenuLocator extends MenuLocator implements IChunkBasedLocator {
    private Level level;
    private ResourceKey<Level> levelResourceKey;

    private final BlockPos blockPos;
    private final ChunkPos chunkPos;

    public BlockMenuLocator(BlockPos blockPos) {
        super(MenuLocatorTypes.CHUNK_MENU);
        this.blockPos = blockPos;
        this.chunkPos = new ChunkPos(blockPos);
    }

    public BlockMenuLocator(FriendlyByteBuf byteBuf){
        super(MenuLocatorTypes.CHUNK_MENU);
        this.blockPos = byteBuf.readBlockPos();
        this.chunkPos = new ChunkPos(byteBuf.readLong());
        this.levelResourceKey = byteBuf.readResourceKey(Registry.DIMENSION_REGISTRY);
    }

    public BlockMenuLocator(Level level, BlockPos blockPos) {
        this(blockPos);
        this.withLevel(level);
    }

    public void withLevel(Level level){
        if(this.levelResourceKey != null){
            return;
        }
        this.level = level;
        this.levelResourceKey = level.dimension();
    }

    @Override
    public void enable(LocatedMenuManager manager) {
        super.enable(manager);
        this.listen();
        if(!level.hasChunk(chunkPos.x, chunkPos.z)){
            return;
        }
        this.broadcastEnable();
    }

    private void broadcastEnable() {
        List<ServerPlayer> players =
                ((ServerChunkCache)level
                        .getChunkSource()
                )
                        .chunkMap
                        .getPlayers(chunkPos, false);
        if(players == null)
            return;

        for(ServerPlayer player : players){
            sendUpTo(player.connection.getConnection());
        }
    }


    private void listen() {
        ServerChunkMenuLocatorManager.register(this);
    }

    private void unlisten() {
        ServerChunkMenuLocatorManager.unregister(this);
    }

    @Override
    public void disable(LocatedMenuManager manager) {
        super.disable(manager);
        this.unlisten();
        this.broadcastDisable();
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeLong(blockPos.asLong());
        byteBuf.writeLong(chunkPos.toLong());
        byteBuf.writeResourceKey(levelResourceKey);
    }

    public ChunkPos getPosition() {
        return chunkPos;
    }

    public static BlockMenuLocator of(Level level, BlockPos blockPos){
        return new BlockMenuLocator(level, blockPos);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BlockMenuLocator that = (BlockMenuLocator) object;
        return Objects.equals(levelResourceKey, that.levelResourceKey) && Objects.equals(blockPos, that.blockPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(levelResourceKey, blockPos);
    }
}
