package kasuga.lib.core.client.gui.components;

import kasuga.lib.core.client.gui.context.RenderContext;

public interface GuiComponent {
    void dispatchRender(RenderContext context);
}
