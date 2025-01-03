package kasuga.lib.core.client.frontend.dom.nodes;

import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.client.frontend.dom.attribute.AttributeMap;
import kasuga.lib.core.client.frontend.dom.event.EventEmitter;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;
import kasuga.lib.core.javascript.engine.JavascriptValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DomNode<T extends DomContext<?,?>> {

    protected final T domContext;
    JavascriptContext context;

    public DomNode<T> parent;
    public List<DomNode<T>> children = new CopyOnWriteArrayList<>();

    protected AttributeMap attributes = new AttributeMap();

    public DomNode(T context) {
        this.domContext = context;
    }

    public boolean addChildBefore(DomNode<T> child, DomNode<T> before){
        int index = children.indexOf(before);
        if(index == -1)
            return false;
        return addChildAt(index,child);
    }

    public boolean addChildAt(int i, DomNode<T> child){
        if(child.parent != null)
            return false;
        if(children.contains(child))
            return false;
        children.add(i,child);
        child.parent = this;
        return true;
    }

    @HostAccess.Export
    public boolean addChild(DomNode<T> child){
        return addChildAt(children.size(),child);
    }

    @HostAccess.Export
    public boolean removeChild(DomNode<T> child){
        boolean resp = children.remove(child);
        if(resp)
            child.parent = null;
        return resp;
    }

    public void clear(){
        List<DomNode<T>> childrenCopy = new ArrayList<>(children);
        for (DomNode<T> child : childrenCopy) {
            removeChild(child);
        }
    }

    protected EventEmitter emitter = new EventEmitter();


    @HostAccess.Export
    public void addEventListener(String eventName, JavascriptValue callback){
        emitter.subscribe(eventName, callback);
    }

    @HostAccess.Export
    public void removeEventListener(String eventName, JavascriptValue callback){
        emitter.unsubscribe(eventName, callback);
    }

    @HostAccess.Export
    public void dispatchEvent(String eventName,Object event){
        emitter.dispatchEvent(eventName,event);
    }

    @HostAccess.Export
    public String getAttribute(String attributeName){
        return this.attributes.get(attributeName);
    }

    @HostAccess.Export
    public void setAttribute(String attributeName, String value){
        this.attributes.set(attributeName, value);
    }


    public void render(Object source,RenderContext context){
        for (DomNode<T> child : this.children) {
            child.render(source,context);
        }
    }

    public T getDomContext() {
        return domContext;
    }

    public void close(){

    }

    @HostAccess.Export
    public boolean hasFeature(String feature){
        return false;
    }

    public void dispatchRenderTick() {
        renderTick();
        for (DomNode<T> child : this.children) {
            child.dispatchRenderTick();
        }
    }

    protected void renderTick() {}
}
