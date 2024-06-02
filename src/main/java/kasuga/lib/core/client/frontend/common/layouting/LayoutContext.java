package kasuga.lib.core.client.frontend.common.layouting;

import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;

import java.util.HashMap;
import java.util.Map;

public class LayoutContext {

    protected final Object source;

    protected LayoutEngine<?> lastEngine;

    protected final LayoutCache cache;

    protected final GuiDomNode target;

    protected EngineLayoutContext engineLayoutContext;
    protected LayoutContext parent;

    public LayoutContext(Object source,GuiDomNode node) {
        this.target = node;
        this.source = source;
        this.cache = new LayoutCache();
    }

    public void apply(LayoutEngine<?> engine){
        if(lastEngine != engine){
            this.close();
            lastEngine = engine;
            engineLayoutContext = engine.createContext(this);
        }
        engine.apply(this);
    }

    public boolean layout(){
        if(!engineLayoutContext.hasNewLayout())
            return false;
        lastEngine.layout(this);
        return true;
    }

    public void trigger(){
        lastEngine.trigger(this);
    }

    public void close() {
        if(engineLayoutContext != null){
            engineLayoutContext.close();
            engineLayoutContext = null;
        }
    }

    public GuiDomNode getTarget() {
        return target;
    }

    public LayoutContext getParent(){
        return parent;
    }

    public void setParent(LayoutContext parent) {
        this.parent = parent;
    }

    public LayoutCache getCache() {
        return cache;
    }

    public EngineLayoutContext getEngineLayoutContext() {
        return engineLayoutContext;
    }

    public Object getSource() {
        return source;
    }
}
