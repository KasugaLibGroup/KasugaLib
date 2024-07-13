package kasuga.lib.core.client.frontend.gui.nodes;

import kasuga.lib.core.client.frontend.gui.GuiContext;

public class GuiDomRoot extends GuiDomNode {
    public GuiDomRoot(GuiContext context) {
        super(context);
        this.styles.decode("height:100%;width:100%");
    }
}
