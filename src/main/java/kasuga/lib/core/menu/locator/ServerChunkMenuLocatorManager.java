package kasuga.lib.core.menu.locator;

import net.minecraft.network.Connection;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.level.ChunkWatchEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerChunkMenuLocatorManager {
    public static HashMap<ChunkPos, List<BlockMenuLocator>> chunks = new HashMap<>();

    public static void register(BlockMenuLocator menuLocator) {
        ChunkPos pos = menuLocator.getPosition();
        chunks.computeIfAbsent(pos, k -> new ArrayList<>()).add(menuLocator);
    }

    public static void unregister(BlockMenuLocator menuLocator) {
        ChunkPos pos = menuLocator.getPosition();
        if (chunks.containsKey(pos)) {
            List<BlockMenuLocator> locators = chunks.get(pos);
            locators.remove(menuLocator);
            if (locators.isEmpty()) {
                chunks.remove(pos);
            }
        }
    }

    public static void notifyLoad(ChunkPos position, Connection connection) {
        if (chunks.containsKey(position)) {
            chunks.get(position).forEach(locator -> locator.sendUpTo(connection));
        }
    }

    public static void notifyUnload(ChunkPos position, Connection connection) {
        if (chunks.containsKey(position)) {
            chunks.get(position).forEach(locator -> locator.sendDownTo(connection));
        }
    }

    public static void onWatch(ChunkWatchEvent.Watch watchEvent){
        notifyLoad(watchEvent.getPos(), watchEvent.getPlayer().connection.getConnection());
    }

    public static void onUnWatch(ChunkWatchEvent.UnWatch unwatchEvent){
        notifyUnload(unwatchEvent.getPos(), unwatchEvent.getPlayer().connection.getConnection());
    }
}
