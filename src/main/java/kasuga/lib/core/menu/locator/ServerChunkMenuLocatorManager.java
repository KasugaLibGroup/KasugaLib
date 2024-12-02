package kasuga.lib.core.menu.locator;

import net.minecraft.network.Connection;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.level.ChunkWatchEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerChunkMenuLocatorManager {
    public static HashMap<ChunkPos, List<IChunkBasedLocator>> chunks = new HashMap<>();

    public static void register(IChunkBasedLocator menuLocator, ChunkPos position) {
        chunks.computeIfAbsent(position, k -> new ArrayList<>()).add(menuLocator);
    }

    public static void register(IChunkBasedLocator menuLocator) {
        register(menuLocator, menuLocator.getPosition());
    }

    public static void unregister(IChunkBasedLocator menuLocator, ChunkPos position) {
        if (chunks.containsKey(position)) {
            List<IChunkBasedLocator> locators = chunks.get(position);
            locators.remove(menuLocator);
            if (locators.isEmpty()) {
                chunks.remove(position);
            }
        }
    }

    public static void unregister(IChunkBasedLocator menuLocator) {
        unregister(menuLocator, menuLocator.getPosition());
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
