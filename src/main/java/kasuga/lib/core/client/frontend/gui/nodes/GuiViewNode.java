package kasuga.lib.core.client.frontend.gui.nodes;

import com.caoccao.javet.annotations.V8Convert;
import kasuga.lib.core.client.frontend.gui.GuiContext;
import kasuga.lib.core.client.frontend.rendering.RenderContext;

@V8Convert()
public class GuiViewNode extends GuiDomNode{
    GuiViewNode(GuiContext context) {
        super(context);
    }

    @Override
    public void render(Object source,RenderContext context) {
        super.render(source, context);
    }
}
