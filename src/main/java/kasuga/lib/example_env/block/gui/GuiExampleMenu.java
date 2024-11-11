package kasuga.lib.example_env.block.gui;

import kasuga.lib.core.menu.base.GuiBinding;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.base.GuiMenuType;
import kasuga.lib.example_env.AllExampleElements;

public class GuiExampleMenu extends GuiMenu {
    public GuiExampleMenu(GuiMenuType<?> type, GuiBinding binding) {
        super(type, binding);
    }

    public GuiExampleMenu(GuiBinding binding) {
        super(AllExampleElements.MENU_EXAMPLE, binding);
    }
}
