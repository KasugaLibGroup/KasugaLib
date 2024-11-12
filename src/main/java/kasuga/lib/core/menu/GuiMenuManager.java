package kasuga.lib.core.menu;

import kasuga.lib.core.menu.base.GuiMenuRegistry;
import kasuga.lib.core.menu.network.GuiMenuNetworking;
import kasuga.lib.core.menu.targets.TargetsClient;

public class GuiMenuManager {
    GuiMenuRegistry registry = new GuiMenuRegistry();

    public void init(){
        TargetsClient.register();
        GuiMenuNetworking.invoke();
    }
}
