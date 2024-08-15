package kasuga.lib.core.client.frontend.gui.layout.yoga;

import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;

public class YogaLayoutEngine implements LayoutEngine<YogaLayoutNode,GuiDomNode> {
    @Override
    public YogaLayoutNode createNode(GuiDomNode node, Object source) {
        return new YogaLayoutNode(node,source);
    }
}
