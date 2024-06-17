package kasuga.lib.core.client.frontend.dom.registration;

import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.util.Callback;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

public class DOMRegistryItemDynamicProxy {
    private final DomContext<?,?> context;
    private final DOMPriorityRegistry registry;
    private final ResourceLocation id;
    Callback unloadHooks;
    private boolean closed = true;

    public DOMRegistryItemDynamicProxy(DOMPriorityRegistry registry, ResourceLocation resourceLocation, DomContext context){
        this.context = context;
        this.registry = registry;
        this.id = resourceLocation;
    }

    public void reload(){
        this.unload();
        this.load();
    }

    public void load(){
        this.closed = false;
        Pair<DOMRegistryItem,JavascriptContext> registryItemPair = registry.get(id);
        DOMRegistryItem item = registryItemPair.getFirst();
        JavascriptContext registryContext = registryItemPair.getSecond();
        if(item == null){
            // @todo: print log
            return;
        }
        DOMRegistryItemDynamicProxy that = this;
        registryContext.runTask(()->{
            Value unload = item.render(context);
            that.updateUnloadHooks(()->{
                registryContext.runTask(unload::executeVoid);
            });
        });
    }

    private void updateUnloadHooks(Callback unloadHook) {
        if(closed)
            unloadHook.execute();
        else this.unloadHooks = unloadHook;
    }

    public void unload(){
        this.closed = true;
        if(this.unloadHooks != null){
            try{
                unloadHooks.execute();
            }catch (PolyglotException e){
                // @todo: print log
            }
            this.unloadHooks = null;
        }
    }

    public void enable() {

    }

    public void disable() {

    }

    public void notifyUpdate(ResourceLocation location) {
        if(this.id == location){
            this.reload();
        }
    }
}
