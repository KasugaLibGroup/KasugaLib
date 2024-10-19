package kasuga.lib.core.client.frontend.dom.registration;

import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.SideEffectContext;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class DOMRegistryItemDynamicProxy {
    private final DomContext<?,?> context;
    private final DOMPriorityRegistry registry;
    private final ResourceLocation id;
    private boolean closed = true;
    private JavascriptContext registryContext;
    private SideEffectContext sideEffectContext;

    public DOMRegistryItemDynamicProxy(DOMPriorityRegistry registry, ResourceLocation resourceLocation, DomContext context){
        this.context = context;
        this.registry = registry;
        this.id = resourceLocation;
        this.sideEffectContext = new SideEffectContext();
    }

    public void reload(){
        this.unload();
        this.load();
    }

    public void load(){
        this.closed = false;
        Pair<DOMRegistryItem, JavascriptContext> registryItemPair = registry.get(id);
        if(registryItemPair == null){
            // @todo: print log
            return;
        }
        DOMRegistryItem item = registryItemPair.getFirst();
        JavascriptContext registryContext = registryItemPair.getSecond();
        registryContext.runTask(()->{
            JavascriptValue unload = item.render(context);
            if(unload.canExecute())
                sideEffectContext.collect(unload::executeVoid);
        });
        this.registryContext = registryContext;

        registryContext.registerTickable(context);

        context.setReady();

    }

    public void unload(){
        this.closed = true;
        context.appendTask(()->sideEffectContext.close());
        context.setNotReady();
        this.registryContext = null;
        this.sideEffectContext = null;
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

    public Optional<JavascriptContext> getContext(){
        return Optional.ofNullable(this.registryContext);
    }
}
