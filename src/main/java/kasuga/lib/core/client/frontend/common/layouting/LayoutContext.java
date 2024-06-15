package kasuga.lib.core.client.frontend.common.layouting;

import kasuga.lib.core.client.frontend.dom.nodes.DomNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LayoutContext<T extends LayoutNode,N extends DomNode> {
    protected final LayoutEngine<T,N> engine;
    protected final HashMap<Object,LayoutNode> sources;
    protected final ArrayList<LayoutContext<?,N>> children;

    protected final N node;

    public LayoutContext(N node, LayoutEngine<T, N> engine){
        this.node = node;
        this.engine = engine;
        this.sources = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public T addSource(Object source){
        T currNode = engine.createNode(node,source);
        this.sources.put(source, currNode);
        for (LayoutContext<?,N> child : children) {
            child.addSource(source);
        }
        return currNode;
    }

    public void removeSource(Object source){
        LayoutNode node = this.sources.remove(source);
        for (LayoutContext<?,N> child : children) {
            child.removeSource(source);
        }
        node.close();
    }

    public void addChild(int index, LayoutContext<?,N> child){
        for (Map.Entry<Object, LayoutNode> entry : this.sources.entrySet()) {
            entry.getValue().addChild(index,child.addSource(entry.getKey()));
        }
        this.children.add(index,child);
    }

    public void removeChild(LayoutContext<?,N> child){
        int index = children.indexOf(child);
        if(index == -1)
            return;
        removeChild(index);
    }

    public void removeChild(int index){
        LayoutContext<?,N> child = this.children.remove(index);
        for (Map.Entry<Object,LayoutNode> entry: this.sources.entrySet()) {
            entry.getValue().removeChild(child.sources.get(entry.getKey()));
            entry.getValue().removeChild(index);
            child.removeSource(entry.getKey());
        }
    }

    public void dispatchApply(){
        for (LayoutNode node : this.sources.values()) {
            node.applyChanges();
        }

        for (LayoutContext<?,N> child : this.children) {
            child.dispatchApply();
        }
    }

    public void dispatchUpdate(){
        boolean updated = false;
        for (LayoutNode node : this.sources.values()) {
            updated |= node.update();
        }

        if(!updated)
            return;

        for (LayoutContext<?,N> child : this.children) {
            child.dispatchUpdate();
        }
    }

    public void runCalculate(){
        for (LayoutNode node : this.sources.values()) {
            node.calculate();
        }
    }

    public void close(){
        for (LayoutNode node : this.sources.values()) {
            node.close();
        }
    }

    public void markDirty(){
        for (LayoutNode node : this.sources.values()) {
            node.markDirty();
        }
    }

    public void tick() {
        this.dispatchApply();
        this.runCalculate();
        this.dispatchUpdate();
    }

    public LayoutNode getSourceNode(Object source) {
        if(!this.sources.containsKey(source))
            throw new IllegalStateException("Source not found");
        return this.sources.get(source);
    }
}
