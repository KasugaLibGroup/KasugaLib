package kasuga.lib.core.client.frontend.gui.nodes;

import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.GuiContext;
import kasuga.lib.core.client.frontend.gui.SourceInfo;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;

import java.util.HashMap;

public class GuiDomRoot extends GuiDomNode {
    public GuiDomRoot(GuiContext context) {
        super(context);
        this.styles.decode("height:100%;width:100%");
    }
}
