package kasuga.lib.core.menu;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

public class GuiMenuManager {
    private static HashMap<UUID, GuiMenu> SERVER_MENUS = new HashMap<>();

    private static HashMap<UUID, GuiMenu> CLIENT_MENUS = new HashMap<>();

    public static GuiMenu createMenuOrExisted(boolean isClient,UUID uuid, Supplier<GuiMenu> constructor){
        if(isClient)
            return CLIENT_MENUS.computeIfAbsent(uuid, (v)->constructor.get());
        else
            return SERVER_MENUS.computeIfAbsent(uuid, (v)->constructor.get());
    }

    public static GuiMenu findMenuFromServer(UUID uuid){
        return SERVER_MENUS.get(uuid);
    }

    public static GuiMenu findMenuFromClient(UUID uuid){
        return CLIENT_MENUS.get(uuid);
    }

    public static void listenFromClient(GuiMenu menu){
        CLIENT_MENUS.put(menu.getID(), menu);
    }

    public static void listenFromServer(GuiMenu menu){
        SERVER_MENUS.put(menu.getID(), menu);
    }



    public static void unlistenFromClient(GuiMenu menu){
        CLIENT_MENUS.remove(menu.getID(), menu);
    }

    public static void unlistenFromServer(GuiMenu menu){
        SERVER_MENUS.remove(menu.getID(), menu);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        SERVER_MENUS.forEach((id,menu)->menu.closeByPlayer(player));
    }
}
