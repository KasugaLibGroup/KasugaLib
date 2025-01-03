package kasuga.lib.core.menu.api;

import com.caoccao.javet.annotations.V8Convert;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;
import kasuga.lib.core.menu.base.GuiMenu;

@V8Convert()
public class GuiMenuOperateProxy {
    private final GuiMenu guiMenu;

    public GuiMenuOperateProxy(GuiMenu guiMenu) {
        this.guiMenu = guiMenu;
    }

    public static GuiMenuOperateProxy wrap(GuiMenu guiMenu) {
        return new GuiMenuOperateProxy(guiMenu);
    }

    @HostAccess.Export
    public ChannelHandlerProxy getChannel(){
        return ChannelHandlerProxy.wrap(guiMenu.getChannel());
    }
}
