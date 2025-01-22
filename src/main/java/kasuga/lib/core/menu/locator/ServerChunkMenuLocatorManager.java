package kasuga.lib.core.menu.locator;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.world.ChunkWatchEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerChunkMenuLocatorManager {
    public static HashMap<ChunkPos, List<IChunkBasedLocator>> chunks = new HashMap<>();
    public static HashMap<ChunkPos, List<ServerPlayer>> players = new HashMap<>();

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
        players.computeIfAbsent(watchEvent.getPos(), k -> new ArrayList<>()).add(watchEvent.getPlayer());
        notifyLoad(watchEvent.getPos(), watchEvent.getPlayer().connection.getConnection());
    }

    public static void onUnWatch(ChunkWatchEvent.UnWatch unwatchEvent){
        if(players.containsKey(unwatchEvent.getPos())){
            List<ServerPlayer> playerList = players.get(unwatchEvent.getPos());
            playerList.remove(unwatchEvent.getPlayer());
            if(playerList.isEmpty()){
                players.remove(unwatchEvent.getPos());
            }
        }
        notifyUnload(unwatchEvent.getPos(), unwatchEvent.getPlayer().connection.getConnection());
    }

    public static void transfer(IChunkBasedLocator menuLocator, ChunkPos oldChunk, ChunkPos newChunk) {
        chunks.computeIfAbsent(newChunk, k -> new ArrayList<>()).add(menuLocator);

        if(chunks.containsKey(oldChunk)){
            chunks.get(oldChunk).remove(menuLocator);
        }

        List<ServerPlayer> oldPlayers = players.get(oldChunk);
        List<ServerPlayer> newPlayers = players.get(newChunk);

        if(oldPlayers != null)
            oldPlayers
                    .stream()
                    .filter(player -> newPlayers == null ? true : !newPlayers.contains(player))
                    .forEach(player -> menuLocator.sendDownTo(player.connection.getConnection()));

        if(newPlayers != null)
            newPlayers
                    .stream()
                    .filter(player -> oldPlayers == null ? true :  !oldPlayers.contains(player))
                    .forEach(player -> menuLocator.sendUpTo(player.connection.getConnection()));
    }
}
