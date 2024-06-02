package kasuga.lib.core.client.frontend.common.layouting;

import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;

import java.util.*;

public class LayoutContextManager {

    private final GuiDomNode node;

    public LayoutContextManager(GuiDomNode node){
        this.node = node;
    }

    public LayoutContextManager parent;
    public Map<Object, LayoutContext> contexts = new HashMap<>();

    public List<LayoutContextManager> children = new ArrayList<>();

    public void addSource(Object source){
        if(!contexts.containsKey(source)){
            LayoutContext context = new LayoutContext(source, node);
            contexts.put(source,context);
        }
        for (LayoutContextManager child : children) {
            child.addSource(source);
        }
    }

    public void removeSource(Object source){
        LayoutContext context = contexts.remove(source);
        if(context != null){
            context.close();
        }
        for (LayoutContextManager child : children) {
            child.removeSource(source);
        }
    }

    public void addChildren(LayoutContextManager child){
        children.add(child);
        child.parent = this;
        for (Map.Entry<Object, LayoutContext> entry : contexts.entrySet()) {
            child.addSource(entry.getKey());
        }
        child.setParent(this);
    }

    public void removeChildren(LayoutContextManager child){
        children.remove(child);
        for (Map.Entry<Object, LayoutContext> entry : contexts.entrySet()) {
            child.removeSource(entry.getKey());
        }
        child.setParent(null);
    }

    protected void setParent(LayoutContextManager manager) {
        this.parent = manager;
        for (Map.Entry<Object, LayoutContext> entry : contexts.entrySet()) {
            Object source = entry.getKey();
            LayoutContext context = entry.getValue();
            if(manager == null)
                context.setParent(null);
            else
                context.setParent(manager.getContextOrThrow(source));
        }
    }

    public LayoutContext getContextOrThrow(Object source) {
        if(!this.contexts.containsKey(source))
            throw new IllegalStateException("Context not exists: "+source.toString());
        return this.contexts.get(source);
    }

    public void executeLayout(Object source, LayoutEngine<?> engine){
        LayoutContext context = this.contexts.get(source);
        apply(source,engine);
        context.trigger();
        layout(source);
    }

    private void apply(Object source, LayoutEngine<?> engine) {
        LayoutContext context = contexts.get(source);
        context.apply(engine);
        for (LayoutContextManager child : this.children) {
            child.apply(source,engine);
        }
    }

    protected void layout(Object source){
        LayoutContext context = contexts.get(source);
        if(!context.layout())
            return;
        for (LayoutContextManager child : this.children) {
            child.layout(source);
        }
    }
}
