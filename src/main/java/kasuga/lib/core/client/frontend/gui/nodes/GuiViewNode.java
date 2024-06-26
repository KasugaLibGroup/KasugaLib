package kasuga.lib.core.client.frontend.gui.nodes;

import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.gui.GuiContext;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;

public class GuiViewNode extends GuiDomNode{
    GuiViewNode(GuiContext context) {
        super(context);
    }

    @Override
    public void render(Object source,RenderContext context) {
        super.render(source, context);
    }
}
