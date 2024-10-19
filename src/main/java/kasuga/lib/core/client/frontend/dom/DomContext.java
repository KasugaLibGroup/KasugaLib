package kasuga.lib.core.client.frontend.dom;

import kasuga.lib.core.client.frontend.dom.event.EventEmitter;
import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.client.frontend.dom.registration.DOMRegistryItemDynamicProxy;
import kasuga.lib.core.javascript.Tickable;
import kasuga.lib.core.javascript.engine.HostAccess;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.util.Callback;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayDeque;

public abstract class DomContext<P extends DomNode<?>,T extends P> implements Tickable {

    protected boolean ready = false;

    T rootNode;
    DOMRegistryItemDynamicProxy renderer;
    protected ArrayDeque<Callback> queue = new ArrayDeque<>();

    public void appendTask(Callback callback) {
        this.queue.add(callback);
    }


    protected DomContext(DOMPriorityRegistry registry, ResourceLocation location){
        rootNode = createRoot();
        renderer = new DOMRegistryItemDynamicProxy(registry, location, this);
    }

    protected abstract T createRoot();

    public abstract P createNodeInternal(String name);

    @HostAccess.Export
    public T getRootNode() {
        return rootNode;
    }

    @HostAccess.Export
    public P createNode(String name){
        return createNodeInternal(name);
    }

    public void start(){
        renderer.load();
        renderer.enable();
    }

    public void stop(){
        renderer.disable();
        renderer.unload();
    }

    public DOMRegistryItemDynamicProxy getRenderer() {
        return renderer;
    }

    public void tick(){
        int taskNumebr = 0;
        while(!queue.isEmpty() && (++taskNumebr)<64){
            queue.poll().execute();
        }
    }

    public void setReady() {
        ready = true;
    }

    public void setNotReady() {
        ready = false;
    }

    EventEmitter emitter = new EventEmitter();

    @HostAccess.Export
    public void addEventListener(String eventName, JavascriptValue callback){
        callback.pin();
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
}
