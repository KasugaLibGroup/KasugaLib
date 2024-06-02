package kasuga.lib.core.client.frontend.gui.layout.yoga;

import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.common.layouting.LayoutCache;
import kasuga.lib.core.client.frontend.common.layouting.LayoutContext;
import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomRoot;

public class YogaLayoutEngine implements LayoutEngine<YogaLayoutEngineContext> {

    @Override
    public void apply(LayoutContext context) {
        YogaLayoutEngineContext engineContext = (YogaLayoutEngineContext) context.getEngineLayoutContext();
        engineContext.apply();
    }

    @Override
    public void layout(LayoutContext context) {
        YogaLayoutEngineContext engineContext = (YogaLayoutEngineContext) context.getEngineLayoutContext();
        LayoutCache cache = context.getCache();
        cache.setTracking(context.getParent() == null ? null : context.getParent().getCache());
        cache.setEngineCoordinate(engineContext.getLayoutResult());
    }

    @Override
    public YogaLayoutEngineContext createContext(LayoutContext context){
        return new YogaLayoutEngineContext(context,context.getTarget());
    }

    @Override
    public void trigger(LayoutContext context) {
        YogaLayoutEngineContext engineContext = (YogaLayoutEngineContext) context.getEngineLayoutContext();
        LayoutBox size = ((GuiDomRoot)context.getTarget()).getSourceInfo(context.getSource()).size;
        engineContext.dispatchYogaLayout(size.width,size.height);
    }
}
