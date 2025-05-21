package kasuga.lib.core.client.frontend.gui.layout.vanilla;

import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.common.layouting.LayoutCache;
import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.common.style.StyleList;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.layout.EdgeSize2D;
import kasuga.lib.core.client.frontend.gui.layout.LayoutNodeContext;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;

import java.util.ArrayList;

public class VanillaLayoutNode implements LayoutNode {
    private final VanillaLayoutEngine layoutEngine;
    private final GuiDomNode node;
    private final Object source;
    private final ArrayList<VanillaLayoutNode> children = new ArrayList<>();
    private final LayoutCache cache = new LayoutCache();
    private LayoutBox engineCoordinate = LayoutBox.ZERO;

    public VanillaLayoutNode(VanillaLayoutEngine layoutEngine, GuiDomNode node, Object source) {
        this.layoutEngine = layoutEngine;
        this.node = node;
        this.source = source;
    }

    @Override
    public boolean applyChanges() {
        StyleList<StyleTarget> styles = node.getStyle();

        if(!styles.hasNewStyle(this))
            return false;

        styles.forEachCacheStyle((style)->{
            style.getTarget().attemptApply(new LayoutNodeContext<>(
                    layoutEngine,
                    node,
                    this
            ));
        });

        styles.resetNewStyle(this);

        cache.setEngineCoordinate(engineCoordinate);

        cache.screenCoordinate.clear();

        return true;
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public void calculate() {}

    @Override
    public LayoutBox getPosition() {
        return cache.screenCoordinate.get();
    }

    @Override
    public void markDirty() {}

    @Override
    public void addChild(int index, LayoutNode $node) {
        if(!($node instanceof VanillaLayoutNode node))
            throw new IllegalArgumentException("Node is not a VanillaLayoutNode");
        if(index < 0 || index > children.size())
            return;
        children.add(index, node);
        node.cache.setTracking(this.cache);
    }

    @Override
    public void removeChild(int index) {
        if(index < 0 || index >= children.size())
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        VanillaLayoutNode node = children.remove(index);
        if(node != null)
            node.cache.setTracking(null);
    }

    @Override
    public void removeChild(LayoutNode node) {
        removeChild(children.indexOf(node));
    }

    @Override
    public void close() {}

    @Override
    public EdgeSize2D getBorder() {
        return EdgeSize2D.ZERO;
    }

    @Override
    public LayoutBox getRelative() {
        return cache.engineCoordinate;
    }

    public void setEngineCoordinate(LayoutBox engineCoordinate) {
        this.engineCoordinate = engineCoordinate;
    }

    public void setEngineCoordinateLeft(float size) {
        this.engineCoordinate = LayoutBox.of(
                size,
                this.engineCoordinate.y,
                this.engineCoordinate.width,
                this.engineCoordinate.height
        );
    }

    public void setEngineCoordinateTop(float size) {
        this.engineCoordinate = LayoutBox.of(
                this.engineCoordinate.x,
                size,
                this.engineCoordinate.width,
                this.engineCoordinate.height
        );
    }

    public void setEngineCoordinateWidth(float size) {
        this.engineCoordinate = LayoutBox.of(
                this.engineCoordinate.x,
                this.engineCoordinate.y,
                size,
                this.engineCoordinate.height
        );
    }

    public void setEngineCoordinateHeight(float size) {
        this.engineCoordinate = LayoutBox.of(
                this.engineCoordinate.x,
                this.engineCoordinate.y,
                this.engineCoordinate.width,
                size
        );
    }
}
