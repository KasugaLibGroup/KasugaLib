package kasuga.lib.example_env.block.gui;

import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelHandle;
import kasuga.lib.core.menu.base.GuiBinding;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.base.GuiMenuType;
import kasuga.lib.core.menu.javascript.JavascriptMenu;
import kasuga.lib.core.menu.targets.Target;
import kasuga.lib.core.menu.targets.WorldRendererTarget;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

public class GuiExampleMenu extends JavascriptMenu {
    public GuiExampleMenu() {
        super(AllExampleElements.MENU_EXAMPLE);
    }

    @Override
    protected GuiBinding createBinding(UUID id) {
        return new GuiBinding(id).execute(ResourceLocation.tryParse("kuayue:lkj_2000")).with(Target.SCREEN);
     }

    @Override
    protected ResourceLocation getServerScriptLocation() {
        return ResourceLocation.tryParse("kuayue:lkj_2000");
    }

    @Override
    protected void createGuiInstance() {
        super.createGuiInstance();
        WorldRendererTarget.attach(this);
    }

    @Override
    protected void closeGuiInstance() {
        WorldRendererTarget.detach(this);
        super.closeGuiInstance();
    }

    public void setBlockEntity(GuiExampleBlockEntity blockEntity){
        if(hasProvide("example"))
            return;
        provide("example", new GuiExampleBlockApi(blockEntity));
    }
}
