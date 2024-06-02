package kasuga.lib.core.client.frontend.gui.layout.yoga;

import kasuga.lib.core.client.frontend.common.layouting.EngineLayoutContext;
import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.common.layouting.LayoutContext;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaMeasureFunction;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;

public class YogaLayoutEngineContext implements EngineLayoutContext {

    private final GuiDomNode target;
    private final LayoutContext context;
    YogaNode node;

    YogaLayoutEngineContext(LayoutContext context, GuiDomNode target){
        this.context = context;
        node = YogaNode.create();
        YogaMeasureFunction measureFunction = target.measure();
        if(measureFunction != null){
            node.setMeasureFunction(measureFunction);
        }
        this.target = target;
    }

    @Override
    public void close() {
        node.close();
    }


    boolean newLayout = true;
    @Override
    public boolean hasNewLayout() {
        return newLayout || node.hasNewLayout();
    }

    public void resetNewLayoutFlag(){
        newLayout = false;
        node.visited();
    }

    public boolean shouldApply(){
        return target.getStyle().hasNewStyle(this);
    }


    public void setNewLayout(){
        newLayout = true;
        LayoutContext parent = context.getParent();
        if(parent != null && parent.getEngineLayoutContext() instanceof YogaLayoutEngineContext parentEC){
            parentEC.setNewLayout();
        }
    }

    public LayoutBox getLayoutResult(){
        resetNewLayoutFlag();
        return LayoutBox.of(node.getLayoutLeft(),node.getLayoutTop(),node.getLayoutWidth(),node.getLayoutHeight());
    }

    private void applyStyles() {
        for (Style<?, StyleTarget> style : target.getStyle().getCachedStyles()) {
            style.getTarget().attemptApply(node);
        }
    }

    public void apply() {
        if(this.shouldApply()){
            this.applyStyles();
            target.getStyle().resetNewStyle(this);
        }
    }

    public void dispatchYogaLayout(float width,float height){
        node.calculateLayout(width, height);
    }
}
