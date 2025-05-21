package kasuga.lib.core.client.frontend.common.layouting;

import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngineType;

public interface LayoutEngine<T extends LayoutNode,N extends DomNode> {
    public void init();
    public T createNode(N node,Object source);
    public LayoutEngineType<?> getType();
}
