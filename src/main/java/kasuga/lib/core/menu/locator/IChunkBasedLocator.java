package kasuga.lib.core.menu.locator;

import net.minecraft.network.Connection;
import net.minecraft.world.level.ChunkPos;

public interface IChunkBasedLocator {
    ChunkPos getPosition();
    void sendUpTo(Connection connection);
    void sendDownTo(Connection connection);
}
