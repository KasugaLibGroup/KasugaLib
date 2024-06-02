package kasuga.lib.core.client.frontend.dom.nodes;

import kasuga.lib.core.client.frontend.dom.attribute.AttributeMap;
import kasuga.lib.core.client.frontend.dom.event.EventEmitter;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.javascript.JavascriptContext;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class DomNode {

    JavascriptContext context;

    public DomNode parent;
    public List<DomNode> children = new ArrayList<>();

    protected AttributeMap attributes = new AttributeMap();

    public boolean addChildAt(int i, DomNode child){
        if(child.parent != null)
            return false;
        if(children.contains(child))
            return false;
        children.add(i,child);
        child.parent = this;
        return true;
    }

    public boolean addChild(DomNode child){
        return addChildAt(children.size(),child);
    }

    public boolean removeChild(DomNode child){
        boolean resp = children.remove(child);
        if(resp)
            child.parent = null;
        return resp;
    }

    public void clear(){
        for (DomNode child : children) {
            child.parent = null;
        }
        children.clear();
    }

    protected EventEmitter emitter = new EventEmitter();


    HashMap<Value, Consumer<Value>> callbacks = new HashMap<>();

    private Consumer<Value> wrapCallback(Value callback) {
        return callbacks.computeIfAbsent(callback, (c)-> (e) -> context.runTask(()->callback.executeVoid(e)));
    }

    @HostAccess.Export
    public void addEventListener(String eventName, Value callback){
        emitter.subscribe(eventName, wrapCallback(callback));
    }

    @HostAccess.Export
    public void removeEventListener(String eventName, Value callback){
        emitter.unsubscribe(eventName, wrapCallback(callback));
    }

    @HostAccess.Export
    public void dispatchEvent(String eventName,Value event){
        emitter.dispatchEvent(eventName,event);
        parent.dispatchEvent(eventName, event);
    }

    public String getAttribute(String attributeName){
        return this.attributes.get(attributeName);
    }

    public void setAttribute(String attributeName, String value){
        this.attributes.set(attributeName, value);
    }

    public void render(RenderContext context){
        for (DomNode child : this.children) {
            child.render(context);
        }
    }
}
