package kasuga.lib.core.client.frontend.dom;

import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.client.frontend.dom.registration.DOMRegistryItemDynamicProxy;
import kasuga.lib.core.javascript.Tickable;
import net.minecraft.resources.ResourceLocation;
import org.graalvm.polyglot.HostAccess;

public abstract class DomContext<P extends DomNode<?>,T extends P> implements Tickable {

    protected boolean ready = false;

    T rootNode;
    DOMRegistryItemDynamicProxy renderer;

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

    }

    public void setReady() {
        ready = true;
    }

    public void setNotReady() {
        ready = false;
    }
}
