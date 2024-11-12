package kasuga.lib.example_env.block.gui;

import kasuga.lib.core.menu.base.GuiBinding;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.base.GuiMenuType;
import kasuga.lib.core.menu.targets.Target;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class GuiExampleMenu extends GuiMenu {
    public GuiExampleMenu() {
        super(AllExampleElements.MENU_EXAMPLE);
    }

    @Override
    protected GuiBinding createBinding(UUID id) {
        return new GuiBinding(id).execute(ResourceLocation.tryParse("kasuga_lib:example")).with(Target.SCREEN);
     }
}
