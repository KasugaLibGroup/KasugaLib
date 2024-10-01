package kasuga.lib.core.client.frontend.gui.nodes;

import kasuga.lib.core.client.frontend.dom.nodes.NodeTypeRegistry;

public class AllGuiNodes {
    public static void register(NodeTypeRegistry<GuiDomNode> nodeTypeRegistry) {
        nodeTypeRegistry.register("view",GuiViewNode::new);
        nodeTypeRegistry.register("text",GuiTextNode::new);
    }
}
