package kasuga.lib.core.client.frontend.gui;

import kasuga.lib.core.client.frontend.dom.nodes.NodeTypeRegistry;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.gui.styles.AllGuiStyles;
import kasuga.lib.core.client.frontend.gui.styles.GuiStyleRegistry;

public class GuiEngine {
    public final DOMPriorityRegistry domRegistry = new DOMPriorityRegistry();

    public final NodeTypeRegistry<GuiDomNode> nodeTypeRegistry = new NodeTypeRegistry<>();

    public final GuiStyleRegistry styleRegistry = new GuiStyleRegistry();

    public void init(){
        AllGuiStyles.register(styleRegistry);
    }
}
