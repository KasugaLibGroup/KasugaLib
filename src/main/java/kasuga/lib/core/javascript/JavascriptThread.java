package kasuga.lib.core.javascript;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.javascript.loader.LoaderContext;
import kasuga.lib.core.javascript.module.ModuleLoader;
import kasuga.lib.core.javascript.module.ModuleLoaderRegistry;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class JavascriptThread extends Thread{

    protected static final Logger LOGGER = KasugaLib.createLogger("KASUGA/JAVASCRIPT");

    ModuleLoader loaderRegistry = new ModuleLoaderRegistry();
    ModuleLoaderRegistry innerLoaderRegistry = new ModuleLoaderRegistry();

    JavascriptThreadGroup threadGroup;
    Map<Object,JavascriptContext> contexts = new HashMap<>();
    final Queue<Runnable> pendingTasks = new ArrayDeque<>();
    final AtomicInteger shouldRunTicks  = new AtomicInteger(0);

    public Set<JavascriptThread> workers = new HashSet<>();

    Object bindingTarget;
    String description;
    private int workerLimit = 8;

    LoaderContext loaderContext;

    JavascriptThread(JavascriptThreadGroup threadGroup,Object target, String description){
        super("Javascript Thread - " + description);
        this.threadGroup = threadGroup;
        this.innerLoaderRegistry.register(loaderRegistry);
        this.innerLoaderRegistry.register(threadGroup.innerLoaderRegistry);
    }

    @Override
    public void run() {
        try{
            while(true){
                try{
                    if(shouldRunTicks.get() <= 0){
                        synchronized (shouldRunTicks){
                            shouldRunTicks.wait();
                        }
                    }
                    if(shouldRunTicks.getAndUpdate((x)->x>0 ? x-1 : 0) > 0){
                        this.tick();
                    }

                    if(!pendingTasks.isEmpty())
                        pendingTasks.poll().run();

                }catch (RuntimeException e){
                    LOGGER.error("An exception occurs when running the tasks",e);
                }
            }
        }catch (InterruptedException e){

        }

        for (JavascriptThread worker : this.workers) {
            worker.interrupt();
        }
        this.threadGroup.onTerminate(this);
    }

    protected void tick() {
        for (JavascriptContext context : contexts.values()) {
            context.tick();
        }
    }

    protected void dispatchTick(){
        synchronized (shouldRunTicks){
            shouldRunTicks.incrementAndGet();
            shouldRunTicks.notifyAll();
        }
    }

    public void recordCall(Runnable runnable){
        this.pendingTasks.add(runnable);
    }

    AtomicInteger workerId = new AtomicInteger();
    AtomicInteger workerCount = new AtomicInteger();


    public JavascriptThread createWorker(){
        if(workerCount.get() >= workerLimit)
            throw new IllegalStateException("Cannot create more workers: worker limits exceeded");
        JavascriptThread thread = new JavascriptThread(this.threadGroup,bindingTarget,"Worker #"+workerId.getAndIncrement());
        thread.setWorkerLimit(0);
        this.workers.add(thread);
        return thread;
    }

    private void setWorkerLimit(int workerLimit) {
        this.workerLimit = workerLimit;
    }

    public JavascriptContext createOrGetContext(Object target,String name){
        return contexts.computeIfAbsent(target,(t)->new JavascriptContext(name,this));
    }

    public LoaderContext getLoaderContext() {
        return loaderContext;
    }
}
