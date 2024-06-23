package kasuga.lib.core.client.frontend.gui.layout.yoga;

import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.common.layouting.LayoutCache;
import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.common.style.StyleList;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.gui.layout.EdgeSize2D;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaMeasureFunction;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomRoot;

import java.util.ArrayList;
import java.util.Optional;

public class YogaLayoutNode implements LayoutNode {
    private final GuiDomNode domNode;
    private final YogaNode node;
    private final Object source;
    private final LayoutCache cache;
    protected YogaLayoutNode parent;
    protected ArrayList<YogaLayoutNode> children = new ArrayList<>();

    private EdgeSize2D padding = EdgeSize2D.ZERO;
    private EdgeSize2D margin = EdgeSize2D.ZERO;
    private EdgeSize2D border = EdgeSize2D.ZERO;

    public YogaLayoutNode(GuiDomNode node, Object source) {
        this.domNode = node;
        this.node = YogaNode.create();
        this.source = source;
        cache = new LayoutCache();
        regenerateMeasure();
    }

    @Override
    public boolean applyChanges() {
        StyleList<StyleTarget> styles = domNode.getStyle();

        if(!styles.hasNewStyle(this))
            return false;

        styles.forEachCacheStyle((style)->{
            style.getTarget().attemptApply(this.node);
        });

        styles.resetNewStyle(this);
        return true;
    }

    @Override
    public boolean update() {
        if(node.hasNewLayout()){
            node.visited();
            LayoutBox layout = LayoutBox.of(node.getLayoutLeft(), node.getLayoutTop(), node.getLayoutWidth(), node.getLayoutHeight());
            cache.setEngineCoordinate(layout);
            padding = EdgeSize2D.fromLambda(node::getLayoutPadding);
            margin = EdgeSize2D.fromLambda(node::getLayoutMargin);
            border = EdgeSize2D.fromLambda(node::getLayoutBorder);
            return true;
        }
        return false;
    }

    @Override
    public void calculate() {
        if(domNode instanceof GuiDomRoot root){
            LayoutBox size = root.getDomContext().getSourceInfo(source).size;
            node.calculateLayout(size.getWidth(), size.getHeight());

        }
    }

    @Override
    public LayoutBox getPosition() {
        return cache.screenCoordinate.get();
    }

    public EdgeSize2D getPadding() {
        return padding;
    }

    public EdgeSize2D getMargin() {
        return margin;
    }

    public EdgeSize2D getBorder(){
        return border;
    }

    @Override
    public void markDirty() {
        cache.screenCoordinate.clear();
        regenerateMeasure();
        node.dirty();
    }

    protected void regenerateMeasure(){
        if(domNode instanceof MayMeasurable measurable){
            Optional<YogaMeasureFunction> measure = measurable.measure();
            if(measure.isPresent())
                node.setMeasureFunction(measure.get());
            else
                node.removeMeasureFunction();
        }
    }

    @Override
    public void addChild(int index, LayoutNode node) {
        if(node instanceof YogaLayoutNode yogaNode) {
            if(yogaNode.parent != null)
                return;
            this.node.addChildAt(index,yogaNode.node);
            this.children.add(index,yogaNode);
            yogaNode.cache.setTracking(this.cache);
            yogaNode.parent = this;
        }else{
            throw new IllegalStateException();
        }
    }

    @Override
    public void removeChild(int index) {
        this.node.removeChildAt(index);
        this.children.remove(index);
    }

    @Override
    public void removeChild(LayoutNode node) {
        if(node instanceof YogaLayoutNode yogaNode) {
            this.node.removeChild(yogaNode.node);
            yogaNode.cache.setTracking(null);
            yogaNode.parent = null;
        }else{
            throw new IllegalStateException();
        }
    }

    @Override
    public void close() {
        for (YogaLayoutNode child : children) {
            child.close();
        }
        if(this.parent != null){
            this.parent.removeChild(this);
        }
        node.close();
    }
}
