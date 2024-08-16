package kasuga.lib.core.client.frontend.common.layouting;

import kasuga.lib.core.client.frontend.dom.nodes.DomNode;

public interface LayoutEngine<T extends LayoutNode,N extends DomNode> {
    public T createNode(N node,Object source);
}
