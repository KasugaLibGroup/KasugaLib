package kasuga.lib.core.client.frontend.gui.layout;

import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.gui.GuiEngine;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;

import java.util.Optional;

public record LayoutNodeContext<T extends LayoutNode>(
        LayoutEngine engine,
        GuiDomNode domNode,
        T layoutNode
) {
    public <V> Optional<V> cast(Class<V> classType){
        if(classType.isAssignableFrom(layoutNode.getClass())) {
            return Optional.of(classType.cast(layoutNode));
        } else {
            return Optional.empty();
        }
    }
}
