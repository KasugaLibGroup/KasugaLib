package kasuga.lib.core.javascript;

import kasuga.lib.core.client.animation.neo_neo.base.Movement;
import kasuga.lib.core.client.frontend.commands.MetroModuleInfo;
import kasuga.lib.core.javascript.engine.*;
import kasuga.lib.core.util.Callback;
import net.minecraft.Util;
import org.mozilla.javascript.commonjs.module.ModuleScope;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.function.Function;
import java.util.function.Supplier;

public class JavascriptContext {
    JavascriptThread thread;
    JavascriptEngineContext context;
    String name;
    SideEffectContext effect = new SideEffectContext();

    HashMap<String,Object> environment = new HashMap<>();

    ContextModuleLoader contextModuleLoader;

    JavascriptContext(String name, JavascriptThread thread){
        this.thread = thread;
        this.name = name;
        String path = java.util.UUID.randomUUID().toString();
        contextModuleLoader = new ContextModuleLoader(thread.contextModuleLoader);
        context = thread.scriptEngine.createInstance(this);
        context.loadModule("@kasugalib/core");
    }

    public void close(){
        effect.close();
    }

    Set<Tickable> tickables = new HashSet<>();

    Queue<CompletableFuture> futures = new ArrayDeque<>();

    public void tick(){
        if(!this.futures.isEmpty()){
            beforeRenderTick();
            CompletableFuture future;
            while((future = this.futures.poll()) != null) future.complete(null);
        }
        tickables.forEach(Tickable::tick);
    }

    public Callback registerTickable(Tickable tickable){
        if(tickables.contains(tickable))
            return Callback.nop();
        return effect.effect(()->{
            tickables.add(tickable);
            return ()->tickables.remove(tickable);
        });
    }

    public Callback collectEffect(Callback callback){
        return effect.collect(callback);
    }

    public HashMap<String, Object> getEnvironment() {
        return environment;
    }

    public Callback runTask(Callback task) {
        return effect.effect(()->{
            Runnable finalTask = task::execute;
            thread.recordCall(finalTask);
            return ()->{
                thread.revokeCall(finalTask);
            };
        });
    }

    Queue<Runnable> afterRenderTickTasks = new ArrayDeque<>(32);

    public void beforeRenderTick(){
        Runnable task;
        while((task = afterRenderTickTasks.poll()) != null){
            task.run();
        }
    }

    public CompletableFuture dispatchBeforeRenderTick(){
        CompletableFuture future = new CompletableFuture();
        this.futures.add(future);
        return future;
    }


    public void enqueueAfterRenderTask(Runnable runnable) {
        this.afterRenderTickTasks.add(runnable);
    }

    public JavascriptEngineContext getRuntimeContext() {
        return this.context;
    }

    public void loadModuleVoid(String name){
        this.context.loadModule(name);
    }

    public JavascriptModuleScope getModuleScope() {
        return this.contextModuleLoader.getScope();
    }

    public JavascriptModuleLoader getModuleLoader() {
        return this.contextModuleLoader.getLoader();
    }
}
