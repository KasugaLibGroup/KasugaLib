package kasuga.lib.core.client.frontend.gui.nodes;

import kasuga.lib.core.client.frontend.common.layouting.LayoutContextManager;
import kasuga.lib.core.client.frontend.common.style.StyleAttributeProxy;
import kasuga.lib.core.client.frontend.common.style.StyleList;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.styles.GuiStyleRegistry;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaMeasureFunction;

public class GuiDomNode extends DomNode {
    StyleList<StyleTarget> styleList = new StyleList<>(GuiStyleRegistry.REGISTRY.get());

    GuiDomNode(){
        super();
        attributes.registerProxy("style",new StyleAttributeProxy(styleList));
    }

    public StyleList<StyleTarget> getStyle() {
        return styleList;
    }

    LayoutContextManager layoutManager = new LayoutContextManager(this);

    public YogaMeasureFunction measure(){
        return null;
    }

    public LayoutContextManager getLayoutManager() {
        return layoutManager;
    }
}
