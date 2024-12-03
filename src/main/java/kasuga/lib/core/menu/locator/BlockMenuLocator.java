package kasuga.lib.core.menu.locator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class BlockMenuLocator extends AbstractChunkBasedLocator {
    private final BlockPos blockPos;

    public BlockMenuLocator(BlockPos blockPos) {
        super(MenuLocatorTypes.BLOCK, new ChunkPos(blockPos));
        this.blockPos = blockPos;
    }

    public BlockMenuLocator(FriendlyByteBuf byteBuf) {
        super(MenuLocatorTypes.BLOCK, byteBuf);
        this.blockPos = byteBuf.readBlockPos();
    }

    public BlockMenuLocator(Level level, BlockPos blockPos) {
        this(blockPos);
        this.withLevel(level);
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        super.write(byteBuf);
        byteBuf.writeBlockPos(blockPos);
    }

    public static BlockMenuLocator of(Level level, BlockPos blockPos) {
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
