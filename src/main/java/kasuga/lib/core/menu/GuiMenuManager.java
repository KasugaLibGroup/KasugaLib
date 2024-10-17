package kasuga.lib.core.menu;

import java.util.HashMap;
import java.util.UUID;

public class GuiMenuManager {
    private static HashMap<UUID, GuiMenu> SERVER_MENUS = new HashMap<>();

    private static HashMap<UUID, GuiMenu> CLIENT_MENUS = new HashMap<>();

    public static GuiMenu findMenuFromServer(UUID uuid){
        return SERVER_MENUS.get(uuid);
    }

    public static GuiMenu findMenuFromClient(UUID uuid){
        return CLIENT_MENUS.get(uuid);
    }
}
