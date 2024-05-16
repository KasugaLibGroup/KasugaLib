package kasuga.lib.core.client.gui.components;

import kasuga.lib.core.client.gui.context.MouseEvent;
import kasuga.lib.core.client.gui.layout.yoga.YogaMeasureFunction;
import kasuga.lib.core.client.gui.layout.yoga.YogaNode;
import kasuga.lib.core.client.gui.layout.yoga.YogaNodeType;
import kasuga.lib.core.client.gui.style.Style;
import kasuga.lib.core.client.gui.style.StyleList;
import kasuga.lib.core.client.gui.style.StyleType;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MultipleLocator {
    private final YogaMeasureFunction measureFunction;
    MultipleLocator parent;
    Map<Object,YogaNode> sourceLocators = new HashMap<>();
    List<MultipleLocator> children = new ArrayList<>();
    Map<YogaNode,CalculatedPositionCache> caches = new HashMap<>();
    Map<StyleType<?>, BiConsumer<YogaNode,CalculatedPositionCache>> locationStyles = new HashMap<>();
    private YogaNodeType nodeType = null;

    MultipleLocator(YogaMeasureFunction measureFunction){
        this.measureFunction = measureFunction;
    }

    public void addChildrenAt(int index,MultipleLocator child){
        children.add(index,child);
        child.parent = this;
        for (Object object : sourceLocators.keySet()) {
            if(!child.sourceLocators.containsKey(object)){
                child.addSource(object);
            }
        }
        for (Map.Entry<Object, YogaNode> source : this.sourceLocators.entrySet()) {
            source.getValue().addChildAt(index,child.sourceLocators.get(source.getKey()));

        }
    }

    public void removeChildAt(int index){
        MultipleLocator child = children.remove(index);
        child.parent = null;
        for (Object object : sourceLocators.keySet()) {
            child.removeSource(object);
        }
        for (Map.Entry<Object, YogaNode> source : this.sourceLocators.entrySet()) {
            source.getValue().removeChildAt(index);
        }
    }

    public void addSource(Object source){
        YogaNode node = YogaNode.create();
        if(measureFunction != null)
            node.setMeasureFunction(measureFunction);
        this.sourceLocators.put(source,node);
        if(this.nodeType != null)
            node.setNodeType(nodeType);
        CalculatedPositionCache positionCache = new CalculatedPositionCache();
        this.caches.put(node,positionCache);
        for (BiConsumer<YogaNode,CalculatedPositionCache> value : this.locationStyles.values()) {
            value.accept(node,positionCache);
        }
        for (int i=0;i<children.size();i++) {
            MultipleLocator child = children.get(i);
            child.addSource(source);
            node.addChildAt(i,child.sourceLocators.get(source));
        }
    }

    public void removeSource(Object source){
        YogaNode node = this.sourceLocators.remove(source);
        if(node != null){
            this.caches.remove(node);
            node.close();
        }
        for (MultipleLocator children : children) {
            children.removeSource(source);
        }
    }

    public void close(){
        for (Object object : this.sourceLocators.keySet()) {
            this.removeSource(object);
        }
    }

    public MouseEvent transformMouseEvent(MouseEvent event){
        CalculatedPositionCache positionCache = this.caches.get(this.sourceLocators.get(event.source()));
        if(positionCache == null){
            return null;
        }
        MouseEvent newEvent = new MouseEvent(
                event.mouseX() - positionCache.x,
                event.mouseY() - positionCache.y,
                event.button(),event.source());
        if(newEvent.mouseX() < 0 ||
                newEvent.mouseY() < 0 ||
                newEvent.mouseX() > positionCache.width ||
                newEvent.mouseY() > positionCache.height
        ){
            return null;
        }
        return newEvent;
    }

    public CalculatedPositionCache getPosition(Object source) {
        return caches.get(sourceLocators.get(source));
    }

    public void markDirty(){
        for (YogaNode value : this.sourceLocators.values()) {
            value.dirty();
        }
    }

    public boolean hasNewLayout(Object source) {
        return this.sourceLocators.containsKey(source) && this.sourceLocators.get(source).hasNewLayout();
    }


    public void visited(Object source) {
        YogaNode node = this.sourceLocators.get(source);
        if(node!=null)
            node.visited();
    }

    public void markCacheDirty(Object source) {
        this.caches.get(this.sourceLocators.get(source)).markDirty();
    }

    public void markAllCacheDirty(){
        for (CalculatedPositionCache value : this.caches.values()) {
            value.markDirty();
        }
    }

    public void updateCache(Object target) {
        CalculatedPositionCache cache = this.caches.get(this.sourceLocators.get(target));
        if(cache != null)
            cache.attemptUpdate(this.parent == null ? null : this.parent.caches.get(this.parent.sourceLocators.get(target)),this.sourceLocators.get(target));
    }

    public void calculateLayout(Object source,float width, float height) {
        this.sourceLocators.get(source).calculateLayout(width, height);
    }

    public void applyStyleInstant(StyleType<?> styleType, Style<?> style) {
        this.locationStyles.put(styleType, (node, cache) -> {
            style.apply(node);
            cache.markDirty();
        });

        this.sourceLocators.forEach((k,v)->{
            style.apply(v);
            this.caches.get(v).markDirty();
        });
    }

    public void setNodeType(YogaNodeType yogaNodeType) {
        this.nodeType = yogaNodeType;
        this.sourceLocators.forEach((k,v)->{
            v.setNodeType(yogaNodeType);
            this.caches.get(v).markDirty();
        });
    }

    public boolean alive(Object target) {
        return this.sourceLocators.containsKey(target);
    }
}
