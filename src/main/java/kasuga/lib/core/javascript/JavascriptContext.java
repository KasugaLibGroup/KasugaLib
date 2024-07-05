package kasuga.lib.core.javascript;

import kasuga.lib.core.javascript.module.JavascriptModule;
import kasuga.lib.core.javascript.module.JavascriptModuleScope;
import kasuga.lib.core.javascript.module.ModuleLoadException;
import kasuga.lib.core.javascript.module.node.JavascriptNodeModule;
import kasuga.lib.core.util.Callback;
import net.minecraft.Util;
import org.graalvm.polyglot.*;

import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.function.Function;
import java.util.function.Supplier;

public class JavascriptContext {
    Context context;
    Engine engine;
    JavascriptThread thread;
    String name;
    ContextModuleLoader moduleLoader;

    public JavascriptModule rootModule = new JavascriptModule(this);
    SideEffectContext effect = new SideEffectContext();

    HashMap<String,Object> environment = new HashMap<>();

    JavascriptContext(String name, JavascriptThread thread){
        this.thread = thread;
        this.name = name;
        String path = java.util.UUID.randomUUID().toString();
        engine = Engine.newBuilder("js")
                .option("inspect", "localhost:4242")
                .option("inspect.Path", path)
                .option("inspect.Suspend","false")
                .build();


        context = Context.newBuilder()
                .allowHostAccess(HostAccess.SCOPED)
                .engine(engine)
                .build();

        moduleLoader = new ContextModuleLoader(thread.getModuleLoader());
    }

    public FutureTask<Value> execute(Supplier<Source> sourceSupplier){
        FutureTask<Value> task = new FutureTask<>(()->{
            return context.eval(sourceSupplier.get());
        });
        this.thread.recordCall(task);
        return task;
    }

    public Value execute(Source source){
        return context.eval(source);
    }

    public Value eval(String code){
        return context.eval("js", code);
    }

    public Function<JavascriptModule, Function<String,Value>> requireFunction = Util.memoize(
            (source) ->
                    (target) ->
                            this.moduleLoader.load(source, target)
                                    .orElseThrow(()->
                                            new ModuleLoadException(target, "failed to locate module.")
                                    ).get()
    );

    public RequireFunction getRequireFunction(JavascriptModule source) {
        return requireFunction.apply(source)::apply;
    }

    public RequireFunction getRequireFunction(){
        return getRequireFunction(rootModule);
    }

    public JavascriptModuleScope getScope() {
        return moduleLoader.scope;
    }

    public void close(){
        effect.close();
    }

    Set<Tickable> tickables = new HashSet<>();

    public void tick(){
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

    public Optional<JavascriptModule> requireModule(String name){
        return moduleLoader.load(rootModule, name);
    }
}
