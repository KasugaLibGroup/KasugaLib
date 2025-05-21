package kasuga.lib.core.client.frontend.gui.layout.vanilla;

import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.GuiEngine;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngineType;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;

public class VanillaLayoutEngine implements LayoutEngine<VanillaLayoutNode, GuiDomNode> {
    @Override
    public void init() {
        VanillaLayoutHandlers.init();
    }

    @Override
    public VanillaLayoutNode createNode(GuiDomNode node, Object source) {
        return new VanillaLayoutNode(this, node, source);
    }

    @Override
    public LayoutEngineType<?> getType() {
        return LayoutEngines.VANILLA;
    }
}
