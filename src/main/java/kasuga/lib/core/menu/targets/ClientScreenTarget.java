package kasuga.lib.core.menu.targets;

import kasuga.lib.core.client.frontend.gui.GuiInstance;
import kasuga.lib.core.client.frontend.gui.GuiScreen;
import kasuga.lib.core.menu.GuiMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ClientScreenTarget {
    private GuiInstance guiInstance;

    public ClientScreenTarget(GuiInstance guiInstance) {
        this.guiInstance = guiInstance;
    }

    public void openScreen(){
        Screen screen = new GuiScreen(this.guiInstance);
        Minecraft.getInstance().setScreen(screen);
    }

    public static void openScreen(GuiMenu menu){
        menu.getBinding().apply(Target.SCREEN).openScreen();
    }
}
