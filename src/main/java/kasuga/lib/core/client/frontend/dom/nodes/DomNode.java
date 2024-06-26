package kasuga.lib.core.client.frontend.dom.nodes;

import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.client.frontend.dom.attribute.AttributeMap;
import kasuga.lib.core.client.frontend.dom.event.EventEmitter;
import kasuga.lib.core.client.frontend.gui.GuiContext;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.javascript.JavascriptContext;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class DomNode<T extends DomContext<?,?>> {

    protected final T domContext;
    JavascriptContext context;

    public DomNode<T> parent;
    public List<DomNode<T>> children = new ArrayList<>();

    protected AttributeMap attributes = new AttributeMap();

    public DomNode(T context) {
        this.domContext = context;
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
        for (DomNode<T> child : children) {
            child.parent = null;
        }
        children.clear();
    }

    protected EventEmitter emitter = new EventEmitter();


    HashMap<Value, Consumer<Value>> callbacks = new HashMap<>();

    @HostAccess.Export
    public void addEventListener(String eventName, Value callback){
        callback.pin();
        emitter.subscribe(eventName, callback);
    }

    @HostAccess.Export
    public void removeEventListener(String eventName, Value callback){
        emitter.unsubscribe(eventName, callback);
    }

    @HostAccess.Export
    public void dispatchEvent(String eventName,Value event){
        emitter.dispatchEvent(eventName,event);
    }

    @HostAccess.Export
    public String getAttribute(String attributeName){
        return this.attributes.get(attributeName);
    }

    public void setAttribute(String attributeName, String value){
        this.attributes.set(attributeName, value);
    }

    @HostAccess.Export
    public void setAttribute(String attributeName, Value value){
        setAttribute(attributeName,value.asString());
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
}
