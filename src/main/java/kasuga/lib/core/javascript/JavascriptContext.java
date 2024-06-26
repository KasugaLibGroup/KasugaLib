package kasuga.lib.core.javascript;

import kasuga.lib.core.javascript.loader.LoaderContext;
import kasuga.lib.core.javascript.module.ModuleLoadException;
import kasuga.lib.core.javascript.module.ModuleLoaderRegistry;
import kasuga.lib.core.javascript.module.RequireFunction;
import kasuga.lib.core.javascript.module.Tickable;
import kasuga.lib.core.util.Callback;
import org.graalvm.polyglot.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JavascriptContext {
    Context context;
    Engine engine;
    ModuleLoaderRegistry internalRegistry = new ModuleLoaderRegistry();
    ModuleLoaderRegistry moduleLoaderRegistry = new ModuleLoaderRegistry();

    JavascriptThread thread;

    LoaderContext loaderContext = new LoaderContext();

    String name;

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

        internalRegistry.register(thread.innerLoaderRegistry);
        internalRegistry.register(moduleLoaderRegistry);
        Value value = context.getBindings("js");
        value.putMember("__KASUGA_REQUIRE__", requireFn);
        this.run(BootstrapSources.BOOTSTRAP);
    }

    protected Value require(String moduleId){
        Optional<Value> requireResult = internalRegistry.load(context, moduleId, requireFn, this);
        if(requireResult.isEmpty()){
            throw new ModuleLoadException(moduleId, "failed to locate module.");
        }
        return requireResult.get();
    }

    Value requireFn = Value.asValue((RequireFunction)this::require);

    Map<String,Object> nativeModules = new HashMap<>();

    Set<Callback> sideEffects = new HashSet<>();
    Set<Tickable> tickableModules = new HashSet<>();

    public Value createNativeModule(String name, Function<JavascriptContext,Object> constructor){
        if(nativeModules.containsKey(name))
            return Value.asValue(nativeModules.get(name));
        Object nativeModule = constructor.apply(this);
        nativeModules.put(name, nativeModule);
        if(nativeModule instanceof Tickable tickable){
            tickableModules.add(tickable);
        }
        return Value.asValue(nativeModule);
    }

    public void run(String source) throws IOException {
        run(Source.newBuilder("js",source,"<eval code>").build());
    }

    public void run(Source source){
        if(Thread.currentThread() != thread){
            thread.recordCall(()->run(source));
            return;
        }
        this.context.eval(source);
    }

    public void run(Supplier<Source> sourceSupplier){
        if(Thread.currentThread() != thread){
            thread.recordCall(()->run(sourceSupplier.get()));
            return;
        }
        this.context.eval(sourceSupplier.get());
    }

    public void require(String moduleId, Consumer<Value> callback){
        if(Thread.currentThread() != thread){
            thread.recordCall(()-> callback.accept(require(moduleId)));
            return;
        }
        callback.accept(require(moduleId));
    }

    public void requireExternal(String moduleId){
        this.require(moduleId,(r)->{});
    }

    public JavascriptContext createWorker(){
        JavascriptThread thread = this.thread.createWorker();
        JavascriptContext workerContext = thread.createOrGetContext(thread,"Worker #");
        workerContext.run(BootstrapSources.WORKER.get());
        return workerContext;
    }

    public void createWorker(Source source){
       createWorker().run(source);
    }

    public void createWorker(String moduleId){
        createWorker().requireExternal(moduleId);
    }

    public void tick() {
        for (Tickable tickableModule : this.tickableModules) {
            tickableModule.tick();
        }
    }

    public void close(){
        for (Object module : nativeModules.values()) {
            if(module instanceof Closeable){
                try {
                    ((Closeable) module).close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (Callback sideEffect : sideEffects) {
            try {
                sideEffect.execute();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public LoaderContext getLoaderContext() {
        return loaderContext;
    }

    public void runTask(Callback callback) {
        this.thread.recordCall(callback::execute);
    }

    public Callback collectEffect(Callback dispose){
        Callback[] wrappedList = new Callback[1];
        Callback wrapped = ()->{
            sideEffects.remove(wrappedList[0]);
            dispose.execute();
        };
        wrappedList[0] = wrapped;
        this.sideEffects.add(wrapped);
        return wrapped;
    }

    public void addTickable(Tickable t) {
        this.tickableModules.add(t);
    }

    public void removeTickable(Tickable t) {
        this.tickableModules.remove(t);
    }

    public HashMap<String, Object> getEnvironment() {
        return environment;
    }
}
