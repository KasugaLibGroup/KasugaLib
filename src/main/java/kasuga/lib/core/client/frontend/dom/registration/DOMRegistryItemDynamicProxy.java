package kasuga.lib.core.client.frontend.dom.registration;

import kasuga.lib.core.client.animation.neo_neo.key_frame.KeyFrameHolder;
import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.SideEffectContext;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.util.Callback;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

public class DOMRegistryItemDynamicProxy {
    private final DomContext<?,?> context;
    private final DOMPriorityRegistry registry;
    private final ResourceLocation id;
    private boolean closed = true;
    private JavascriptContext registryContext;
    private SideEffectContext sideEffectContext;
    private HashSet<Callback> pendingTasks = new HashSet<>();

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
        if(this.sideEffectContext != null)
            this.sideEffectContext.close();
        this.sideEffectContext = new SideEffectContext();
        this.closed = false;
        Pair<DOMRegistryItem, JavascriptContext> registryItemPair = registry.get(id);
        if(registryItemPair == null){
            // @todo: print log
            return;
        }
        DOMRegistryItem item = registryItemPair.getFirst();
        JavascriptContext registryContext = registryItemPair.getSecond();
        this.pendingTasks.add(registryContext.runTask(()->{
            JavascriptValue unload = item.render(context);
            if(unload.canExecute())
                sideEffectContext.collect(unload::executeVoid);
        }));
        this.registryContext = registryContext;
        sideEffectContext.collect(registryContext.registerTickable(context));

        context.setReady();

    }

    public void unload(){
        if(this.closed)
            return;
        this.context.getRootNode().clear();
        this.closed = true;
        SideEffectContext _closingContext = this.sideEffectContext;
        for (Callback pendingTask : this.pendingTasks) {
            pendingTask.execute(); // Cancel pending tasks
        }
        pendingTasks.clear();
        context.appendTask(()->{
            _closingContext.close();
        });
        context.setNotReady();
        this.sideEffectContext = null;
        this.registryContext = null;
    }

    public void enable() {

    }

    public void disable() {

    }

    public void notifyUpdate(ResourceLocation location) {
        if(Objects.equals(this.id, location)){
            this.reload();
        }
    }

    public Optional<JavascriptContext> getContext(){
        return Optional.ofNullable(this.registryContext);
    }
}
