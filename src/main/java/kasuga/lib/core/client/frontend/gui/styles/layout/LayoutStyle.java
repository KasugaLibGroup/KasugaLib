package kasuga.lib.core.client.frontend.gui.styles.layout;

import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.gui.layout.LayoutNodeContext;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.gui.styles.LayoutApplierRegistry;

public abstract class LayoutStyle<P> extends Style<P, StyleTarget> {
    @Override
    public StyleTarget getTarget() {
        return LAYOUT_NODE.create((node)->{
            LayoutApplierRegistry.getInstance().apply(node, this);
        });
    }

    public static final StyleTarget.StyleTargetType<LayoutNodeContext<?>> LAYOUT_NODE = new StyleTarget.StyleTargetType<>(
            (o) -> o instanceof LayoutNodeContext<?>,
            (o) -> (LayoutNodeContext<?>) o
    );
}
