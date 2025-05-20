package kasuga.lib.core.client.frontend.common.layouting;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.util.Envs;
import net.minecraftforge.fml.loading.FMLEnvironment;

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
        for (int i = 0; i < children.size(); i++) {
            currNode.addChild(i,children.get(i).addSource(source));
        }
        return currNode;
    }

    public LayoutNode removeSource(Object source){
        LayoutNode node = this.sources.remove(source);
        if(node == null){
            if(Envs.isDevEnvironment()){
                KasugaLib.MAIN_LOGGER.error("Source "+source.toString()+ "has not found in layout context, it may caused by multithread problems. If this still happends, please check.");
            }
        }
        for (LayoutContext<?,N> child : children) {
            node.removeChild(child.removeSource(source));
        }
        node.close();
        return node;
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
            child.removeSource(entry.getKey());
        }
    }

    public void dispatchApply(){
        boolean flag = false;
        for (LayoutNode node : this.sources.values()) {
            flag |= node.applyChanges();
        }

        if(!flag)
            return;

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

    public boolean hasSource(Object source) {
        if(Envs.isDevEnvironment() && !this.sources.containsKey(source)){
            KasugaLib.MAIN_LOGGER.error("Source "+source.toString()+ "has not found in layout context "+this +", it may caused by multithread problems. If this still happends, please check.");
        }
        return this.sources.containsKey(source);
    }

    public LayoutEngine<T, N> getLayoutEngine() {
        return engine;
    }
}
